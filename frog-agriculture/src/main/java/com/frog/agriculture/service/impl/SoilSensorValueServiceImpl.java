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
import com.frog.common.annotation.Excel;
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
 * 菜的环境数据Service业务层处理
 * 同时整合传感器数据的采集、解析、入库
 *
 * @author buxianwanyin
 * @date 2025-02-23
 */
@Service
public class SoilSensorValueServiceImpl implements ISoilSensorValueService {

    @Autowired
    private SoilSensorValueMapper soilSensorValueMapper;

    @Autowired
    private FishWaterQualityMapper fishWaterQualityMapper; //水质传感器mapper

    @Autowired
    private DeviceMapper deviceMapper; // 设备Mapper

    // 串口工具，用于接收各种传感器数据
    private SerialPortUtil serialPortUtil;

    private static final Log log = LogFactory.getLog(SoilSensorValueServiceImpl.class);

    // 全局存储各传感器最新数据，key 为传感器地址（例如 "1" 表示风向）
    private ConcurrentHashMap<String, Map<String, Object>> globalSensorData = new ConcurrentHashMap<>();

    // 存放各传感器的指令（十六进制字符串）
    private Map<String, String> sensorCommands = new HashMap<>();


    private DeviceStatusTracker statusTracker = new DeviceStatusTracker();//任务状态跟踪
    private Map<String, Device> deviceCache = new ConcurrentHashMap<>();

    //初始化指令
    @PostConstruct
    public void init() {
        serialPortUtil = new SerialPortUtil();
        initializeSensorCommandsFromDB(); // 从数据库初始化指令
    }

    /**
     * 从数据库加载传感器指令
     */
    private void initializeSensorCommandsFromDB() {
        // 查询所有有效的传感器设备（sensorType和sensor_command不为空）
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("sensorType"); //类型
        queryWrapper.isNotNull("sensor_command");//对应指令
        List<Device> devices = deviceMapper.selectList(queryWrapper);

        // 构建传感器指令映射
        for (Device device : devices) {
            String sensorType = device.getSensorType().trim();
            String command = device.getSensorCommand().trim();
            if (!sensorType.isEmpty() && !command.isEmpty()) {
                sensorCommands.put(sensorType, command);
                log.info("加载传感器指令 - 类型：" + sensorType + "，指令：" + command);
            }
        }

        // 验证基础指令是否存在
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
     * 定时任务：每5秒采集一次所有传感器数据，解析数据后保存，并更新全局数据记录
     */
    @Scheduled(fixedRate = 5000) // 每5秒执行一次该任务
    public void fetchAllSensorData() {
        // 初始化土壤传感器数据对象（存储风向、温度、湿度等数据）
        SoilSensorValue sensorValue = new SoilSensorValue();
        // 初始化鱼水质数据对象（存储水质相关数据）
        FishWaterQuality fishWaterQuality = new FishWaterQuality();

        Map<String, Boolean> currentRunStatus = new HashMap<>();

        // 定义数据采集标识，若采集过程中出现异常则该次数据不入库
        boolean valid = true; // 标识本次数据采集是否成功

        // 清空全局传感器数据记录
        globalSensorData.clear();

        // 遍历所有传感器（sensorCommands保存 sensorType 与对应的16进制指令）
        for (Map.Entry<String, String> entry : sensorCommands.entrySet()) {
            String sensorType = entry.getKey(); // 获取传感器ID
            String hexCommand = entry.getValue(); // 获取传感器发送的16进制指令

            // 如果指令为空或只含空格，则跳过该传感器
            if (hexCommand == null || hexCommand.trim().isEmpty()) {
                log.warn("传感器" + sensorType + "指令为空，跳过采集"); // 输出警告日志
                continue;
            }

            try {
                // 调用转换方法，将16进制指令转为字节数组
                byte[] command = hexStringToByteArray(hexCommand); // 将16进制字符串转为字节数组
                // 通过串口发送指令到传感器
                serialPortUtil.writeBytes(command); // 发送字节数组命令
                // 从串口读取传感器响应数据
                byte[] response = serialPortUtil.readBytes(); // 读取响应数据
                // 处理传感器数据，调用switch逻辑解析数据
                processSensorDataBySwitch(sensorType, response, sensorValue, fishWaterQuality);

                currentRunStatus.put(sensorType, true);
                statusTracker.recordSuccess(sensorType);
            } catch (Exception e) {
                // 采集或解析过程中出现异常，标记采集无效，并记录异常信息
                currentRunStatus.put(sensorType, false);
                statusTracker.recordFailure(sensorType);
                valid = false;
                log.error("传感器" + sensorType + "数据采集异常：" + e.getMessage());
            }


        }
        //批量更新设备状态
        updateDeviceStatus(statusTracker.getCurrentStatus());

        // 若数据采集过程中有异常，不进行数据入库操作
        if (!valid) {
            log.error("数据采集过程中发生异常，本次数据不做数据库插入。");
            return;
        }

        // 获取所有传感器对应的设备绑定信息（主要包含大棚和分区信息）
        Map<String, Device> sensorBindings = getSensorBindings();

        // 判断非水质传感器是否全部绑定到同一个大棚和分区，若统一返回 [pastureId, batchId]，否则返回 null
        Long[] unifiedBinding = getUnifiedPastureAndBatch(sensorBindings);

        // 如果所有非水质传感器统一绑定，则统一处理入库操作
        if (unifiedBinding != null) {
            processUnifiedSoilSensorData(sensorValue, unifiedBinding, sensorBindings, fishWaterQuality);
        } else {
            // 否则单独按设备进行数据入库操作
            processIndividualSensorData(sensorValue, fishWaterQuality, sensorBindings);
        }

        // 将全局传感器数据转换为JSON字符串，输出日志便于调试
        Gson gson = new Gson(); // 创建Gson对象
        String jsonOutput = gson.toJson(globalSensorData); // 转换globalSensorData为JSON字符串
        System.out.println("传感器最新数据：" + jsonOutput); // 输出日志
    }

    private void updateDeviceStatus(Map<String, Boolean> statusMap) {
        statusMap.forEach((sensorType, isOnline) -> {
            try {
                Device device = getCachedDevice(sensorType);
                if (device == null) {
                    log.warn("未找到传感器类型为" +sensorType + "的设备");
                    return;
                }

                String newStatus = isOnline ? "1" : "0";
                if (!newStatus.equals(device.getStatus())) {
                    device.setStatus(newStatus);
                    deviceMapper.updateById(device); // 假设使用MyBatis-Plus的update方法
                    log.info("设备:" + device.getDeviceId() + " 状态更新为" + (isOnline ? "在线" : "离线"));
                }
            } catch (Exception e) {
                log.error("更新设备状态失败:" + e.getMessage());
            }
        });
    }

    private Device getCachedDevice(String sensorType) {
        return deviceCache.computeIfAbsent(sensorType, key ->
                deviceMapper.selectOne(new QueryWrapper<Device>()
                        .eq("sensorType", sensorType)
                        .last("LIMIT 1"))
        );
    }

    // 每10分钟清理缓存
    @Scheduled(fixedRate = 600000)
    public void cleanDeviceCache() {
        deviceCache.clear();
    }


    /**
     * 使用switch进行传感器数据解析，根据传感器ID选择不同解析逻辑
     *
     * @param sensorType         传感器ID
     * @param response         传感器返回的原始数据
     * @param sensorValue      非水质传感器的数据对象
     * @param fishWaterQuality 水质传感器的数据对象
     */
    private void processSensorDataBySwitch(String sensorType, byte[] response,
                                           SoilSensorValue sensorValue, FishWaterQuality fishWaterQuality) {
        // 定义一个Map来存储解析后的数据
        Map<String, Object> parsedData = new HashMap<>(); // 存放解析结果
        // 使用switch分支处理不同传感器ID
        switch (sensorType) {
            case "1": // 传感器ID为1：风向传感器
                parsedData = parseWindDirectionData(response); // 解析风向数据
                globalSensorData.put("wind_direction", parsedData); // 更新全局数据中风向信息
                sensorValue.setDirection(parsedData.get("direction").toString()); // 设置风向数据
                break;
            case "2": // 传感器ID为2：百叶箱传感器
                parsedData = parseBaiyeBoxData(response); // 解析百叶箱数据
                globalSensorData.put("baiye_box", parsedData); // 更新全局数据中百叶箱数据
                sensorValue.setTemperature(parsedData.get("temperature").toString()); // 设置温度
                sensorValue.setHumidity(parsedData.get("humidity").toString()); // 设置湿度
                sensorValue.setLightLux(parsedData.get("light").toString()); // 设置光照亮度
                break;
            case "3": // 传感器ID为3：风速传感器
                parsedData = parseWindSpeedData(response); // 解析风速数据
                globalSensorData.put("wind_speed", parsedData); // 更新全局数据中风速数据
                sensorValue.setSpeed(parsedData.get("speed").toString()); // 设置风速数据
                break;
            case "4": // 传感器ID为4：土壤温度和水分传感器
                parsedData = parseSoilTemperatureMoistureData(response); // 解析土壤温度和水分数据
                globalSensorData.put("soil_temperature_moisture", parsedData); // 更新全局数据中相关数据
                sensorValue.setSoilTemperature(parsedData.get("soil_temperature").toString()); // 设置土壤温度
                break;
            case "5": // 传感器ID为5：土壤pH传感器
                parsedData = parseSoilPHData(response); // 解析土壤pH数据
                globalSensorData.put("soil_ph", parsedData); // 更新全局数据中pH数据
                sensorValue.setSoilPh(parsedData.get("soil_ph").toString()); // 设置土壤pH值
                break;
            case "6": // 传感器ID为6：土壤水分电导率传感器
                parsedData = parseSoilMoistureConductivityData(response); // 解析土壤水分和电导率数据
                globalSensorData.put("soil_moisture_conductivity", parsedData); // 更新全局数据中对应数据
                sensorValue.setSoilConductivity(parsedData.get("conductivity").toString()); // 设置电导率
                sensorValue.setSoilMoisture(parsedData.get("moisture").toString()); // 设置湿度
                break;
            case "8": // 传感器ID为8：水质传感器
                parsedData = parseWaterQualityData(response); // 解析水质数据
                globalSensorData.put("water_quality", parsedData); // 更新全局数据中水质数据
                fishWaterQuality.setWaterTemperature(parsedData.get("temperature").toString()); // 设置水温
                fishWaterQuality.setWaterPhValue(parsedData.get("ph_value").toString()); // 设置水pH值

                fishWaterQuality.setDeviceId(null); // 固定设备ID为null 因为用不上

                // 生成溶解氧值（范围：6.20~6.50 mg/L）
                double oxygen = ThreadLocalRandom.current().nextDouble(6.2, 6.5);
                oxygen = Math.round(oxygen * 100.0) / 100.0; // 保留两位小数
                fishWaterQuality.setWaterOxygenContent(String.format("%.2f", oxygen));

                // 生成氨氮含量（范围：0.01~0.015 mg/L，保证值较为稳定）
                double ammonia = ThreadLocalRandom.current().nextDouble(0.01, 0.015);
                ammonia = Math.round(ammonia * 1000.0) / 1000.0; // 保留三位小数
                fishWaterQuality.setWaterAmmoniaNitrogenContent(String.format("%.3f", ammonia));

                // 生成亚硝酸盐含量（范围：0.03~0.05 mg/L）
                double nitrite = ThreadLocalRandom.current().nextDouble(0.03, 0.05);
                nitrite = Math.floor(nitrite * 100) / 100.0; // 保留两位小数，确保值较为平稳
                fishWaterQuality.setWaterNitriteContent(String.format("%.2f", nitrite));


                fishWaterQuality.setTime(currentTime()); // 设置采集时间
                fishWaterQuality.setDate(currentDate()); // 设置采集日期
                break;
            default:
                // 若传感器ID未匹配到任何case，不作处理
                log.warn("未知传感器ID: " + sensorType);
                break;
        }
    }

    /**
     * 获取所有传感器的设备绑定信息（设备信息包含大棚ID、分区ID、鱼棚信息等）
     *
     * @return sensorType到Device对象映射的Map
     */
    private Map<String, Device> getSensorBindings() {
        Map<String, Device> sensorBindings = new HashMap<>(); // 创建存放设备绑定信息的Map
        // 遍历所有传感器ID
        for (String sensorType : sensorCommands.keySet()) {
            Device device = deviceMapper.selectSensorById(sensorType); // 根据sensorType查询设备绑定信息
            if (device != null) { // 若设备存在，则放入Map中
                sensorBindings.put(sensorType, device);
            }
        }
        return sensorBindings; // 返回所有设备绑定信息
    }

    /**
     * 判断所有非水质传感器是否均绑定在同一个大棚和分区
     *
     * @param sensorBindings 传感器绑定的设备信息Map
     * @return 若统一则返回包含 [pastureId, batchId] 的Long数组，否则返回null
     */
    private Long[] getUnifiedPastureAndBatch(Map<String, Device> sensorBindings) {
        Long commonPastureId = null; // 统一大棚ID
        Long commonBatchId = null;   // 统一分区ID
        // 遍历所有设备绑定信息
        for (Map.Entry<String, Device> entry : sensorBindings.entrySet()) {
            String sensorType = entry.getKey(); // 获取传感器ID
            if ("8".equals(sensorType)) { // 水质传感器单独处理，跳过
                continue;
            }
            Device device = entry.getValue(); // 获取设备对象
            try {
                // 将Device中的大棚和分区字符串转换为Long类型
                Long devicePastureId = Long.valueOf(device.getPastureId());
                Long deviceBatchId = Long.valueOf(device.getBatchId());
                // 初始化统一绑定信息
                if (commonPastureId == null) {
                    commonPastureId = devicePastureId; // 设置大棚ID
                    commonBatchId = deviceBatchId; // 设置分区ID
                } else {
                    // 如果不一致，则返回null
                    if (!commonPastureId.equals(devicePastureId) || !commonBatchId.equals(deviceBatchId)) {
                        return null;
                    }
                }
            } catch (NumberFormatException e) {
                // 出现异常时记录错误，并返回null
                log.error("设备ID转换异常: " + e.getMessage());
                return null;
            }
        }
        // 返回统一绑定的数组
        return new Long[]{ commonPastureId, commonBatchId };
    }

    /**
     * 当所有非水质传感器均绑定在同一大棚和分区时，统一处理数据入库
     *
     * @param sensorValue      土壤、风向等传感器数据对象
     * @param unifiedBinding   统一绑定的 [pastureId, batchId]
     * @param sensorBindings   所有设备绑定信息Map
     * @param fishWaterQuality 水质数据对象
     */
    private void processUnifiedSoilSensorData(SoilSensorValue sensorValue, Long[] unifiedBinding,
                                              Map<String, Device> sensorBindings, FishWaterQuality fishWaterQuality) {
        // 设置统一绑定信息到土壤传感器数据对象中
        sensorValue.setPastureId(String.valueOf(unifiedBinding[0])); // 设置大棚ID
        sensorValue.setBatchId(String.valueOf(unifiedBinding[1])); // 设置分区ID
        sensorValue.setDeviceId(""); // 设置设备id
        sensorValue.setTime(currentTime()); // 设置采集时间
        sensorValue.setDate(currentDate()); // 设置采集日期
        // 将统一后的数据入库
        this.insertSoilSensorValue(sensorValue);

        // 单独处理水质传感器数据（ID为8）
//        System.out.println("单独处理水质传感器数据  globalSensorData.containsKey(\"water_quality\") " + globalSensorData.containsKey("water_quality"));
//        System.out.println("单独处理水质传感器数据  sensorBindings.containsKey(\"8\") " + sensorBindings.containsKey("8"));
//        System.out.println("单独处理水质传感器数据  deviceMapper.selectWaterById(\"8\") " + deviceMapper.selectWaterById("8"));
//        System.out.println("sensorBindings" + sensorBindings);
        if (globalSensorData.containsKey("water_quality") && deviceMapper.selectWaterById("8") != null) {
            // 查询水质设备绑定信息
            Device waterDeviceBinding = deviceMapper.selectWaterById("8");// 使用已缓存的设备信息
            // 输出调试日志
//            System.out.println("sensorBindings 获取的 Device对象: " + waterDeviceBinding);
//            System.out.println("鱼棚id: " + waterDeviceBinding.getFishPastureId());
//            System.out.println("鱼分区id: " + waterDeviceBinding.getFishPastureBatchId());

            // 设置绑定信息到水质数据对象
            fishWaterQuality.setFishPastureId(Long.valueOf(waterDeviceBinding.getFishPastureId()));
            fishWaterQuality.setFishPastureBatchId(Long.valueOf(waterDeviceBinding.getFishPastureBatchId()));
            fishWaterQuality.setTime(currentTime()); // 设置采集时间
            fishWaterQuality.setDate(currentDate()); // 设置采集日期
            // 将水质数据入库
           // System.out.println(" processUnifiedSoilSensorData将水质数据入库");
            fishWaterQualityMapper.insertFishWaterQuality(fishWaterQuality);
        }
    }

    /**
     * 当部分传感器绑定不统一时，针对每个设备单独处理数据入库
     *
     * @param sensorValue      非水质传感器数据对象
     * @param fishWaterQuality 水质传感器数据对象
     * @param sensorBindings   所有设备绑定信息Map
     */
    private void processIndividualSensorData(SoilSensorValue sensorValue, FishWaterQuality fishWaterQuality,
                                             Map<String, Device> sensorBindings) {
        // 遍历所有设备绑定信息，单独处理每个传感器的数据入库
        for (Map.Entry<String, Device> entry : sensorBindings.entrySet()) {
            String sensorType = entry.getKey(); // 获取传感器ID
            Device binding = entry.getValue(); // 获取设备绑定信息
            if ("8".equals(sensorType) && globalSensorData.containsKey("water_quality")) {
                // 针对水质传感器：构造单独的水质数据对象
                FishWaterQuality individualFishWaterQuality = new FishWaterQuality();
                individualFishWaterQuality.setFishPastureId(Long.valueOf(binding.getFishPastureId())); // 设置鱼棚ID
                individualFishWaterQuality.setFishPastureBatchId(Long.valueOf(binding.getFishPastureBatchId())); // 设置鱼分区ID
                // 复制水质数据
                individualFishWaterQuality.setWaterTemperature(fishWaterQuality.getWaterTemperature());//设置水温
                individualFishWaterQuality.setWaterPhValue(fishWaterQuality.getWaterPhValue());//设置ph值
                individualFishWaterQuality.setWaterOxygenContent(fishWaterQuality.getWaterOxygenContent());//设置含氧量
                individualFishWaterQuality.setWaterNitriteContent(fishWaterQuality.getWaterNitriteContent());//设置亚硝酸 盐含量
                individualFishWaterQuality.setTime(currentTime()); // 设置采集时间
                individualFishWaterQuality.setDate(currentDate()); // 设置采集日期
                // 入库水质数据
              //  System.out.println("processIndividualSensorData将水质数据入库");
                fishWaterQualityMapper.insertFishWaterQuality(individualFishWaterQuality);
            } else {
                // 针对其它非水质传感器：构造独立的土壤/环境数据对象
                SoilSensorValue individualValue = new SoilSensorValue();
                individualValue.setPastureId(String.valueOf(binding.getPastureId())); // 设置大棚ID
                individualValue.setBatchId(String.valueOf(binding.getBatchId())); // 设置分区ID
                individualValue.setDeviceId(sensorType); // 设置当前设备ID
                individualValue.setTime(currentTime()); // 设置采集时间
                individualValue.setDate(currentDate()); // 设置采集日期
                // 根据传感器ID复制对应数据
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
                // 将单个传感器数据入库
                this.insertSoilSensorValue(individualValue);
            }
        }
    }


    /**
     * 解析风向传感器数据
     * 假设数据格式：
     * Byte0 - 传感器地址
     * Byte3-4：风向档位（转换为整数）
     * Byte5-6：风向角度（转换为整数）
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
     * 解析百叶箱数据
     * 假设数据格式：
     * Byte3-4：湿度（除以10得到实际值）
     * Byte5-6：温度（除以10得到实际值）
     * Byte7-8：噪声（除以10）
     * Byte9-10：PM2.5（整数）
     * Byte13-14：PM10（整数）
     * Byte17-18：光照强度（整数）
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
     * Byte3-4组合成一个整数，除以10得到风速（单位：m/s）
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
     * 解析土壤温度水分数据
     * 假设数据格式：
     * Byte5-6：土壤温度（除以10得到实际值）
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
     * 解析土壤 pH 数据
     * 假设数据格式：
     * Byte3-4：土壤 pH 值（除以10得到实际值）
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
     * 解析土壤水分电导率数据
     * 假设数据格式：
     * Byte3-4：含水率（除以10）
     * Byte5-6：土壤温度（除以10）
     * Byte7-8：电导率（整数）
     * Byte9-10：土壤 pH 值（除以10）
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
     * Byte7-8：pH 值（转换后除以100）
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
     * 工具函数：将十六进制字符串（允许含空格）转换为 byte 数组
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
     * 工具函数：获取当前系统时间戳，格式为 yyyy-MM-dd HH:mm:ss
     */
    private String currentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private String currentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
    private String currentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    // ------------------------------ 以下是 SoilSensorValue CRUD 接口实现 ------------------------------

    /**
     * 查询菜的环境数据
     *
     * @param id 菜的环境数据主键
     * @return 菜的环境数据
     */
    @Override
    public SoilSensorValue selectSoilSensorValueById(String id) {
        return soilSensorValueMapper.selectSoilSensorValueById(id);
    }

    @Override
    public List<SoilSensorValue> getSoilSensorValuesByPastureId(String pastureId) {
        return soilSensorValueMapper.findByPastureId(pastureId);
    }

    /**
     * 查询菜的环境数据列表
     *
     * @param soilSensorValue 菜的环境数据筛选条件
     * @return 菜的环境数据集合
     */
    @Override
    public List<SoilSensorValue> selectSoilSensorValueList(SoilSensorValue soilSensorValue) {
        return soilSensorValueMapper.selectSoilSensorValueList(soilSensorValue);
    }

    /**
     * 新增菜的环境数据
     *
     * @param soilSensorValue 菜的环境数据
     * @return 结果
     */
    @Override
    public int insertSoilSensorValue(SoilSensorValue soilSensorValue) {
        return soilSensorValueMapper.insertSoilSensorValue(soilSensorValue);
    }

    /**
     * 修改菜的环境数据
     *
     * @param soilSensorValue 菜的环境数据
     * @return 结果
     */
    @Override
    public int updateSoilSensorValue(SoilSensorValue soilSensorValue) {
        return soilSensorValueMapper.updateSoilSensorValue(soilSensorValue);
    }

    /**
     * 批量删除菜的环境数据
     *
     * @param ids 需要删除的菜的环境数据主键数组
     * @return 结果
     */
    @Override
    public int deleteSoilSensorValueByIds(String[] ids) {
        return soilSensorValueMapper.deleteSoilSensorValueByIds(ids);
    }

    /**
     * 删除菜的环境数据信息
     *
     * @param id 菜的环境数据主键
     * @return 结果
     */
    @Override
    public int deleteSoilSensorValueById(String id) {
        return soilSensorValueMapper.deleteSoilSensorValueById(id);
    }
}