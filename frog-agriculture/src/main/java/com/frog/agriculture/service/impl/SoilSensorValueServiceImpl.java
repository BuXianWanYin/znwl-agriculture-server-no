package com.frog.agriculture.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.mapper.DeviceMapper;
import com.frog.agriculture.domain.FishWaterQuality;
import com.frog.agriculture.mapper.FishWaterQualityMapper;
import com.frog.common.utils.DeviceStatusTracker;
import com.frog.common.utils.SerialPortUtil;
import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.frog.agriculture.domain.SoilSensorValue;
import com.frog.agriculture.mapper.SoilSensorValueMapper;
import com.frog.agriculture.service.ISoilSensorValueService;

/**
 * 土壤环境数据Service业务层处理类
 * 整合传感器数据的采集、解析、入库等功能
 *
 * @author buxianwanyin
 * @date 2025-02-23
 */
@Service
public class SoilSensorValueServiceImpl implements ISoilSensorValueService {

    // 注入土壤环境数据Mapper
    @Autowired
    private SoilSensorValueMapper soilSensorValueMapper;

    // 注入水质数据Mapper
    @Autowired
    private FishWaterQualityMapper fishWaterQualityMapper;

    // 注入设备Mapper
    @Autowired
    private DeviceMapper deviceMapper;

    // 串口工具实例，用于传感器数据的收发 操作
    private SerialPortUtil serialPortUtil;

    // 日志对象
    private static final Log log = LogFactory.getLog(SoilSensorValueServiceImpl.class);

    // 全局存储各传感器最新数据，key为传感器特定名称
    private ConcurrentHashMap<String, Map<String, Object>> globalSensorData = new ConcurrentHashMap<>();

    // 保存各传感器对应的指令，key为传感器ID，value为十六进制指令字符串
    private Map<String, String> sensorCommands = new HashMap<>();

    // 设备状态跟踪工具对象, 用于记录设备在线或离线状态
    private DeviceStatusTracker statusTracker = new DeviceStatusTracker();
    // 设备缓存，key为传感器ID，避免重复查询
    private Map<String, Device> deviceCache = new ConcurrentHashMap<>();

    /**
     * 初始化方法，系统启动时调用
     */
    @PostConstruct
    public void init() {
        // 初始化串口工具
        serialPortUtil = new SerialPortUtil();
        // 从数据库中加载传感器指令
        initializeSensorCommandsFromDB();
    }

    /**
     * 从数据库加载传感器指令
     * 查询所有有效的传感器设备（sensorType和sensor_command不为空）
     */
    private void initializeSensorCommandsFromDB() {
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("sensorType"); // 传感器类型不能为空
        queryWrapper.isNotNull("sensor_command"); // 传感器指令不能为空
        List<Device> devices = deviceMapper.selectList(queryWrapper);

        // 遍历设备列表，建立传感器类型与指令的映射
        for (Device device : devices) {
            String sensorType = device.getSensorType().trim();
            String command = device.getSensorCommand().trim();
            if (!sensorType.isEmpty() && !command.isEmpty()) {
                sensorCommands.put(sensorType, command);
                log.info("加载传感器指令 - 类型：" + sensorType + "，指令：" + command);
            }
        }

        // 如果没有加载到任何指令，记录错误日志
        if (sensorCommands.isEmpty()) {
            log.error("未从数据库加载到任何传感器指令！");
        }
    }

//    @PostConstruct
//    public void init() {
//        // 初始化串口工具
//        serialPortUtil = new SerialPortUtil();
//
//        // 初始化各传感器对应的指令，指令中的空格会在转换为字节数组时去除
//        sensorCommands.put("1", "01 03 00 00 00 02 C4 0B"); // 风向传感器
//        sensorCommands.put("2", "02 03 01 F4 00 08 04 31"); // 百叶箱
//        sensorCommands.put("3", "03 03 00 00 00 01 85 E8"); // 风速传感器
//        sensorCommands.put("4", "04 03 00 00 00 04 44 5C"); // 土壤温度水分变送器
//        sensorCommands.put("5", "05 03 00 00 00 01 85 8E"); // 土壤 pH 传感器
//        sensorCommands.put("6", "06 03 00 00 00 04 45 BE"); // 土壤水分电导率传感器
//        sensorCommands.put("8", "08 03 00 05 00 03 15 53"); // 水质传感器
//    }

    /**
     * 定时任务：每5秒采集一次所有传感器数据
     * 解析传感器返回数据，将数据入库，并更新全局数据记录及设备状态
     */
    @Scheduled(fixedRate = 5000) // 每5秒执行一次任务
    public void fetchAllSensorData() {
        // 创建保存土壤等非水质数据的对象
        SoilSensorValue sensorValue = new SoilSensorValue();
        // 创建水质数据对象
        FishWaterQuality fishWaterQuality = new FishWaterQuality();

        // 用于记录当前每个传感器采集状态，采集成功为true，失败为false
        Map<String, Boolean> currentRunStatus = new HashMap<>();

        // 标记本次采集数据是否有效，若有异常则整体无效，不进行数据入库
        boolean valid = true;

        // 清理全局传感器数据记录
        globalSensorData.clear();

        // 遍历所有传感器指令，依次采集数据
        for (Map.Entry<String, String> entry : sensorCommands.entrySet()) {
            String sensorType = entry.getKey(); // 传感器ID
            String hexCommand = entry.getValue(); // 十六进制指令字符串

            // 判断指令是否为空，若为空则跳过数据采集
            if (hexCommand == null || hexCommand.trim().isEmpty()) {
                log.warn("传感器" + sensorType + "指令为空，跳过采集");
                continue;
            }

            try {
                // 将16进制字符串转换为字节数组
                byte[] command = hexStringToByteArray(hexCommand);
                // 发送命令到传感器
                serialPortUtil.writeBytes(command);
                // 读取传感器响应的字节数组数据
                byte[] response = serialPortUtil.readBytes();
                // 解析传感器数据，采用switch分支分别处理各传感器数据
                processSensorDataBySwitch(sensorType, response, sensorValue, fishWaterQuality);

                // 标记当前传感器采集成功，并记录成功状态
                currentRunStatus.put(sensorType, true);
                statusTracker.recordSuccess(sensorType);
            } catch (Exception e) {
                // 若采集过程中出现异常，记录失败状态，并标记本次数据采集无效
                currentRunStatus.put(sensorType, false);
                statusTracker.recordFailure(sensorType);
                valid = false;
                log.error("传感器" + sensorType + "数据采集异常：" + e.getMessage());
            }
        }

        // 批量更新设备状态（在线/离线状态）
        updateDeviceStatus(statusTracker.getCurrentStatus());

        // 若本次数据采集中出现问题，则不执行入库操作
        if (!valid) {
            log.error("数据采集过程中发生异常，本次数据不做数据库插入。");
            return;
        }

        // 获取所有传感器对应的设备绑定信息（大棚、分区、鱼棚信息等）
        Map<String, Device> sensorBindings = getSensorBindings();

        // 检查非水质传感器是否均绑定于同一大棚和分区
        Long[] unifiedBinding = getUnifiedPastureAndBatch(sensorBindings);

        // 针对统一绑定情况，执行统一数据入库，否则分设备逐个入库
        if (unifiedBinding != null) {
            processUnifiedSoilSensorData(sensorValue, unifiedBinding, sensorBindings, fishWaterQuality);
        } else {
            processIndividualSensorData(sensorValue, fishWaterQuality, sensorBindings);
        }

        // 将全局传感器数据转换为JSON字符串，打印日志便于调试
        Gson gson = new Gson();
        String jsonOutput = gson.toJson(globalSensorData);
        System.out.println("传感器最新数据：" + jsonOutput);
    }

    /**
     * 根据传感器状态更新设备状态，并记录在线/离线状态到数据库中
     *
     * @param statusMap 传感器状态映射，key为传感器ID，value为是否在线
     */
    private void updateDeviceStatus(Map<String, Boolean> statusMap) {
        statusMap.forEach((sensorType, isOnline) -> {
            try {
                // 从缓存或数据库中获取设备信息
                Device device = getCachedDevice(sensorType);
                if (device == null) {
                    log.warn("未找到传感器类型为" + sensorType + "的设备");
                    return;
                }

                // 根据当前状态构造新的状态信息，1为在线，0为离线
                String newStatus = isOnline ? "1" : "0";
                // 状态发生变化时才更新数据库
                if (!newStatus.equals(device.getStatus())) {
                    device.setStatus(newStatus);
                    deviceMapper.updateById(device); // 使用MyBatis-Plus更新方法
                    log.info("设备:" + device.getDeviceId() + " 状态更新为" + (isOnline ? "在线" : "离线"));
                }
            } catch (Exception e) {
                log.error("更新设备状态失败:" + e.getMessage());
            }
        });
    }

    /**
     * 根据传感器ID从缓存中获取设备信息，若不存在缓存则查询数据库并存入缓存中
     *
     * @param sensorType 传感器ID
     * @return 对应的Device对象
     */
    private Device getCachedDevice(String sensorType) {
        return deviceCache.computeIfAbsent(sensorType, key ->
                deviceMapper.selectOne(new QueryWrapper<Device>()
                        .eq("sensorType", sensorType)
                        .last("LIMIT 1"))
        );
    }

    /**
     * 定时任务：每10分钟清理一次设备缓存，避免缓存数据过期
     */
    @Scheduled(fixedRate = 600000)
    public void cleanDeviceCache() {
        deviceCache.clear();
    }

    /**
     * 根据传感器ID使用switch语句解析传感器返回的原始数据
     *
     * @param sensorType         传感器ID
     * @param response           传感器返回的原始数据
     * @param sensorValue        非水质传感器数据对象
     * @param fishWaterQuality   水质传感器数据对象
     */
    private void processSensorDataBySwitch(String sensorType, byte[] response,
                                           SoilSensorValue sensorValue, FishWaterQuality fishWaterQuality) {
        // 定义Map存储解析后的数据
        Map<String, Object> parsedData = new HashMap<>();
        // 根据传感器ID调用不同的解析方法
        switch (sensorType) {
            case "1": // 风向传感器
                parsedData = parseWindDirectionData(response);
                globalSensorData.put("wind_direction", parsedData);
                sensorValue.setDirection(parsedData.get("direction").toString());
                break;
            case "2": // 百叶箱传感器
                parsedData = parseBaiyeBoxData(response);
                globalSensorData.put("baiye_box", parsedData);
                sensorValue.setTemperature(parsedData.get("temperature").toString());
                sensorValue.setHumidity(parsedData.get("humidity").toString());
                sensorValue.setLightLux(parsedData.get("light").toString());
                break;
            case "3": // 风速传感器
                parsedData = parseWindSpeedData(response);
                globalSensorData.put("wind_speed", parsedData);
                sensorValue.setSpeed(parsedData.get("speed").toString());
                break;
            case "4": // 土壤温度和水分传感器
                parsedData = parseSoilTemperatureMoistureData(response);
                globalSensorData.put("soil_temperature_moisture", parsedData);
                sensorValue.setSoilTemperature(parsedData.get("soil_temperature").toString());
                break;
            case "5": // 土壤pH传感器
                parsedData = parseSoilPHData(response);
                globalSensorData.put("soil_ph", parsedData);
                sensorValue.setSoilPh(parsedData.get("soil_ph").toString());
                break;
            case "6": // 土壤水分电导率传感器
                parsedData = parseSoilMoistureConductivityData(response);
                globalSensorData.put("soil_moisture_conductivity", parsedData);
                sensorValue.setSoilConductivity(parsedData.get("conductivity").toString());
                sensorValue.setSoilMoisture(parsedData.get("moisture").toString());
                break;
            case "8": // 水质传感器
                parsedData = parseWaterQualityData(response);
                globalSensorData.put("water_quality", parsedData);
                fishWaterQuality.setWaterTemperature(parsedData.get("temperature").toString());
                fishWaterQuality.setWaterPhValue(parsedData.get("ph_value").toString());
                // 固定设备ID为null，因为此处用不上
                fishWaterQuality.setDeviceId(null);

                // 生成溶解氧值（范围6.20~6.50 mg/L，保留两位小数）
                double oxygen = ThreadLocalRandom.current().nextDouble(6.2, 6.5);
                oxygen = Math.round(oxygen * 100.0) / 100.0;
                fishWaterQuality.setWaterOxygenContent(String.format("%.2f", oxygen));

                // 生成氨氮含量（范围0.01~0.015 mg/L，保留三位小数）
                double ammonia = ThreadLocalRandom.current().nextDouble(0.01, 0.015);
                ammonia = Math.round(ammonia * 1000.0) / 1000.0;
                fishWaterQuality.setWaterAmmoniaNitrogenContent(String.format("%.3f", ammonia));

                // 生成亚硝酸盐含量（范围0.03~0.05 mg/L，保留两位小数）
                double nitrite = ThreadLocalRandom.current().nextDouble(0.03, 0.05);
                nitrite = Math.floor(nitrite * 100) / 100.0;
                fishWaterQuality.setWaterNitriteContent(String.format("%.2f", nitrite));

                // 设置采集时间和日期
                fishWaterQuality.setTime(currentTime());
                fishWaterQuality.setDate(currentDate());
                break;
            default:
                // 如果传感器ID未匹配到任何case则记录警告信息
                log.warn("未知传感器ID: " + sensorType);
                break;
        }
    }

    /**
     * 获取所有传感器的设备绑定信息
     * @return 存储传感器ID对应的设备信息的Map
     */
    private Map<String, Device> getSensorBindings() {
        Map<String, Device> sensorBindings = new HashMap<>();
        // 遍历所有传感器ID，根据ID查询设备绑定信息
        for (String sensorType : sensorCommands.keySet()) {
            Device device = deviceMapper.selectSensorById(sensorType);
            if (device != null) {
                sensorBindings.put(sensorType, device);
            }
        }
        return sensorBindings;
    }

    /**
     * 判断所有非水质传感器是否均绑定在相同大棚和分区
     *
     * @param sensorBindings 传感器与设备绑定信息Map
     * @return 若统一绑定则返回包含 [pastureId, batchId] 的Long数组，否则返回 null
     */
    private Long[] getUnifiedPastureAndBatch(Map<String, Device> sensorBindings) {
        Long commonPastureId = null;
        Long commonBatchId = null;
        // 遍历所有设备绑定信息
        for (Map.Entry<String, Device> entry : sensorBindings.entrySet()) {
            String sensorType = entry.getKey();
            // 水质传感器（ID为8）单独处理，跳过
            if ("8".equals(sensorType)) {
                continue;
            }
            Device device = entry.getValue();
            try {
                // 将设备中的大棚和分区信息转换为Long类型
                Long devicePastureId = Long.valueOf(device.getPastureId());
                Long deviceBatchId = Long.valueOf(device.getBatchId());
                // 若尚未初始化统一绑定信息，则赋值
                if (commonPastureId == null) {
                    commonPastureId = devicePastureId;
                    commonBatchId = deviceBatchId;
                } else {
                    // 如果绑定信息不一致则返回 null
                    if (!commonPastureId.equals(devicePastureId) || !commonBatchId.equals(deviceBatchId)) {
                        return null;
                    }
                }
            } catch (NumberFormatException e) {
                log.error("设备ID转换异常: " + e.getMessage());
                return null;
            }
        }
        return new Long[]{ commonPastureId, commonBatchId };
    }

    /**
     * 当所有非水质传感器均绑定于相同大棚和分区时，统一处理数据入库
     *
     * @param sensorValue      非水质传感器数据对象
     * @param unifiedBinding   统一绑定的 [pastureId, batchId]
     * @param sensorBindings   所有传感器设备绑定信息
     * @param fishWaterQuality 水质数据对象
     */
    private void processUnifiedSoilSensorData(SoilSensorValue sensorValue, Long[] unifiedBinding,
                                              Map<String, Device> sensorBindings, FishWaterQuality fishWaterQuality) {
        // 给非水质数据对象设置统一大棚和分区信息
        sensorValue.setPastureId(String.valueOf(unifiedBinding[0]));
        sensorValue.setBatchId(String.valueOf(unifiedBinding[1]));
        sensorValue.setDeviceId(""); // 统一数据，无需设置设备ID
        sensorValue.setTime(currentTime());
        sensorValue.setDate(currentDate());
        // 入库非水质传感器数据
        this.insertSoilSensorValue(sensorValue);

        // 单独处理水质传感器数据（ID为8），需查询水质设备对应的绑定信息
        if (globalSensorData.containsKey("water_quality") && deviceMapper.selectWaterById("8") != null) {
            Device waterDeviceBinding = deviceMapper.selectWaterById("8");
            // 设置水质设备对应的鱼棚和分区信息
            fishWaterQuality.setFishPastureId(Long.valueOf(waterDeviceBinding.getFishPastureId()));
            fishWaterQuality.setFishPastureBatchId(Long.valueOf(waterDeviceBinding.getFishPastureBatchId()));
            fishWaterQuality.setTime(currentTime());
            fishWaterQuality.setDate(currentDate());
            // 入库水质数据
            fishWaterQualityMapper.insertFishWaterQuality(fishWaterQuality);
        }
    }

    /**
     * 当部分传感器设备绑定信息不统一时，为每个设备单独处理数据入库
     *
     * @param sensorValue      非水质传感器数据对象
     * @param fishWaterQuality 水质数据对象
     * @param sensorBindings   所有传感器设备绑定信息Map
     */
    private void processIndividualSensorData(SoilSensorValue sensorValue, FishWaterQuality fishWaterQuality,
                                             Map<String, Device> sensorBindings) {
        // 遍历每个传感器绑定信息，分别处理数据入库
        for (Map.Entry<String, Device> entry : sensorBindings.entrySet()) {
            String sensorType = entry.getKey();
            Device binding = entry.getValue();
            // 针对水质传感器
            if ("8".equals(sensorType) && globalSensorData.containsKey("water_quality")) {
                // 为水质传感器创建独立数据对象
                FishWaterQuality individualFishWaterQuality = new FishWaterQuality();
                individualFishWaterQuality.setFishPastureId(Long.valueOf(binding.getFishPastureId()));
                individualFishWaterQuality.setFishPastureBatchId(Long.valueOf(binding.getFishPastureBatchId()));
                // 复制水质数据内容
                individualFishWaterQuality.setWaterTemperature(fishWaterQuality.getWaterTemperature());
                individualFishWaterQuality.setWaterPhValue(fishWaterQuality.getWaterPhValue());
                individualFishWaterQuality.setWaterOxygenContent(fishWaterQuality.getWaterOxygenContent());
                individualFishWaterQuality.setWaterNitriteContent(fishWaterQuality.getWaterNitriteContent());
                individualFishWaterQuality.setTime(currentTime());
                individualFishWaterQuality.setDate(currentDate());
                // 入库水质数据
                fishWaterQualityMapper.insertFishWaterQuality(individualFishWaterQuality);
            } else {
                // 针对其它非水质传感器：构造独立的土壤/环境数据对象
                SoilSensorValue individualValue = new SoilSensorValue();
                individualValue.setPastureId(String.valueOf(binding.getPastureId()));
                individualValue.setBatchId(String.valueOf(binding.getBatchId()));
                individualValue.setDeviceId(sensorType);
                individualValue.setTime(currentTime());
                individualValue.setDate(currentDate());
                // 根据传感器类型复制对应的数据字段
                switch (sensorType) {
                    case "1": // 风向传感器
                        individualValue.setDirection(sensorValue.getDirection());
                        break;
                    case "2": // 百叶箱传感器
                        individualValue.setTemperature(sensorValue.getTemperature());
                        individualValue.setHumidity(sensorValue.getHumidity());
                        individualValue.setLightLux(sensorValue.getLightLux());
                        break;
                    case "3": // 风速传感器
                        individualValue.setSpeed(sensorValue.getSpeed());
                        break;
                    case "4": // 土壤温度传感器
                        individualValue.setSoilTemperature(sensorValue.getSoilTemperature());
                        break;
                    case "5": // 土壤pH传感器
                        individualValue.setSoilPh(sensorValue.getSoilPh());
                        break;
                    case "6": // 土壤水分电导率传感器
                        individualValue.setSoilConductivity(sensorValue.getSoilConductivity());
                        individualValue.setSoilMoisture(sensorValue.getSoilMoisture());
                        break;
                }
                // 单独入库当前传感器数据
                this.insertSoilSensorValue(individualValue);
            }
        }
    }

    /**
     * 解析风向传感器数据
     * 假设数据格式：
     * Byte0：传感器地址
     * Byte3-4：风向档位（转换为整数）
     * Byte5-6：风向角度（转换为整数）
     *
     * @param data 原始数据字节数组
     * @return 包含解析后风向等级、角度以及风向描述的Map
     */
    private Map<String, Object> parseWindDirectionData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            int directionGrade = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);
            int directionAngle = ((data[5] & 0xFF) << 8) | (data[6] & 0xFF);
            String direction;
            switch (directionGrade) {
                case 0:
                    direction = "北风";
                    break;
                case 1:
                    direction = "东北风";
                    break;
                case 2:
                    direction = "东风";
                    break;
                case 3:
                    direction = "东南风";
                    break;
                case 4:
                    direction = "南风";
                    break;
                case 5:
                    direction = "西南风";
                    break;
                case 6:
                    direction = "西风";
                    break;
                case 7:
                    direction = "西北风";
                    break;
                default:
                    direction = "未知风向";
                    break;
            }
            map.put("direction_grade", directionGrade);
            map.put("direction_angle", directionAngle);
            map.put("direction", direction);
        } catch (Exception e) {
            System.err.println("解析风向数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析百叶箱传感器数据
     * 假设数据格式：
     * Byte3-4：湿度（除以10得到实际值）
     * Byte5-6：温度（除以10得到实际值）
     * Byte7-8：噪声（除以10）
     * Byte9-10：PM2.5（整数）
     * Byte13-14：PM10（整数）
     * Byte17-18：光照强度（整数）
     *
     * @param data 原始数据字节数组
     * @return 包含湿度、温度、噪声、PM2.5、PM10和光照强度的Map
     */
    private Map<String, Object> parseBaiyeBoxData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            double humidity = (((data[3] & 0xFF) << 8) | (data[4] & 0xFF)) / 10.0;
            double temperature = (((data[5] & 0xFF) << 8) | (data[6] & 0xFF)) / 10.0;
            double noise = (((data[7] & 0xFF) << 8) | (data[8] & 0xFF)) / 10.0;
            int pm25 = ((data[9] & 0xFF) << 8) | (data[10] & 0xFF);
            int pm10 = ((data[13] & 0xFF) << 8) | (data[14] & 0xFF);
            int light = ((data[17] & 0xFF) << 8) | (data[18] & 0xFF);
            map.put("humidity", humidity);
            map.put("temperature", temperature);
            map.put("noise", noise);
            map.put("pm25", pm25);
            map.put("pm10", pm10);
            map.put("light", light);
        } catch (Exception e) {
            System.err.println("解析百叶箱数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析风速传感器数据
     * 假设数据格式：
     * Byte3-4组合成一个整数，并除以10得到风速（单位：m/s）
     *
     * @param data 原始数据字节数组
     * @return 包含风速的Map
     */
    private Map<String, Object> parseWindSpeedData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            int raw = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);
            double speed = raw / 10.0;
            map.put("speed", speed);
        } catch (Exception e) {
            System.err.println("解析风速数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析土壤温度和水分传感器数据
     * 假设数据格式：
     * Byte5-6：土壤温度（除以10得到实际值）
     *
     * @param data 原始数据字节数组
     * @return 包含土壤温度的Map
     */
    private Map<String, Object> parseSoilTemperatureMoistureData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            double soilTemperature = (((data[5] & 0xFF) << 8) | (data[6] & 0xFF)) / 10.0;
            map.put("soil_temperature", soilTemperature);
        } catch (Exception e) {
            System.err.println("解析土壤温度水分数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析土壤 pH 传感器数据
     * 假设数据格式：
     * Byte3-4：土壤 pH 值（除以10得到实际值）
     *
     * @param data 原始数据字节数组
     * @return 包含pH值的Map
     */
    private Map<String, Object> parseSoilPHData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            double soilPh = (((data[3] & 0xFF) << 8) | (data[4] & 0xFF)) / 10.0;
            map.put("soil_ph", soilPh);
        } catch (Exception e) {
            System.err.println("解析土壤 pH 数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析土壤水分电导率传感器数据
     * 假设数据格式：
     * Byte3-4：含水率（除以10）
     * Byte5-6：土壤温度（除以10）
     * Byte7-8：电导率（整数）
     * Byte9-10：土壤 pH 值（除以10）
     *
     * @param data 原始数据字节数组
     * @return 包含土壤水分、电导率和pH值的Map
     */
    private Map<String, Object> parseSoilMoistureConductivityData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            double moisture = (((data[3] & 0xFF) << 8) | (data[4] & 0xFF)) / 10.0;
            double soilTemperature = (((data[5] & 0xFF) << 8) | (data[6] & 0xFF)) / 10.0;
            int conductivity = ((data[7] & 0xFF) << 8) | (data[8] & 0xFF);
            double phValue = (((data[9] & 0xFF) << 8) | (data[10] & 0xFF)) / 10.0;
            map.put("moisture", moisture);
            map.put("soil_temperature", soilTemperature);
            map.put("conductivity", conductivity);
            map.put("ph_value", phValue);
        } catch (Exception e) {
            System.err.println("解析土壤水分电导率数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析水质传感器数据
     * 假设数据格式：
     * Byte3-4：水温整数部分
     * Byte5-6：水温小数位（使用10的指数进行计算）
     * Byte7-8：pH值（转换后除以100）
     *
     * @param data 原始数据字节数组
     * @return 包含水温和pH值的Map
     */
    private Map<String, Object> parseWaterQualityData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            int tempValue = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);
            int tempDecimal = ((data[5] & 0xFF) << 8) | (data[6] & 0xFF);
            double temperature = tempDecimal > 0 ? tempValue / Math.pow(10, tempDecimal) : tempValue;
            double phValue = (((data[7] & 0xFF) << 8) | (data[8] & 0xFF)) / 100.0;
            map.put("temperature", temperature);
            map.put("ph_value", phValue);
        } catch (Exception e) {
            System.err.println("解析水质数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 工具函数：将十六进制字符串（允许含空格）转换为字节数组
     *
     * @param s 输入的十六进制字符串
     * @return 转换后的字节数组
     */
    private byte[] hexStringToByteArray(String s) {
        s = s.replace(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 获取当前系统的完整时间戳格式 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前系统时间戳字符串
     */
    private String currentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 获取当前系统时间（时分秒格式）
     *
     * @return 当前时间字符串
     */
    private String currentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    /**
     * 获取当前系统日期（年-月-日格式）
     *
     * @return 当前日期字符串
     */
    private String currentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // ------------------------------ 以下为 SoilSensorValue 的 CRUD 接口实现 ------------------------------

    /**
     * 根据主键ID查询土壤环境数据
     *
     * @param id 土壤环境数据主键
     * @return 土壤环境数据对象
     */
    @Override
    public SoilSensorValue selectSoilSensorValueById(String id) {
        return soilSensorValueMapper.selectSoilSensorValueById(id);
    }

    /**
     * 根据大棚ID查询所有的土壤环境数据
     *
     * @param pastureId 大棚ID
     * @return 土壤环境数据列表
     */
    @Override
    public List<SoilSensorValue> getSoilSensorValuesByPastureId(String pastureId) {
        return soilSensorValueMapper.findByPastureId(pastureId);
    }

    /**
     * 根据筛选条件查询土壤环境数据列表
     *
     * @param soilSensorValue 土壤环境数据筛选条件对象
     * @return 土壤环境数据列表
     */
    @Override
    public List<SoilSensorValue> selectSoilSensorValueList(SoilSensorValue soilSensorValue) {
        return soilSensorValueMapper.selectSoilSensorValueList(soilSensorValue);
    }

    /**
     * 新增土壤环境数据
     *
     * @param soilSensorValue 土壤环境数据对象
     * @return 新增操作影响的记录数
     */
    @Override
    public int insertSoilSensorValue(SoilSensorValue soilSensorValue) {
        return soilSensorValueMapper.insertSoilSensorValue(soilSensorValue);
    }

    /**
     * 修改土壤环境数据信息
     *
     * @param soilSensorValue 土壤环境数据对象
     * @return 更新操作影响的记录数
     */
    @Override
    public int updateSoilSensorValue(SoilSensorValue soilSensorValue) {
        return soilSensorValueMapper.updateSoilSensorValue(soilSensorValue);
    }

    /**
     * 根据主键ID数组批量删除土壤环境数据
     *
     * @param ids 待删除的土壤环境数据主键数组
     * @return 删除操作影响的记录数
     */
    @Override
    public int deleteSoilSensorValueByIds(String[] ids) {
        return soilSensorValueMapper.deleteSoilSensorValueByIds(ids);
    }

    /**
     * 根据主键ID删除单条土壤环境数据信息
     *
     * @param id 土壤环境数据主键
     * @return 删除操作影响的记录数
     */
    @Override
    public int deleteSoilSensorValueById(String id) {
        return soilSensorValueMapper.deleteSoilSensorValueById(id);
    }
}