package com.frog.agriculture.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 传感器阈值配置工具类
 */
public class ThresholdConfigUtil {
    private static final Log log = LogFactory.getLog(ThresholdConfigUtil.class);

    private static Map<String, double[]> thresholdConfig = new HashMap<>();

    /**
     * 初始化传感器阈值配置
     */
    public static void initializeThresholdConfig() {
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
     * 获取阈值配置
     */
    public static Map<String, double[]> getThresholdConfig() {
        return thresholdConfig;
    }

    /**
     * 获取指定参数的阈值配置
     */
    public static double[] getThresholdByKey(String key) {
        return thresholdConfig.get(key);
    }
}