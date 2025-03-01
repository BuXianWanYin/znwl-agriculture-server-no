package com.frog.agriculture.utils;

import com.frog.IaAgriculture.domain.Device;
import com.frog.agriculture.domain.FishWaterQuality;
import com.frog.agriculture.domain.SensorAlert;
import com.frog.agriculture.domain.SoilSensorValue;
import com.frog.agriculture.mapper.SensorAlertMapper;
import com.frog.common.utils.AudioPlayer;
import com.frog.common.utils.SerialPortUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预警处理工具类
 */
public class AlertProcessUtil {
    private static final Log log = LogFactory.getLog(AlertProcessUtil.class);
    /**
     * 获取预警阈值缓冲区间
     */
    public static double[] getThresholdBuffer(String paramKey) {
        double minBuffer, maxBuffer;
        switch (paramKey) {
            case "temperature":
                minBuffer = 2.0;
                maxBuffer = 3.0;
                break;
            case "humidity":
                minBuffer = 5.0;
                maxBuffer = 5.0;
                break;
            case "light":
                minBuffer = 40.0;
                maxBuffer = 1000.0;
                break;
            case "speed":
                minBuffer = 0.0;
                maxBuffer = 1.5;
                break;
            case "soil_temperature":
                minBuffer = 2.0;
                maxBuffer = 2.0;
                break;
            case "soil_ph":
                minBuffer = 0.3;
                maxBuffer = 0.3;
                break;
            case "conductivity":
                minBuffer = 10.0;
                maxBuffer = 200.0;
                break;
            case "moisture":
                minBuffer = 5.0;
                maxBuffer = 5.0;
                break;
            case "water_temperature":
                minBuffer = 2.0;
                maxBuffer = 2.0;
                break;
            case "water_ph":
                minBuffer = 0.3;
                maxBuffer = 0.3;
                break;
            case "oxygen":
                minBuffer = 0.5;
                maxBuffer = 0.5;
                break;
            case "ammonia":
                minBuffer = 0.0;
                maxBuffer = 0.005;
                break;
            case "nitrite":
                minBuffer = 0.0;
                maxBuffer = 0.02;
                break;
            default:
                minBuffer = 0.0;
                maxBuffer = 0.0;
        }
        return new double[]{minBuffer, maxBuffer};
    }

    /**
     * 判断是否需要检查双向阈值
     */
    public static boolean shouldCheckBothThresholds(String paramKey) {
        return !paramKey.equals("speed") && !paramKey.equals("ammonia") && !paramKey.equals("nitrite");
    }

    /**
     * 检查预警阈值并返回预警类型
     */
    public static String checkWarningThresholds(double value, double minWarning, double maxWarning, String paramName) {
        if (value < minWarning) {
            return "接近低值预警";
        }
        if (value > maxWarning) {
            return "接近高值预警";
        }
        return null;
    }

    /**
     * 生成预警消息
     */
    public static String generateWarningMessage(double value, String alertType, String paramName,
                                                double minWarning, double maxWarning) {
        if (alertType.contains("低值")) {
            return paramName + "接近下限：当前值" + value + "，警戒值" + minWarning;
        } else {
            return paramName + "接近上限：当前值" + value + "，警戒值" + maxWarning;
        }
    }

    /**
     * 设置预警信息的设备相关信息
     */
    public static void setAlertDeviceInfo(SensorAlert alert, Device device) {
        if (device != null) {
            alert.setDeviceId(device.getDeviceId());
            alert.setDeviceName(device.getDeviceName());
            alert.setSensorType(device.getSensorType());
        }
    }

    /**
     * 设置预警信息的时间相关信息
     */
    public static void setAlertTimeInfo(SensorAlert alert) {
        alert.setAlertTime(currentTimestamp());
        alert.setAlertDate(currentDate());
        alert.setStatus("0"); // 0表示未处理
    }


    
    /**
     * 获取当前系统的完整时间戳格式 yyyy-MM-dd HH:mm:ss
     */
    public static String currentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 获取当前系统时间（时分秒格式）
     */
    public static String currentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    /**
     * 获取当前系统日期（年-月-日格式）
     */
    public static String currentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 检查并处理预警信息
     */
    public static void processAlert(String paramKey, String paramName, double value, double[] thresholds,
                                  double minWarning, double maxWarning, String pastureId, String batchId,
                                  Device device, String alertType, String alertMessage, SensorAlertMapper sensorAlertMapper,
                                    SerialPortUtil serialPortUtil) {

        if (!hasActiveAlert(paramName, alertType, pastureId, batchId, device, sensorAlertMapper)) {
            if (alertType.contains("严重")) {
                generateSeriousAlert(paramKey, paramName, value, thresholds, pastureId, batchId,
                        device, alertType, alertMessage, sensorAlertMapper,serialPortUtil);
            } else {
                generateWarning(paramKey, paramName, value, thresholds, minWarning, maxWarning,
                        pastureId, batchId, device, alertType, alertMessage, sensorAlertMapper);
            }
        }
    }

    /**
     * 检查是否存在未处理的相同类型预警
     */
    public static boolean hasActiveAlert(String paramName, String alertType, String pastureId, 
                                       String batchId, Device device, SensorAlertMapper sensorAlertMapper) {
        try {
            SensorAlert queryAlert = new SensorAlert();
            queryAlert.setParamName(paramName);
            queryAlert.setAlertType(alertType);
            queryAlert.setPastureId(pastureId);
            queryAlert.setBatchId(batchId);
            queryAlert.setStatus("0");

            queryAlert.setPastureType(isPastureTypeWater(paramName) ? "1" : "0");

            if (device != null) {
                queryAlert.setSensorType(device.getSensorType());
            }

            List<SensorAlert> existingAlerts = sensorAlertMapper.selectSensorAlertList(queryAlert);
            return existingAlerts != null && !existingAlerts.isEmpty();
        } catch (Exception e) {
            log.error("查询现有预警失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新活跃预警状态
     */
    public static void updateActiveAlerts(String paramName, String pastureId, String batchId, 
                                        Device device, SensorAlertMapper sensorAlertMapper,
                                        SerialPortUtil serialPortUtil) {
        try {
            SensorAlert queryAlert = new SensorAlert();
            queryAlert.setParamName(paramName);
            queryAlert.setPastureId(pastureId);
            queryAlert.setBatchId(batchId);
            queryAlert.setStatus("0");
            queryAlert.setAlertLevel("1"); // 只查询严重警告级别的预警
            queryAlert.setPastureType(isPastureTypeWater(paramName) ? "1" : "0");

            if (device != null) {
                queryAlert.setSensorType(device.getSensorType());
            }

            List<SensorAlert> activeAlerts = sensorAlertMapper.selectSensorAlertList(queryAlert);

            if (activeAlerts != null && !activeAlerts.isEmpty()) {
                for (SensorAlert alert : activeAlerts) {
                    alert.setStatus("1");
                    alert.setRemark("数据恢复正常，系统自动处理");
                    alert.setUpdateTime(currentTimestamp());
                    sensorAlertMapper.updateSensorAlert(alert);
                    log.info("自动处理严重警告: " + paramName + " 数据恢复正常");
                    
                    // 当严重警告解除时，关闭设备和音频
                    try {
                        //全部关闭命令
                        serialPortUtil.sendAllClose();
                        //停止播放音频
                        AudioPlayer.stopAlarmSound();
                        //延时
                        Thread.sleep(2000);
                        //收回推杆
                        serialPortUtil.sendRelay4();
                        log.info("已发送数据恢复继电器控制命令");
                    } catch (Exception e) {
                        log.error("发送继电器控制命令失败: " + e.getMessage());
                    }
                }
            } else {
                // 如果没有严重警告，只更新普通预警状态
                queryAlert.setAlertLevel("0");
                activeAlerts = sensorAlertMapper.selectSensorAlertList(queryAlert);
                if (activeAlerts != null && !activeAlerts.isEmpty()) {
                    for (SensorAlert alert : activeAlerts) {
                        alert.setStatus("1");
                        alert.setRemark("数据恢复正常，系统自动处理");
                        alert.setUpdateTime(currentTimestamp());
                        sensorAlertMapper.updateSensorAlert(alert);
                        log.info("自动处理预警: " + paramName + " 数据恢复正常");
                    }
                }
            }
        } catch (Exception e) {
            log.error("更新预警状态失败: " + e.getMessage());
        }
    }

    /**
     * 生成预警信息
     */
    private static void generateWarning(String paramKey, String paramName, double value, double[] thresholds,
                                      double minWarning, double maxWarning, String pastureId, String batchId,
                                      Device device, String alertType, String alertMessage, 
                                      SensorAlertMapper sensorAlertMapper) {
        SensorAlert alert = createBaseAlert(paramKey, paramName, value, thresholds, pastureId, 
                                          batchId, device, alertType, alertMessage);
        alert.setAlertLevel("0");
        saveAlert(alert, "预警", sensorAlertMapper);
    }

    /**
     * 生成严重警告信息
     */
    private static void generateSeriousAlert(String paramKey, String paramName, double value, double[] thresholds,
                                           String pastureId, String batchId, Device device, String alertType,
                                           String alertMessage, SensorAlertMapper sensorAlertMapper,SerialPortUtil serialPortUtil) {

        SensorAlert alert = createBaseAlert(paramKey, paramName, value, thresholds, pastureId, 
                                          batchId, device, alertType, alertMessage);
        //严重警告 设置level 为1 打开继电器 播放音频
        alert.setAlertLevel("1");
        serialPortUtil.sendMultipleRelays(); //同时打开1  2 3 个继电器
        AudioPlayer.playAlarmSound();
        saveAlert(alert, "严重警告", sensorAlertMapper);
    }

    /**
     * 创建基础预警对象
     */
    private static SensorAlert createBaseAlert(String paramKey, String paramName, double value, 
                                             double[] thresholds, String pastureId, String batchId,
                                             Device device, String alertType, String alertMessage) {
        SensorAlert alert = new SensorAlert();
        alert.setAlertType(alertType);
        alert.setAlertMessage(alertMessage);
        alert.setParamName(paramName);
        alert.setParamValue(String.valueOf(value));
        alert.setThresholdMin(String.valueOf(thresholds[0]));
        alert.setThresholdMax(String.valueOf(thresholds[1]));
        alert.setPastureId(pastureId);
        alert.setBatchId(batchId);
        alert.setPastureType(isPastureTypeWater(paramKey) ? "1" : "0");
        setAlertDeviceInfo(alert, device);
        setAlertTimeInfo(alert);
        return alert;
    }

    /**
     * 保存预警信息到数据库
     */
    private static void saveAlert(SensorAlert alert, String alertTypeName, SensorAlertMapper sensorAlertMapper) {
        try {
            sensorAlertMapper.insertSensorAlert(alert);
            log.info("生成" + alertTypeName + "信息: " + alert.getAlertMessage());
        } catch (Exception e) {
            log.error("保存" + alertTypeName + "信息失败: " + e.getMessage());
        }
    }

    /**
     * 判断是否为水质相关参数
     */
    private static boolean isPastureTypeWater(String paramKey) {
        return paramKey.startsWith("water_") ||
               paramKey.equals("oxygen") ||
               paramKey.equals("ammonia") ||
               paramKey.equals("nitrite");
    }

    /**
     * 检查传感器数据是否超过阈值，并生成预警信息
     */
    public static void checkThresholdsAndGenerateAlerts(SoilSensorValue sensorValue,
                                                        FishWaterQuality fishWaterQuality,
                                                        Map<String, Device> sensorBindings,
                                                        SensorAlertMapper sensorAlertMapper,
                                                        com.frog.common.utils.SerialPortUtil serialPortUtil) {
        // 检查土壤和环境数据
        checkAndAlertForSoilData(sensorValue, sensorBindings, sensorAlertMapper, serialPortUtil);

        // 检查水质数据
        if (fishWaterQuality != null && fishWaterQuality.getWaterTemperature() != null) {
            checkAndAlertForWaterData(fishWaterQuality, sensorBindings.get("8"), sensorAlertMapper, serialPortUtil);
        }
    }

    /**
     * 检查土壤和环境数据是否超过阈值并生成预警
     */
    private static void checkAndAlertForSoilData(SoilSensorValue sensorValue, 
                                               Map<String, Device> sensorBindings,
                                               SensorAlertMapper sensorAlertMapper,
                                               com.frog.common.utils.SerialPortUtil serialPortUtil) {
        // 检查温度
        if (sensorValue.getTemperature() != null) {
            try {
                double temperature = Double.parseDouble(sensorValue.getTemperature());
                checkThresholdAndAlert("temperature", temperature, "温度",
                        sensorValue.getPastureId(), sensorValue.getBatchId(), 
                        sensorBindings.get("2"), sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("温度数据格式错误: " + sensorValue.getTemperature());
            }
        }

        // 检查湿度
        if (sensorValue.getHumidity() != null) {
            try {
                double humidity = Double.parseDouble(sensorValue.getHumidity());
                checkThresholdAndAlert("humidity", humidity, "湿度",
                        sensorValue.getPastureId(), sensorValue.getBatchId(), 
                        sensorBindings.get("2"), sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("湿度数据格式错误: " + sensorValue.getHumidity());
            }
        }

        // 检查光照
        if (sensorValue.getLightLux() != null) {
            try {
                double light = Double.parseDouble(sensorValue.getLightLux());
                checkThresholdAndAlert("light", light, "光照",
                        sensorValue.getPastureId(), sensorValue.getBatchId(), 
                        sensorBindings.get("2"), sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("光照数据格式错误: " + sensorValue.getLightLux());
            }
        }

        // 检查风速
        if (sensorValue.getSpeed() != null) {
            try {
                double speed = Double.parseDouble(sensorValue.getSpeed());
                checkThresholdAndAlert("speed", speed, "风速",
                        sensorValue.getPastureId(), sensorValue.getBatchId(), 
                        sensorBindings.get("3"), sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("风速数据格式错误: " + sensorValue.getSpeed());
            }
        }

        // 检查土壤温度
        if (sensorValue.getSoilTemperature() != null) {
            try {
                double soilTemp = Double.parseDouble(sensorValue.getSoilTemperature());
                checkThresholdAndAlert("soil_temperature", soilTemp, "土壤温度",
                        sensorValue.getPastureId(), sensorValue.getBatchId(), 
                        sensorBindings.get("4"), sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("土壤温度数据格式错误: " + sensorValue.getSoilTemperature());
            }
        }

        // 检查土壤pH
        if (sensorValue.getSoilPh() != null) {
            try {
                double soilPh = Double.parseDouble(sensorValue.getSoilPh());
                checkThresholdAndAlert("soil_ph", soilPh, "土壤pH",
                        sensorValue.getPastureId(), sensorValue.getBatchId(), 
                        sensorBindings.get("5"), sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("土壤pH数据格式错误: " + sensorValue.getSoilPh());
            }
        }

        // 检查土壤电导率
        if (sensorValue.getSoilConductivity() != null) {
            try {
                double conductivity = Double.parseDouble(sensorValue.getSoilConductivity());
                checkThresholdAndAlert("conductivity", conductivity, "土壤电导率",
                        sensorValue.getPastureId(), sensorValue.getBatchId(), 
                        sensorBindings.get("6"), sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("土壤电导率数据格式错误: " + sensorValue.getSoilConductivity());
            }
        }

        // 检查土壤水分
        if (sensorValue.getSoilMoisture() != null) {
            try {
                double moisture = Double.parseDouble(sensorValue.getSoilMoisture());
                checkThresholdAndAlert("moisture", moisture, "土壤水分",
                        sensorValue.getPastureId(), sensorValue.getBatchId(), 
                        sensorBindings.get("6"), sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("土壤水分数据格式错误: " + sensorValue.getSoilMoisture());
            }
        }
    }

    /**
     * 检查水质数据是否超过阈值并生成预警
     */
    private static void checkAndAlertForWaterData(FishWaterQuality fishWaterQuality, 
                                                Device waterDevice,
                                                SensorAlertMapper sensorAlertMapper,
                                                com.frog.common.utils.SerialPortUtil serialPortUtil) {
        String pastureId = String.valueOf(fishWaterQuality.getFishPastureId());
        String batchId = String.valueOf(fishWaterQuality.getFishPastureBatchId());

        // 检查水温
        if (fishWaterQuality.getWaterTemperature() != null) {
            try {
                double waterTemp = Double.parseDouble(fishWaterQuality.getWaterTemperature());
                checkThresholdAndAlert("water_temperature", waterTemp, "水温",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("水温数据格式错误: " + fishWaterQuality.getWaterTemperature());
            }
        }

        // 检查水pH
        if (fishWaterQuality.getWaterPhValue() != null) {
            try {
                double waterPh = Double.parseDouble(fishWaterQuality.getWaterPhValue());
                checkThresholdAndAlert("water_ph", waterPh, "水pH",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("水pH数据格式错误: " + fishWaterQuality.getWaterPhValue());
            }
        }

        // 检查溶解氧
        if (fishWaterQuality.getWaterOxygenContent() != null) {
            try {
                double oxygen = Double.parseDouble(fishWaterQuality.getWaterOxygenContent());
                checkThresholdAndAlert("oxygen", oxygen, "溶解氧",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("溶解氧数据格式错误: " + fishWaterQuality.getWaterOxygenContent());
            }
        }

        // 检查氨氮
        if (fishWaterQuality.getWaterAmmoniaNitrogenContent() != null) {
            try {
                double ammonia = Double.parseDouble(fishWaterQuality.getWaterAmmoniaNitrogenContent());
                checkThresholdAndAlert("ammonia", ammonia, "氨氮",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("氨氮数据格式错误: " + fishWaterQuality.getWaterAmmoniaNitrogenContent());
            }
        }

        // 检查亚硝酸盐
        if (fishWaterQuality.getWaterNitriteContent() != null) {
            try {
                double nitrite = Double.parseDouble(fishWaterQuality.getWaterNitriteContent());
                checkThresholdAndAlert("nitrite", nitrite, "亚硝酸盐",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil);
            } catch (NumberFormatException e) {
                log.warn("亚硝酸盐数据格式错误: " + fishWaterQuality.getWaterNitriteContent());
            }
        }
    }

    /**
     * 检查单个参数是否接近或超过阈值，并生成预警信息
     */
    private static void checkThresholdAndAlert(String paramKey, double value, String paramName,
                                             String pastureId, String batchId, Device device,
                                             SensorAlertMapper sensorAlertMapper,
                                             SerialPortUtil serialPortUtil) {
        double[] thresholds = ThresholdConfigUtil.getThresholdByKey(paramKey);
        if (thresholds == null) {
            log.warn("未找到参数 " + paramKey + " 的阈值配置");
            return;
        }

        double[] buffers = getThresholdBuffer(paramKey);
        double minBuffer = buffers[0];
        double maxBuffer = buffers[1];

        double minWarning = thresholds[0] + minBuffer;
        double maxWarning = thresholds[1] - maxBuffer;

        String alertType = null;
        String alertMessage = null;

        // 检查是否超出阈值范围
        if (value < thresholds[0]) {
            alertMessage = paramName + "过低：当前值" + value + "，最小阈值" + thresholds[0];
            alertType = value < thresholds[0] * 0.8 ? "严重低值警告" : "低值预警";
        } else if (value > thresholds[1]) {
            alertMessage = paramName + "过高：当前值" + value + "，最大阈值" + thresholds[1];
            alertType = value > thresholds[1] * 1.2 ? "严重高值警告" : "高值预警";
        }

        if (alertType != null) {
            processAlert(paramKey, paramName, value, thresholds, minWarning, maxWarning,
                    pastureId, batchId, device, alertType, alertMessage, sensorAlertMapper,serialPortUtil);
            return;
        }

        // 检查是否需要生成预警
        if (shouldCheckBothThresholds(paramKey)) {
            alertType = checkWarningThresholds(value, minWarning, maxWarning, paramName);
            if (alertType != null) {
                alertMessage = generateWarningMessage(value, alertType, paramName, minWarning, maxWarning);
                processAlert(paramKey, paramName, value, thresholds, minWarning, maxWarning,
                        pastureId, batchId, device, alertType, alertMessage, sensorAlertMapper,serialPortUtil);
                return;
            }
        }

        // 检查并更新未处理的预警
        updateActiveAlerts(paramName, pastureId, batchId, device, sensorAlertMapper, serialPortUtil);
    }
}