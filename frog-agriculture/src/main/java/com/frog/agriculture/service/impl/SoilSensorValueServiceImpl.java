package com.frog.agriculture.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import com.frog.agriculture.domain.SensorAlert;
import com.frog.agriculture.mapper.SensorAlertMapper;

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

    // 注入预警信息Mapper
    @Autowired
    private SensorAlertMapper sensorAlertMapper;

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

    // 传感器阈值配置，key为传感器参数名称，value为阈值上下限
    private Map<String, double[]> thresholdConfig = new HashMap<>();

    /**
     * 初始化方法，系统启动时调用
     */
    @PostConstruct
    public void init() {
        // 初始化串口工具，配置与Python代码相同的参数
        serialPortUtil = new SerialPortUtil();

        // 从数据库中加载传感器指令
        initializeSensorCommandsFromDB();
        // 初始化传感器阈值配置
        initializeThresholdConfig();
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

    /**
     * 初始化传感器阈值配置
     * 可以从数据库加载或直接配置
     */
    private void initializeThresholdConfig() {
        // 配置格式：[最小值, 最大值]，超出范围则触发预警
        // 温度阈值（摄氏度）
        thresholdConfig.put("temperature", new double[]{10.0, 35.0});
        // 湿度阈值（百分比）
        thresholdConfig.put("humidity", new double[]{30.0, 80.0});
        // 光照阈值（lux）
        thresholdConfig.put("light", new double[]{80, 10000.0});
        // 风速阈值（m/s）
        thresholdConfig.put("speed", new double[]{0.0, 10.0});
        // 土壤温度阈值（摄氏度）
        thresholdConfig.put("soil_temperature", new double[]{5.0, 30.0});
        // 土壤pH阈值
        thresholdConfig.put("soil_ph", new double[]{5.0, 7.5});
        // 土壤电导率阈值（μS/cm）
        thresholdConfig.put("conductivity", new double[]{100.0, 2000.0});
        // 土壤水分阈值（百分比）
        thresholdConfig.put("moisture", new double[]{20.0, 60.0});
        // 水温阈值（摄氏度）
        thresholdConfig.put("water_temperature", new double[]{15.0, 30.0});
        // 水pH阈值
        thresholdConfig.put("water_ph", new double[]{6.5, 8.5});
        // 溶解氧阈值（mg/L）
        thresholdConfig.put("oxygen", new double[]{5.0, 8.0});
        // 氨氮阈值（mg/L）
        thresholdConfig.put("ammonia", new double[]{0.0, 0.02});
        // 亚硝酸盐阈值（mg/L）
        thresholdConfig.put("nitrite", new double[]{0.0, 0.1});
        
        log.info("传感器阈值配置初始化完成，共配置" + thresholdConfig.size() + "个参数阈值");
    }

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

        // 使用串口锁确保串口操作的线程安全
        synchronized (serialPortUtil) {
            // 遍历所有传感器指令，依次采集数据
            for (Map.Entry<String, String> entry : sensorCommands.entrySet()) {
                String sensorType = entry.getKey();
                String hexCommand = entry.getValue();

                if (hexCommand == null || hexCommand.trim().isEmpty()) {
                    log.warn("传感器" + sensorType + "指令为空，跳过采集");
                    continue;
                }

                try {
                    // 发送命令到传感器
                    byte[] command = hexStringToByteArray(hexCommand);
                    serialPortUtil.writeBytes(command);
                    
                    // 等待传感器响应
                    Thread.sleep(100);
                    
                    // 读取传感器响应数据
                    byte[] response = serialPortUtil.readBytes();
                    
                    if (response != null && response.length > 0) {
                        // 解析传感器数据
                        processSensorDataBySwitch(sensorType, response, sensorValue, fishWaterQuality);
                        currentRunStatus.put(sensorType, true);
                        statusTracker.recordSuccess(sensorType);
                    } else {
                        log.warn("传感器" + sensorType + "未返回数据");
                        currentRunStatus.put(sensorType, false);
                        statusTracker.recordFailure(sensorType);
                    }
                } catch (Exception e) {
                    valid = false;
                    log.error("传感器" + sensorType + "数据采集异常：" + e.getMessage());
                    currentRunStatus.put(sensorType, false);
                    statusTracker.recordFailure(sensorType);
                }
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
        
        // 检查数据是否超过阈值，生成预警信息
        checkThresholdsAndGenerateAlerts(sensorValue, fishWaterQuality, sensorBindings);

        // 将全局传感器数据转换为JSON字符串，打印日志便于调试
        Gson gson = new Gson();
        String jsonOutput = gson.toJson(globalSensorData);
        System.out.println("传感器最新数据：" + jsonOutput);
    }

    /**
     * 根据传感器状态更新设备状态，并记录在线/离线状态到数据库中
     * 只有当设备连续多次（默认3次）采集失败时才标记为离线
     *
     * @param statusMap 传感器状态映射，key为传感器ID，value为是否在线
     */
    private void updateDeviceStatus(Map<String, Boolean> statusMap) {
        final int OFFLINE_THRESHOLD = 3; // 连续失败次数阈值，超过此值才标记为离线
        
        statusMap.forEach((sensorType, isOnline) -> {
            try {
                // 从缓存或数据库中获取设备信息
                Device device = getCachedDevice(sensorType);
                if (device == null) {
                    log.warn("未找到传感器类型为" + sensorType + "的设备");
                    return;
                }

                String currentStatus = device.getStatus();
                String newStatus = currentStatus; // 默认保持当前状态

                if (isOnline) {
                    // 如果数据采集成功，直接标记为在线
                    newStatus = "1";
                    statusTracker.resetFailureCount(sensorType); // 重置失败计数
                } else {
                    // 数据采集失败，增加失败计数
                    int failureCount = statusTracker.getFailureCount(sensorType);
                    if (failureCount >= OFFLINE_THRESHOLD) {
                        // 只有连续失败次数达到阈值才标记为离线
                        newStatus = "0";
                    }
                }

                // 状态发生变化时才更新数据库
                if (!newStatus.equals(currentStatus)) {
                    device.setStatus(newStatus);
                    deviceMapper.updateById(device);
                    log.info("设备:" + device.getDeviceId() + " 状态更新为" + (newStatus.equals("1") ? "在线" : "离线"));
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
        Map<String, Object> parsedData = new HashMap<>();
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
            /* 暂时注释掉水质传感器处理
            case "8": // 水质传感器
                parsedData = parseWaterQualityData(response);
                globalSensorData.put("water_quality", parsedData);
                fishWaterQuality.setWaterTemperature(parsedData.get("temperature").toString());
                fishWaterQuality.setWaterPhValue(parsedData.get("ph_value").toString());
                fishWaterQuality.setDeviceId(null);
                
                // 生成溶解氧值
                double oxygen = ThreadLocalRandom.current().nextDouble(6.2, 6.5);
                oxygen = Math.round(oxygen * 100.0) / 100.0;
                fishWaterQuality.setWaterOxygenContent(String.format("%.2f", oxygen));

                // 生成氨氮含量
                double ammonia = ThreadLocalRandom.current().nextDouble(0.01, 0.015);
                ammonia = Math.round(ammonia * 1000.0) / 1000.0;
                fishWaterQuality.setWaterAmmoniaNitrogenContent(String.format("%.3f", ammonia));

                // 生成亚硝酸盐含量
                double nitrite = ThreadLocalRandom.current().nextDouble(0.03, 0.05);
                nitrite = Math.floor(nitrite * 100) / 100.0;
                fishWaterQuality.setWaterNitriteContent(String.format("%.2f", nitrite));

                fishWaterQuality.setTime(currentTime());
                fishWaterQuality.setDate(currentDate());
                break;
            */
            default:
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
                individualValue.setDeviceId("");
                individualValue.setTime(currentTime());
                individualValue.setDate(currentDate());
                // 根据传感器类型复制对应的数据字段
                switch (sensorType) {
                    case "1": // 风向传感器
                        individualValue.setDirection(sensorValue.getDirection());
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
     * 解析百叶箱传感器数据
     */
    private Map<String, Object> parseBaiyeBoxData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            // 湿度 = (byte3 << 8 | byte4) / 10.0
            double humidity = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF)) / 10.0;
            // 温度 = (byte5 << 8 | byte6) / 10.0
            double temperature = ((data[5] & 0xFF) << 8 | (data[6] & 0xFF)) / 10.0;
            // 噪音 = (byte7 << 8 | byte8) / 10.0
            double noise = ((data[7] & 0xFF) << 8 | (data[8] & 0xFF)) / 10.0;
            // PM2.5 = byte9 << 8 | byte10
            int pm25 = ((data[9] & 0xFF) << 8 | (data[10] & 0xFF));
            // PM10 = byte13 << 8 | byte14
            int pm10 = ((data[13] & 0xFF) << 8 | (data[14] & 0xFF));
            // 光照 = byte17 << 8 | byte18
            int light = ((data[17] & 0xFF) << 8 | (data[18] & 0xFF));
            
            map.put("humidity", humidity);
            map.put("temperature", temperature);
            map.put("noise", noise);
            map.put("pm25", pm25);
            map.put("pm10", pm10);
            map.put("light", light);
        } catch (Exception e) {
            log.error("解析百叶箱数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析土壤温度水分传感器数据
     */
    private Map<String, Object> parseSoilTemperatureMoistureData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            // 水分值 = (byte3 << 8 | byte4) / 10.0
            double moisture = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF)) / 10.0;
            
            // 温度值处理，考虑负温度情况
            int tempValue = ((data[5] & 0xFF) << 8 | (data[6] & 0xFF));
            if (tempValue > 32767) {
                tempValue = tempValue - 65536;
            }
            double temperature = tempValue / 10.0;
            
            map.put("soil_temperature", temperature);
            map.put("moisture", moisture);
        } catch (Exception e) {
            log.error("解析土壤温度水分数据出错: " + e.getMessage());
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
     */
    private Map<String, Object> parseSoilMoistureConductivityData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            // 水分值 = (byte3 << 8 | byte4) / 10.0
            double moisture = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF)) / 10.0;
            
            // 温度值处理，考虑负温度情况
            int tempValue = ((data[5] & 0xFF) << 8 | (data[6] & 0xFF));
            if (tempValue > 32767) {
                tempValue = tempValue - 65536;
            }
            double temperature = tempValue / 10.0;
            
            // 电导率 = byte7 << 8 | byte8
            int conductivity = ((data[7] & 0xFF) << 8 | (data[8] & 0xFF));
            
            map.put("moisture", moisture);
            map.put("soil_temperature", temperature);
            map.put("conductivity", conductivity);
        } catch (Exception e) {
            log.error("解析土壤水分电导率数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析水质传感器数据
     */
    private Map<String, Object> parseWaterQualityData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            // 温度值和小数位处理
            int tempValue = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF));
            int tempDecimal = ((data[5] & 0xFF) << 8 | (data[6] & 0xFF));
            double temperature = tempValue / Math.pow(10, tempDecimal);
            temperature = Math.round(temperature * Math.pow(10, tempDecimal)) / Math.pow(10, tempDecimal);
            
            // pH值 = (byte7 << 8 | byte8) / 100.0
            double phValue = ((data[7] & 0xFF) << 8 | (data[8] & 0xFF)) / 100.0;
            
            map.put("temperature", temperature);
            map.put("ph_value", phValue);
        } catch (Exception e) {
            log.error("解析水质传感器数据出错: " + e.getMessage());
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

    /**
     * 检查传感器数据是否超过阈值，并生成预警信息
     * 
     * @param sensorValue 土壤环境数据对象
     * @param fishWaterQuality 水质数据对象
     * @param sensorBindings 传感器绑定信息
     */
    private void checkThresholdsAndGenerateAlerts(SoilSensorValue sensorValue, 
                                                 FishWaterQuality fishWaterQuality,
                                                 Map<String, Device> sensorBindings) {
        // 检查土壤和环境数据
        checkAndAlertForSoilData(sensorValue, sensorBindings);
        
        // 检查水质数据
        if (fishWaterQuality != null && fishWaterQuality.getWaterTemperature() != null) {
            checkAndAlertForWaterData(fishWaterQuality, sensorBindings.get("8"));
        }
    }
    
    /**
     * 检查土壤和环境数据是否超过阈值并生成预警
     * 
     * @param sensorValue 土壤环境数据对象
     * @param sensorBindings 传感器绑定信息
     */
    private void checkAndAlertForSoilData(SoilSensorValue sensorValue, Map<String, Device> sensorBindings) {
        // 检查温度
        if (sensorValue.getTemperature() != null) {
            try {
                double temperature = Double.parseDouble(sensorValue.getTemperature());
                checkThresholdAndAlert("temperature", temperature, "温度", 
                        sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("2"));
            } catch (NumberFormatException e) {
                log.warn("温度数据格式错误: " + sensorValue.getTemperature());
            }
        }
        
        // 检查湿度
        if (sensorValue.getHumidity() != null) {
            try {
                double humidity = Double.parseDouble(sensorValue.getHumidity());
                checkThresholdAndAlert("humidity", humidity, "湿度", 
                        sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("2"));
            } catch (NumberFormatException e) {
                log.warn("湿度数据格式错误: " + sensorValue.getHumidity());
            }
        }
        
        // 检查光照
        if (sensorValue.getLightLux() != null) {
            try {
                double light = Double.parseDouble(sensorValue.getLightLux());
                checkThresholdAndAlert("light", light, "光照", 
                        sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("2"));
            } catch (NumberFormatException e) {
                log.warn("光照数据格式错误: " + sensorValue.getLightLux());
            }
        }

        
        // 检查风速
        if (sensorValue.getSpeed() != null) {
            try {
                double speed = Double.parseDouble(sensorValue.getSpeed());
                checkThresholdAndAlert("speed", speed, "风速", 
                        sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("3"));
            } catch (NumberFormatException e) {
                log.warn("风速数据格式错误: " + sensorValue.getSpeed());
            }
        }
        
        // 检查土壤温度
        if (sensorValue.getSoilTemperature() != null) {
            try {
                double soilTemp = Double.parseDouble(sensorValue.getSoilTemperature());
                checkThresholdAndAlert("soil_temperature", soilTemp, "土壤温度", 
                        sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("4"));
            } catch (NumberFormatException e) {
                log.warn("土壤温度数据格式错误: " + sensorValue.getSoilTemperature());
            }
        }
        
        // 检查土壤pH
        if (sensorValue.getSoilPh() != null) {
            try {
                double soilPh = Double.parseDouble(sensorValue.getSoilPh());
                checkThresholdAndAlert("soil_ph", soilPh, "土壤pH", 
                        sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("5"));
            } catch (NumberFormatException e) {
                log.warn("土壤pH数据格式错误: " + sensorValue.getSoilPh());
            }
        }
        
        // 检查土壤电导率
        if (sensorValue.getSoilConductivity() != null) {
            try {
                double conductivity = Double.parseDouble(sensorValue.getSoilConductivity());
                checkThresholdAndAlert("conductivity", conductivity, "土壤电导率", 
                        sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("6"));
            } catch (NumberFormatException e) {
                log.warn("土壤电导率数据格式错误: " + sensorValue.getSoilConductivity());
            }
        }
        
        // 检查土壤水分
        if (sensorValue.getSoilMoisture() != null) {
            try {
                double moisture = Double.parseDouble(sensorValue.getSoilMoisture());
                checkThresholdAndAlert("moisture", moisture, "土壤水分", 
                        sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("6"));
            } catch (NumberFormatException e) {
                log.warn("土壤水分数据格式错误: " + sensorValue.getSoilMoisture());
            }
        }
    }
    
    /**
     * 检查水质数据是否超过阈值并生成预警
     * 
     * @param fishWaterQuality 水质数据对象
     * @param waterDevice 水质传感器设备信息
     */
    private void checkAndAlertForWaterData(FishWaterQuality fishWaterQuality, Device waterDevice) {
        // 检查水温
        if (fishWaterQuality.getWaterTemperature() != null) {
            try {
                double waterTemp = Double.parseDouble(fishWaterQuality.getWaterTemperature());
                checkThresholdAndAlert("water_temperature", waterTemp, "水温", 
                        String.valueOf(fishWaterQuality.getFishPastureId()), 
                        String.valueOf(fishWaterQuality.getFishPastureBatchId()), 
                        waterDevice);
            } catch (NumberFormatException e) {
                log.warn("水温数据格式错误: " + fishWaterQuality.getWaterTemperature());
            }
        }
        
        // 检查水pH
        if (fishWaterQuality.getWaterPhValue() != null) {
            try {
                double waterPh = Double.parseDouble(fishWaterQuality.getWaterPhValue());
                checkThresholdAndAlert("water_ph", waterPh, "水pH", 
                        String.valueOf(fishWaterQuality.getFishPastureId()), 
                        String.valueOf(fishWaterQuality.getFishPastureBatchId()), 
                        waterDevice);
            } catch (NumberFormatException e) {
                log.warn("水pH数据格式错误: " + fishWaterQuality.getWaterPhValue());
            }
        }
        
        // 检查溶解氧
        if (fishWaterQuality.getWaterOxygenContent() != null) {
            try {
                double oxygen = Double.parseDouble(fishWaterQuality.getWaterOxygenContent());
                checkThresholdAndAlert("oxygen", oxygen, "溶解氧", 
                        String.valueOf(fishWaterQuality.getFishPastureId()), 
                        String.valueOf(fishWaterQuality.getFishPastureBatchId()), 
                        waterDevice);
            } catch (NumberFormatException e) {
                log.warn("溶解氧数据格式错误: " + fishWaterQuality.getWaterOxygenContent());
            }
        }
        
        // 检查氨氮
        if (fishWaterQuality.getWaterAmmoniaNitrogenContent() != null) {
            try {
                double ammonia = Double.parseDouble(fishWaterQuality.getWaterAmmoniaNitrogenContent());
                checkThresholdAndAlert("ammonia", ammonia, "氨氮", 
                        String.valueOf(fishWaterQuality.getFishPastureId()), 
                        String.valueOf(fishWaterQuality.getFishPastureBatchId()), 
                        waterDevice);
            } catch (NumberFormatException e) {
                log.warn("氨氮数据格式错误: " + fishWaterQuality.getWaterAmmoniaNitrogenContent());
            }
        }
        
        // 检查亚硝酸盐
        if (fishWaterQuality.getWaterNitriteContent() != null) {
            try {
                double nitrite = Double.parseDouble(fishWaterQuality.getWaterNitriteContent());
                checkThresholdAndAlert("nitrite", nitrite, "亚硝酸盐", 
                        String.valueOf(fishWaterQuality.getFishPastureId()), 
                        String.valueOf(fishWaterQuality.getFishPastureBatchId()), 
                        waterDevice);
            } catch (NumberFormatException e) {
                log.warn("亚硝酸盐数据格式错误: " + fishWaterQuality.getWaterNitriteContent());
            }
        }
    }
    
    /**
     * 检查单个参数是否接近或超过阈值，并生成预警信息
     */
    private void checkThresholdAndAlert(String paramKey, double value, String paramName, 
                                       String pastureId, String batchId, Device device) {
        // 获取参数阈值配置
        double[] thresholds = thresholdConfig.get(paramKey);
        if (thresholds == null) {
            log.warn("未找到参数 " + paramKey + " 的阈值配置");
            return;
        }
        
        // 定义接近阈值的警戒范围
        double minBuffer, maxBuffer;
        
        // 根据参数类型设置不同的警戒范围计算方式
        switch (paramKey) {
            case "temperature": // 温度 10-35℃
                minBuffer = 2.0; // 低于12℃预警
                maxBuffer = 3.0; // 高于32℃预警
                break;
            case "humidity": // 湿度 30-80%
                minBuffer = 5.0; // 低于35%预警
                maxBuffer = 5.0; // 高于75%预警
                break;
            case "light": // 光照 80-10000 lux
                minBuffer = 40.0; // 低于120 lux预警
                maxBuffer = 1000.0; // 高于9000 lux预警
                break;
            case "speed": // 风速 0-10 m/s
                minBuffer = 0.0; // 不预警低值
                maxBuffer = 1.5; // 高于8.5 m/s预警
                break;
            case "soil_temperature": // 土壤温度 5-30℃
                minBuffer = 2.0; // 低于7℃预警
                maxBuffer = 2.0; // 高于28℃预警
                break;
            case "soil_ph": // 土壤pH 5.0-7.5
                minBuffer = 0.3; // 低于5.3预警
                maxBuffer = 0.3; // 高于7.2预警
                break;
            case "conductivity": // 土壤电导率 100-2000 μS/cm
                minBuffer = 50.0; // 低于150预警
                maxBuffer = 200.0; // 高于1800预警
                break;
            case "moisture": // 土壤水分 20-60%
                minBuffer = 5.0; // 低于25%预警
                maxBuffer = 5.0; // 高于55%预警
                break;
            case "water_temperature": // 水温 15-30℃
                minBuffer = 2.0; // 低于17℃预警
                maxBuffer = 2.0; // 高于28℃预警
                break;
            case "water_ph": // 水pH 6.5-8.5
                minBuffer = 0.3; // 低于6.8预警
                maxBuffer = 0.3; // 高于8.2预警
                break;
            case "oxygen": // 溶解氧 5.0-8.0 mg/L
                minBuffer = 0.5; // 低于5.5预警
                maxBuffer = 0.5; // 高于7.5预警
                break;
            case "ammonia": // 氨氮 0-0.02 mg/L
                minBuffer = 0.0; // 不预警低值
                maxBuffer = 0.005; // 高于0.015预警
                break;
            case "nitrite": // 亚硝酸盐 0-0.1 mg/L
                minBuffer = 0.0; // 不预警低值
                maxBuffer = 0.02; // 高于0.08预警
                break;
            default:
                minBuffer = 0.0;
                maxBuffer = 0.0;
        }
        
        // 计算警戒范围
        double minWarning = thresholds[0] + minBuffer;
        double maxWarning = thresholds[1] - maxBuffer;
        
        String alertType = null;
        String alertMessage = null;

        // 检查是否超出或接近阈值范围
        if (value < thresholds[0]) {
            alertType = "低于阈值";
            serialPortUtil.sendMultipleRelays();//打开报警
            alertMessage = paramName + "过低: " + value + ", 最低阈值: " + thresholds[0];
        } else if (value > thresholds[1]) {
            alertType = "超过阈值";
            alertMessage = paramName + "过高: " + value + ", 最高阈值: " + thresholds[1];

        } else if (paramKey.equals("speed") || paramKey.equals("ammonia") || paramKey.equals("nitrite")) {
            // 这些参数只在接近高阈值时预警
            if (value > maxWarning) {
                alertType = "接近最高阈值";
                alertMessage = paramName + "接近最高阈值,目前值为：" + value + ", 警戒值为: " + maxWarning;
            }
        } else if (value < minWarning) {
            alertType = "接近最低阈值";
            alertMessage = paramName + "接近最低阈值，目前值为：" + value + ", 警戒值为: " + minWarning;
        } else if (value > maxWarning) {
            alertType = "接近最高阈值";
            alertMessage = paramName + "接近最高阈值，目前值为：" + value + ", 警戒值为: " + maxWarning;
        }
        
        // 如果需要生成预警，先检查是否存在未处理的相同类型预警
        if (alertType != null) {
            // 检查是否已存在未处理的相同类型预警
            if (hasActiveAlert(paramName, alertType, pastureId, batchId, device)) {
                log.info("已存在未处理的" + paramName + alertType + "预警，不重复生成");
                return;
            }
            
            SensorAlert alert = new SensorAlert();
            alert.setAlertType(alertType);
            alert.setAlertMessage(alertMessage);
            alert.setParamName(paramName);
            alert.setParamValue(String.valueOf(value));
            alert.setThresholdMin(String.valueOf(thresholds[0]));
            alert.setThresholdMax(String.valueOf(thresholds[1]));
            alert.setPastureId(pastureId);
            alert.setBatchId(batchId);
            
            // 根据参数类型判断pastureType
            if (paramKey.startsWith("water_") || 
                paramKey.equals("oxygen") || 
                paramKey.equals("ammonia") || 
                paramKey.equals("nitrite")) {
                alert.setPastureType("1"); // 鱼棚
            } else {
                alert.setPastureType("0"); // 大棚
            }
            
            // 设置设备信息
            if (device != null) {
                alert.setDeviceId(device.getDeviceId());
                alert.setDeviceName(device.getDeviceName());
                alert.setSensorType(device.getSensorType());
            }
            
            // 设置时间信息
            alert.setAlertTime(currentTimestamp());
            alert.setAlertDate(currentDate());
            alert.setStatus("0"); // 0表示未处理
            
            // 保存预警信息到数据库
            try {
                sensorAlertMapper.insertSensorAlert(alert);
                log.info("生成预警信息: " + alertMessage);
            } catch (Exception e) {
                log.error("保存预警信息失败: " + e.getMessage());
            }
        }
    }

    /**
     * 检查是否已存在未处理的相同类型预警
     */
    private boolean hasActiveAlert(String paramName, String alertType, String pastureId, String batchId, Device device) {
        try {
            // 构造查询条件
            SensorAlert queryAlert = new SensorAlert();
            queryAlert.setParamName(paramName);
            queryAlert.setAlertType(alertType);
            queryAlert.setPastureId(pastureId);
            queryAlert.setBatchId(batchId);
            queryAlert.setStatus("0"); // 0表示未处理
            
            // 设置pastureType
            if (paramName.startsWith("水") || 
                paramName.equals("溶解氧") || 
                paramName.equals("氨氮") || 
                paramName.equals("亚硝酸盐")) {
                queryAlert.setPastureType("1"); // 鱼棚
            } else {
                queryAlert.setPastureType("0"); // 大棚
            }
            
            // 如果设备信息不为空，添加设备相关条件
            if (device != null) {
                queryAlert.setSensorType(device.getSensorType());
            }
            
            // 查询是否存在符合条件的未处理预警
            List<SensorAlert> existingAlerts = sensorAlertMapper.selectSensorAlertList(queryAlert);
            
            return existingAlerts != null && !existingAlerts.isEmpty();
        } catch (Exception e) {
            log.error("查询现有预警失败: " + e.getMessage());
            return false;
        }
    }
}