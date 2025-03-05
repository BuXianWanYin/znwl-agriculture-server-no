package com.frog.agriculture.service.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;

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
import com.frog.agriculture.mapper.SensorAlertMapper;
import com.frog.agriculture.utils.AlertProcessUtil;
import com.frog.agriculture.utils.SensorDataParser;
import com.frog.agriculture.utils.ThresholdConfigUtil;
import com.frog.agriculture.utils.DeviceStatusUtil;

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
        serialPortUtil = new SerialPortUtil();
        DeviceStatusUtil.initializeSensorCommands(deviceMapper, sensorCommands);
        ThresholdConfigUtil.initializeThresholdConfig();
        thresholdConfig = ThresholdConfigUtil.getThresholdConfig();
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
                    byte[] command = SensorDataParser.hexStringToByteArray(hexCommand);
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

        // 使用DeviceStatusUtil更新设备状态
        DeviceStatusUtil.updateDeviceStatus(statusTracker.getCurrentStatus(), 
                                          deviceMapper, 
                                          deviceCache, 
                                          statusTracker);

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
        AlertProcessUtil.checkThresholdsAndGenerateAlerts(sensorValue, fishWaterQuality, 
            sensorBindings);

        // 将全局传感器数据转换为JSON字符串，打印日志便于调试
        Gson gson = new Gson();
        String jsonOutput = gson.toJson(globalSensorData);
        System.out.println("传感器最新数据：" + jsonOutput);
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
     * @param sensorType       传感器ID
     * @param response         传感器返回的原始数据
     * @param sensorValue      非水质传感器数据对象
     * @param fishWaterQuality 水质传感器数据对象
     */
    private void processSensorDataBySwitch(String sensorType, byte[] response,
                                           SoilSensorValue sensorValue, FishWaterQuality fishWaterQuality) {
        Map<String, Object> parsedData = new HashMap<>();
        switch (sensorType) {
            case "1": // 风向传感器
                parsedData = SensorDataParser.parseWindDirectionData(response);
                globalSensorData.put("wind_direction", parsedData);
                sensorValue.setDirection(parsedData.get("direction").toString());
                break;

            case "2": // 百叶箱传感器
                parsedData = SensorDataParser.parseBaiyeBoxData(response);
                globalSensorData.put("baiye_box", parsedData);
                sensorValue.setTemperature(parsedData.get("temperature").toString());
                sensorValue.setHumidity(parsedData.get("humidity").toString());
                sensorValue.setLightLux(parsedData.get("light").toString());
                break;

            case "3": // 风速传感器
                parsedData = SensorDataParser.parseWindSpeedData(response);
                globalSensorData.put("wind_speed", parsedData);
                sensorValue.setSpeed(parsedData.get("speed").toString());
                break;
            case "4": // 土壤温度和水分传感器
                parsedData = SensorDataParser.parseSoilTemperatureMoistureData(response);
                globalSensorData.put("soil_temperature_moisture", parsedData);
                sensorValue.setSoilTemperature(parsedData.get("soil_temperature").toString());
                break;
            case "5": // 土壤pH传感器
                parsedData = SensorDataParser.parseSoilPHData(response);
                globalSensorData.put("soil_ph", parsedData);
                sensorValue.setSoilPh(parsedData.get("soil_ph").toString());
                break;
            case "6": // 土壤水分电导率传感器
                parsedData = SensorDataParser.parseSoilMoistureConductivityData(response);
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
     *
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
        return new Long[]{commonPastureId, commonBatchId};
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
        sensorValue.setTime(AlertProcessUtil.currentTime());
        sensorValue.setDate(AlertProcessUtil.currentDate());
        // 入库非水质传感器数据
        this.insertSoilSensorValue(sensorValue);

        // 单独处理水质传感器数据（ID为8），需查询水质设备对应的绑定信息
        if (globalSensorData.containsKey("water_quality") && deviceMapper.selectWaterById("8") != null) {
            Device waterDeviceBinding = deviceMapper.selectWaterById("8");
            // 设置水质设备对应的鱼棚和分区信息
            fishWaterQuality.setFishPastureId(Long.valueOf(waterDeviceBinding.getFishPastureId()));
            fishWaterQuality.setFishPastureBatchId(Long.valueOf(waterDeviceBinding.getFishPastureBatchId()));
            fishWaterQuality.setTime(AlertProcessUtil.currentTime());
            fishWaterQuality.setDate(AlertProcessUtil.currentDate());
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
                individualFishWaterQuality.setTime(AlertProcessUtil.currentTime());
                individualFishWaterQuality.setDate(AlertProcessUtil.currentDate());
                // 入库水质数据
                fishWaterQualityMapper.insertFishWaterQuality(individualFishWaterQuality);
            } else {
                // 针对其它非水质传感器：构造独立的土壤/环境数据对象
                SoilSensorValue individualValue = new SoilSensorValue();
                individualValue.setPastureId(String.valueOf(binding.getPastureId()));
                individualValue.setBatchId(String.valueOf(binding.getBatchId()));
                individualValue.setDeviceId("");
                individualValue.setTime(AlertProcessUtil.currentTime());
                individualValue.setDate(AlertProcessUtil.currentDate());
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
     * 查询菜的环境数据
     *
     * @param id 菜的环境数据主键
     * @return 菜的环境数据
     */
    @Override
    public SoilSensorValue selectSoilSensorValueById(String id)
    {
        return soilSensorValueMapper.selectSoilSensorValueById(id);
    }

    /**
     * 查询菜的环境数据列表
     *
     * @param soilSensorValue 菜的环境数据
     * @return 菜的环境数据
     */
    @Override
    public List<SoilSensorValue> selectSoilSensorValueList(SoilSensorValue soilSensorValue)
    {
        return soilSensorValueMapper.selectSoilSensorValueList(soilSensorValue);
    }

    /**
     * 新增菜的环境数据
     *
     * @param soilSensorValue 菜的环境数据
     * @return 结果
     */
    @Override
    public int insertSoilSensorValue(SoilSensorValue soilSensorValue)
    {
        return soilSensorValueMapper.insertSoilSensorValue(soilSensorValue);
    }

    @Override
    public List<SoilSensorValue> getSoilSensorValuesByPastureId(String pastureId) {
        return Collections.emptyList();
    }

    /**
     * 修改菜的环境数据
     *
     * @param soilSensorValue 菜的环境数据
     * @return 结果
     */
    @Override
    public int updateSoilSensorValue(SoilSensorValue soilSensorValue)
    {
        return soilSensorValueMapper.updateSoilSensorValue(soilSensorValue);
    }

    /**
     * 批量删除菜的环境数据
     *
     * @param ids 需要删除的菜的环境数据主键
     * @return 结果
     */
    @Override
    public int deleteSoilSensorValueByIds(String[] ids)
    {
        return soilSensorValueMapper.deleteSoilSensorValueByIds(ids);
    }

    /**
     * 删除菜的环境数据信息
     *
     * @param id 菜的环境数据主键
     * @return 结果
     */
    @Override
    public int deleteSoilSensorValueById(String id)
    {
        return soilSensorValueMapper.deleteSoilSensorValueById(id);
    }

    @Override
    public List<SoilSensorValue> selectSoilSensorValuesByBatchIdAndDateRange(Long batchId, String startDate, String endDate) {
        return soilSensorValueMapper.selectSoilSensorValuesByBatchIdAndDateRange(batchId, startDate, endDate);
    }
}