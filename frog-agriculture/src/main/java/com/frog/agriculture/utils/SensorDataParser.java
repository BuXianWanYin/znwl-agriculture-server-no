package com.frog.agriculture.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 传感器数据解析工具类
 * 用于解析各类传感器返回的原始数据
 */
public class SensorDataParser {
    private static final Log log = LogFactory.getLog(SensorDataParser.class);

    /**
     * 解析风向传感器数据
     *
     * @param data 原始字节数据
     * @return Map包含:
     *         direction_grade: 风向等级(0-7)
     *         direction_angle: 风向角度
     *         direction: 风向描述(如"北风","东北风"等)
     */
    public static Map<String, Object> parseWindDirectionData(byte[] data) {
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
            log.error("解析风向数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析百叶箱传感器数据
     *
     * @param data 原始字节数据
     * @return Map包含:
     *         humidity: 湿度(%)
     *         temperature: 温度(℃)
     *         noise: 噪音(dB)
     *         pm25: PM2.5浓度(μg/m³)
     *         pm10: PM10浓度(μg/m³)
     *         light: 光照强度(lux)
     */
    public static Map<String, Object> parseBaiyeBoxData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            double humidity = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF)) / 10.0;
            double temperature = ((data[5] & 0xFF) << 8 | (data[6] & 0xFF)) / 10.0;
            double noise = ((data[7] & 0xFF) << 8 | (data[8] & 0xFF)) / 10.0;
            int pm25 = ((data[9] & 0xFF) << 8 | (data[10] & 0xFF));
            int pm10 = ((data[13] & 0xFF) << 8 | (data[14] & 0xFF));
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
     * 解析风速传感器数据
     *
     * @param data 原始字节数据
     * @return Map包含:
     *         speed: 风速(m/s)
     */
    public static Map<String, Object> parseWindSpeedData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            int raw = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);
            double speed = raw / 10.0;
            map.put("speed", speed);
        } catch (Exception e) {
            log.error("解析风速数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析土壤温度水分传感器数据
     *
     * @param data 原始字节数据
     * @return Map包含:
     *         soil_temperature: 土壤温度(℃)
     *         moisture: 土壤水分含量(%)
     */
    public static Map<String, Object> parseSoilTemperatureMoistureData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            double moisture = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF)) / 10.0;
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
     * 解析土壤pH传感器数据
     *
     * @param data 原始字节数据
     * @return Map包含:
     *         soil_ph: 土壤pH值
     */
    public static Map<String, Object> parseSoilPHData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            double soilPh = (((data[3] & 0xFF) << 8) | (data[4] & 0xFF)) / 10.0;
            map.put("soil_ph", soilPh);
        } catch (Exception e) {
            log.error("解析土壤pH数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 解析土壤水分电导率传感器数据
     *
     * @param data 原始字节数据
     * @return Map包含:
     *         moisture: 土壤水分含量(%)
     *         soil_temperature: 土壤温度(℃)
     *         conductivity: 土壤电导率(μS/cm)
     */
    public static Map<String, Object> parseSoilMoistureConductivityData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            double moisture = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF)) / 10.0;
            int tempValue = ((data[5] & 0xFF) << 8 | (data[6] & 0xFF));
            if (tempValue > 32767) {
                tempValue = tempValue - 65536;
            }
            double temperature = tempValue / 10.0;
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
     *
     * @param data 原始字节数据
     * @return Map包含:
     *         temperature: 水温(℃)
     *         ph_value: 水质pH值
     */
    public static Map<String, Object> parseWaterQualityData(byte[] data) {
        Map<String, Object> map = new HashMap<>();
        try {
            int tempValue = ((data[3] & 0xFF) << 8 | (data[4] & 0xFF));
            int tempDecimal = ((data[5] & 0xFF) << 8 | (data[6] & 0xFF));
            double temperature = tempValue / Math.pow(10, tempDecimal);
            temperature = Math.round(temperature * Math.pow(10, tempDecimal)) / Math.pow(10, tempDecimal);
            double phValue = ((data[7] & 0xFF) << 8 | (data[8] & 0xFF)) / 100.0;

            map.put("temperature", temperature);
            map.put("ph_value", phValue);
        } catch (Exception e) {
            log.error("解析水质传感器数据出错: " + e.getMessage());
        }
        return map;
    }

    /**
     * 将十六进制字符串转换为字节数组
     *
     * @param s 十六进制字符串(可包含空格)
     * @return 转换后的字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        s = s.replace(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}