package com.frog.agriculture.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.frog.agriculture.domain.FishWaterQuality;
import com.frog.agriculture.mapper.FishWaterQualityMapper;
import com.frog.common.annotation.Excel;
import com.frog.common.utils.SerialPortUtil;
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

    // 串口工具，用于接收各种传感器数据
    private SerialPortUtil serialPortUtil;

    private static final Log log = LogFactory.getLog(SoilSensorValueServiceImpl.class);

    // 全局存储各传感器最新数据，key 为传感器地址（例如 "1" 表示风向）
    private ConcurrentHashMap<String, Map<String, Object>> globalSensorData = new ConcurrentHashMap<>();

    // 存放各传感器的指令（十六进制字符串）
    private Map<String, String> sensorCommands = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化串口工具
        serialPortUtil = new SerialPortUtil();

        // 初始化各传感器对应的指令，指令中的空格会在转换为字节数组时去除
        sensorCommands.put("1", "01 03 00 00 00 02 C4 0B"); // 风向传感器
        sensorCommands.put("2", "02 03 01 F4 00 08 04 31"); // 百叶箱
        sensorCommands.put("3", "03 03 00 00 00 01 85 E8"); // 风速传感器
        sensorCommands.put("4", "04 03 00 00 00 04 44 5C"); // 土壤温度水分变送器
        sensorCommands.put("5", "05 03 00 00 00 01 85 8E"); // 土壤 pH 传感器
        sensorCommands.put("6", "06 03 00 00 00 04 45 BE"); // 土壤水分电导率传感器
        sensorCommands.put("8", "08 03 00 05 00 03 15 53"); // 水质传感器
    }

    /**
     * 定时任务：每5秒采集一次所有传感器数据，解析数据后保存，并更新全局数据记录
     */
    @Scheduled(fixedRate = 5000)
    public void fetchAllSensorData() {
        System.out.println("获取土壤传感器数据");
        // 每次采集前，清空或初始化一个 SoilSensorValue 对象
        SoilSensorValue sensorValue = new SoilSensorValue();

        for (Map.Entry<String, String> entry : sensorCommands.entrySet()) {
            String sensorId = entry.getKey();
            byte[] command = hexStringToByteArray(entry.getValue());
            // 通过串口发送指令
            serialPortUtil.writeBytes(command);
            // 接收响应数据
            byte[] response = serialPortUtil.readBytes();
            Map<String, Object> parsedData = new HashMap<>();

            if ("1".equals(sensorId)) {  // 解析风向传感器数据
                parsedData = parseWindDirectionData(response);
                globalSensorData.put("wind_direction", parsedData);
                sensorValue.setDirection(parsedData.get("direction").toString());//设置风向信息
            } else if ("2".equals(sensorId)) { // 解析百叶箱数据
                parsedData = parseBaiyeBoxData(response);
                globalSensorData.put("baiye_box", parsedData);
                //环境温度
                sensorValue.setTemperature( parsedData.get("temperature").toString());
                //环境湿度
                sensorValue.setHumidity( parsedData.get("humidity").toString());
                //光照强度
                sensorValue.setLightLux( parsedData.get("light").toString());
            } else if ("3".equals(sensorId)) {// 解析风速传感器数据
                parsedData = parseWindSpeedData(response);
                globalSensorData.put("wind_speed", parsedData);
                // 风速
                sensorValue.setSpeed( parsedData.get("speed").toString());
            } else if ("4".equals(sensorId)) { // 解析土壤温度水分数据
                parsedData = parseSoilTemperatureMoistureData(response);
                globalSensorData.put("soil_temperature_moisture", parsedData);
                //土壤温度
                sensorValue.setSoilTemperature( parsedData.get("soil_temperature").toString());
            } else if ("5".equals(sensorId)) {// 解析土壤 pH 数据
                parsedData = parseSoilPHData(response);
                globalSensorData.put("soil_ph", parsedData);
                sensorValue.setSoilPh(parsedData.get("soil_ph").toString());
            } else if ("6".equals(sensorId)) {   // 解析土壤水分电导率数据
                parsedData = parseSoilMoistureConductivityData(response);
                globalSensorData.put("soil_moisture_conductivity", parsedData);
                //电导率
                sensorValue.setSoilConductivity(parsedData.get("conductivity").toString());
                //土壤湿度
                sensorValue.setSoilMoisture(parsedData.get("moisture").toString());
            } else if ("8".equals(sensorId)) {
                FishWaterQuality fishWaterQuality = new FishWaterQuality();
                // 解析水质传感器数据
                parsedData = parseWaterQualityData(response);
                System.out.println("解析水质传感器数据" + parsedData);
                fishWaterQuality.setWaterTemperature(parsedData.get("temperature").toString());//水温
                fishWaterQuality.setWaterPhValue(parsedData.get("ph_value").toString());//ph值
                fishWaterQuality.setFishPastureId(1L); // 大棚id 测试固定值
                fishWaterQuality.setFishPastureBatchId(2L);//分区id 测试固定值
                fishWaterQuality.setDeviceId(3L);//设备id  测试固定值
                fishWaterQuality.setWaterOxygenContent("30");//含氧量  测试固定值
                fishWaterQuality.setWaterNitriteContent("0.01g");//亚硝酸盐含量 测试固定值
                fishWaterQuality.setTime(currentTime());
                fishWaterQuality.setDate(currentDate());

                System.out.println("水质数据" + fishWaterQuality.toString());
                //添加水质数据
                fishWaterQualityMapper.insertFishWaterQuality(fishWaterQuality);
                globalSensorData.put("water_quality", parsedData);

                log.info("水质传感器最新数据：" + parsedData);
            }
        }
        sensorValue.setPastureId("1"); //设置测试 固定ID
        sensorValue.setBatchId("2");
        sensorValue.setDeviceId("3");
        sensorValue.setTime(currentTime());
        sensorValue.setDate(currentDate());
        // 数据采集后，将封装了所有传感器数据的 sensorValue 对象插入数据库
        this.insertSoilSensorValue(sensorValue);
        log.info("所有传感器最新数据：" + globalSensorData);
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