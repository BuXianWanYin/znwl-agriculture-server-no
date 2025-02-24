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
import com.frog.common.annotation.Excel;
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
        queryWrapper.isNotNull("sensorType");
        queryWrapper.isNotNull("sensor_command");
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
    @Scheduled(fixedRate = 5000)
    public void fetchAllSensorData() {
        // 每次采集前初始化两个数据对象
        SoilSensorValue sensorValue = new SoilSensorValue();
        FishWaterQuality fishWaterQuality = new FishWaterQuality();

        // 定义采集过程中是否出现异常标识，若异常则本次数据不做入库处理
        boolean valid = true;
        // 清空全局数据记录
        globalSensorData.clear();

        // 遍历所有传感器（sensorCommands 存储的是 sensorType 和 传感器发送的指令）
        for (Map.Entry<String, String> entry : sensorCommands.entrySet()) {
            String sensorId = entry.getKey();
            String hexCommand = entry.getValue();

            if (hexCommand == null || hexCommand.trim().isEmpty()) {
                log.warn("传感器" + sensorId + "指令为空，跳过采集");
                continue;
            }
            try {
                byte[] command = hexStringToByteArray(hexCommand);
                // 通过串口发送指令
                serialPortUtil.writeBytes(command);
                byte[] response = serialPortUtil.readBytes();
                Map<String, Object> parsedData = new HashMap<>();

                // 根据不同传感器id解析数据
                if ("1".equals(sensorId)) { // 解析风向传感器数据
                    parsedData = parseWindDirectionData(response);
                    globalSensorData.put("wind_direction", parsedData);
                    sensorValue.setDirection(parsedData.get("direction").toString());

                } else if ("2".equals(sensorId)) { // 解析百叶箱数据
                    parsedData = parseBaiyeBoxData(response);
                    globalSensorData.put("baiye_box", parsedData);
                    sensorValue.setTemperature(parsedData.get("temperature").toString());
                    sensorValue.setHumidity(parsedData.get("humidity").toString());
                    sensorValue.setLightLux(parsedData.get("light").toString());

                } else if ("3".equals(sensorId)) { // 解析风速传感器数据
                    parsedData = parseWindSpeedData(response);
                    globalSensorData.put("wind_speed", parsedData);
                    sensorValue.setSpeed(parsedData.get("speed").toString());

                } else if ("4".equals(sensorId)) { // 解析土壤温度水分数据
                    parsedData = parseSoilTemperatureMoistureData(response);
                    globalSensorData.put("soil_temperature_moisture", parsedData);
                    sensorValue.setSoilTemperature(parsedData.get("soil_temperature").toString());

                } else if ("5".equals(sensorId)) { // 解析土壤pH数据
                    parsedData = parseSoilPHData(response);
                    globalSensorData.put("soil_ph", parsedData);
                    sensorValue.setSoilPh(parsedData.get("soil_ph").toString());

                } else if ("6".equals(sensorId)) { // 解析土壤水分电导率数据
                    parsedData = parseSoilMoistureConductivityData(response);
                    globalSensorData.put("soil_moisture_conductivity", parsedData);
                    sensorValue.setSoilConductivity(parsedData.get("conductivity").toString());
                    sensorValue.setSoilMoisture(parsedData.get("moisture").toString());

                } else if ("8".equals(sensorId)) {  // 解析水质传感器数据
                    parsedData = parseWaterQualityData(response);
                    globalSensorData.put("water_quality", parsedData);
                    fishWaterQuality.setWaterTemperature(parsedData.get("temperature").toString());
                    fishWaterQuality.setWaterPhValue(parsedData.get("ph_value").toString());
                    fishWaterQuality.setDeviceId(3L);
                    fishWaterQuality.setWaterOxygenContent("30");
                    fishWaterQuality.setWaterNitriteContent("0.01g");
                    fishWaterQuality.setTime(currentTime());
                    fishWaterQuality.setDate(currentDate());
                }
            } catch (Exception e) {
                valid = false;
                log.error("传感器" + sensorId + "数据采集异常：" + e.getMessage());
            }
        }

        // 如果采集过程中出现异常，不做数据库入库处理
        if (!valid) {
            log.error("数据采集过程中发生异常，本次数据不做数据库插入。");
            return;
        }

        /*
         * 遍历查询每个传感器设备的绑定信息：
         * 根据设备ID（此处假设sensorId可作为deviceMapper查询的主键或者查询条件）获取对应的大棚（pasture_id）和分区（batch_id）
         * 鱼水质传感器（id:"8"），设备信息中包含的字段是 fishPastureId 和 fishPastureBatchId，
         * 与其它传感器使用的 pasture_id 和 batch_id 不同。
         */
        Map<String, Device> sensorBindings = new HashMap<>();
        for (String sensorType : sensorCommands.keySet()) {
            Device device = deviceMapper.selectById(sensorType);
            if (device != null) {
                sensorBindings.put(sensorType, device);
            }
        }

        /*
         * 根据查询结果判断非水质设备是否全部绑定同一大棚和分区（只涉及土壤、风向、百叶箱等传感器）
         * 水质传感器单独处理绑定信息
         */
        boolean allSame = true;
        Long commonPastureId = null;
        Long commonBatchId = null;
        for (Map.Entry<String, Device> entry : sensorBindings.entrySet()) {
            String sensorId = entry.getKey();
            if ("8".equals(sensorId)) {  // 水质设备跳过统一处理
                continue;
            }
            Device device = entry.getValue();
            try {
                Long devicePastureId = Long.valueOf(device.getPastureId());
                Long deviceBatchId = Long.valueOf(device.getBatchId());
                if (commonPastureId == null) {
                    commonPastureId = devicePastureId;
                    commonBatchId = deviceBatchId;
                } else {
                    if (!commonPastureId.equals(devicePastureId) || !commonBatchId.equals(deviceBatchId)) {
                        allSame = false;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                log.error("设备ID转换异常: " + e.getMessage());
                allSame = false;
                break;
            }
        }

        if (allSame) {
            // 所有非水质传感器均绑定同一大棚和分区，合并采集数据后入库
            sensorValue.setPastureId(String.valueOf(commonPastureId));
            sensorValue.setBatchId(String.valueOf(commonBatchId));
            sensorValue.setDeviceId("null"); // 标识为汇总数据
            sensorValue.setTime(currentTime());
            sensorValue.setDate(currentDate());
            this.insertSoilSensorValue(sensorValue);

            // 对于水质传感器数据，单独从其设备绑定信息中取鱼棚相关id（传感器ID "8"）
            if (globalSensorData.containsKey("water_quality") && sensorBindings.containsKey("8")) {
                Device waterDeviceBinding = deviceMapper.selectWaterById("8");

                System.out.println("搜索sensorBindings这个Map，看是否有一个键等于 \"8\""  + sensorBindings.containsKey("8"));
                System.out.println("sensorBindings 获取的 Device对象" + waterDeviceBinding);
                System.out.println("鱼棚id" + waterDeviceBinding.getFishPastureId());
                System.out.println("鱼分区id" + waterDeviceBinding.getFishPastureBatchId());


                fishWaterQuality.setFishPastureId(Long.valueOf(waterDeviceBinding.getFishPastureId()));
                fishWaterQuality.setFishPastureBatchId(Long.valueOf(waterDeviceBinding.getFishPastureBatchId()));
                fishWaterQuality.setTime(currentTime());
                fishWaterQuality.setDate(currentDate());
                fishWaterQualityMapper.insertFishWaterQuality(fishWaterQuality);
            }
        } else {
            /*
             * 如果部分设备不在同一大棚和分区，则单独按设备处理：
             * 对于非水质传感器构造 SoilSensorValue 数据对象，
             * 对于水质传感器独立构造 FishWaterQuality 数据对象，绑定对应的鱼棚信息。
             */
            for (Map.Entry<String, Device> entry : sensorBindings.entrySet()) {
                String sensorId = entry.getKey();
                Device binding = entry.getValue();
                if ("8".equals(sensorId) && globalSensorData.containsKey("water_quality")) {
                    // 针对水质传感器
                    FishWaterQuality individualFishWaterQuality = new FishWaterQuality();
                    individualFishWaterQuality.setFishPastureId(Long.valueOf(binding.getFishPastureId()));
                    individualFishWaterQuality.setFishPastureBatchId(Long.valueOf(binding.getFishPastureBatchId()));
                    // 将采集到的水质数据赋给 individualFishWaterQuality
                    individualFishWaterQuality.setWaterTemperature(fishWaterQuality.getWaterTemperature());
                    individualFishWaterQuality.setWaterPhValue(fishWaterQuality.getWaterPhValue());
                    individualFishWaterQuality.setWaterOxygenContent(fishWaterQuality.getWaterOxygenContent());
                    individualFishWaterQuality.setWaterNitriteContent(fishWaterQuality.getWaterNitriteContent());
                    individualFishWaterQuality.setTime(currentTime());
                    individualFishWaterQuality.setDate(currentDate());
                    fishWaterQualityMapper.insertFishWaterQuality(individualFishWaterQuality);
                } else {
                    // 针对其它传感器，构造 SoilSensorValue 数据对象
                    SoilSensorValue individualValue = new SoilSensorValue();
                    individualValue.setPastureId(String.valueOf(binding.getPastureId()));
                    individualValue.setBatchId(String.valueOf(binding.getBatchId()));
                    individualValue.setDeviceId(sensorId);
                    individualValue.setTime(currentTime());
                    individualValue.setDate(currentDate());
                    if ("1".equals(sensorId)) {
                        individualValue.setDirection(sensorValue.getDirection());
                    } else if ("2".equals(sensorId)) {
                        individualValue.setTemperature(sensorValue.getTemperature());
                        individualValue.setHumidity(sensorValue.getHumidity());
                        individualValue.setLightLux(sensorValue.getLightLux());
                    } else if ("3".equals(sensorId)) {
                        individualValue.setSpeed(sensorValue.getSpeed());
                    } else if ("4".equals(sensorId)) {
                        individualValue.setSoilTemperature(sensorValue.getSoilTemperature());
                    } else if ("5".equals(sensorId)) {
                        individualValue.setSoilPh(sensorValue.getSoilPh());
                    } else if ("6".equals(sensorId)) {
                        individualValue.setSoilConductivity(sensorValue.getSoilConductivity());
                        individualValue.setSoilMoisture(sensorValue.getSoilMoisture());
                    }
                    this.insertSoilSensorValue(individualValue);
                }
            }
        }

        // 将 globalSensorData 转换为 JSON 字符串输出日志
        Gson gson = new Gson();
        String jsonOutput = gson.toJson(globalSensorData);
        System.out.println("传感器最新数据：" + jsonOutput);
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