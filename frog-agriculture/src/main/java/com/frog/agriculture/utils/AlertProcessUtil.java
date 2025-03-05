package com.frog.agriculture.utils;

import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.exception.ServerException;
import com.frog.agriculture.domain.FishWaterQuality;
import com.frog.agriculture.domain.SensorAlert;
import com.frog.agriculture.domain.SoilSensorValue;
import com.frog.agriculture.mapper.SensorAlertMapper;
import com.frog.agriculture.websocket.AlertWebSocketServer;
import com.frog.common.utils.AudioPlayer;
import com.frog.common.utils.SerialPortUtil;
import com.frog.common.utils.spring.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import vip.blockchain.agriculture.utils.BaseUtil;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 预警处理工具类
 */
public class AlertProcessUtil { // 定义AlertProcessUtil类
    private static final Log log = LogFactory.getLog(AlertProcessUtil.class);

    private static SerialPortUtil serialPortUtil = new SerialPortUtil();
    private static SensorAlertMapper sensorAlertMapper;


    /**
     * 获取SerialPortUtil实例
     * 使用双重检查锁定模式实现线程安全的单例
     */
    public static SerialPortUtil getSerialPortUtil() {
        if (serialPortUtil == null) {
            synchronized (AlertProcessUtil.class) {
                if (serialPortUtil == null) {
                    serialPortUtil = new SerialPortUtil();
                }
            }
        }
        return serialPortUtil;
    }

    /**
     * 获取SensorAlertMapper实例
     * 使用懒加载方式获取Spring Bean
     */
    private static SensorAlertMapper getSensorAlertMapper() {
        if (sensorAlertMapper == null) {
            sensorAlertMapper = SpringUtils.getBean(SensorAlertMapper.class);
        }
        return sensorAlertMapper;
    }


    /**
     * 获取预警阈值缓冲区间
     *
     * @param paramKey 参数键名
     * @return double数组, [0]为最小缓冲值, [1]为最大缓冲值
     */
    public static double[] getThresholdBuffer(String paramKey) { // 定义获取预警阈值缓冲区间的方法，参数为参数键
        double minBuffer, maxBuffer; // 定义最小和最大缓冲变量
        switch (paramKey) { // 根据参数键进行判断
            case "temperature": // 如果参数为temperature（温度）
                minBuffer = 2.0; // 设置最小缓冲值为2.0
                maxBuffer = 3.0; // 设置最大缓冲值为3.0
                break;
            case "humidity": // 如果参数为humidity（湿度）
                minBuffer = 5.0; // 设置最小缓冲值为5.0
                maxBuffer = 10.0; // 设置最大缓冲值为10.0
                break;
            case "light": // 如果参数为light（光照）
                minBuffer = 40.0; // 设置最小缓冲值为40.0
                maxBuffer = 1000.0; // 设置最大缓冲值为1000.0
                break;
            case "speed": // 如果参数为speed（风速）
                minBuffer = 0.0; // 设置最小缓冲值为0.0
                maxBuffer = 1.5; // 设置最大缓冲值为1.5
                break;
            case "soil_temperature": // 如果参数为soil_temperature（土壤温度）
                minBuffer = 2.0; // 设置最小缓冲值为2.0
                maxBuffer = 2.0; // 设置最大缓冲值为2.0
                break;
            case "soil_ph": // 如果参数为soil_ph（土壤pH）
                minBuffer = 0.3; // 设置最小缓冲值为0.3
                maxBuffer = 0.3; // 设置最大缓冲值为0.3
                break;
            case "conductivity": // 如果参数为conductivity（土壤电导率）
                minBuffer = 10.0; // 设置最小缓冲值为10.0
                maxBuffer = 200.0; // 设置最大缓冲值为200.0
                break;
            case "moisture": // 如果参数为moisture（土壤水分）
                minBuffer = 5.0; // 设置最小缓冲值为5.0
                maxBuffer = 5.0; // 设置最大缓冲值为5.0
                break;
            case "water_temperature": // 如果参数为water_temperature（水温）
                minBuffer = 2.0; // 设置最小缓冲值为2.0
                maxBuffer = 2.0; // 设置最大缓冲值为2.0
                break;
            case "water_ph": // 如果参数为water_ph（水pH）
                minBuffer = 0.3; // 设置最小缓冲值为0.3
                maxBuffer = 0.3; // 设置最大缓冲值为0.3
                break;
            case "oxygen": // 如果参数为oxygen（溶解氧）
                minBuffer = 0.5; // 设置最小缓冲值为0.5
                maxBuffer = 0.5; // 设置最大缓冲值为0.5
                break;
            case "ammonia": // 如果参数为ammonia（氨氮）
                minBuffer = 0.0; // 设置最小缓冲值为0.0
                maxBuffer = 0.005; // 设置最大缓冲值为0.005
                break;
            case "nitrite": // 如果参数为nitrite（亚硝酸盐）
                minBuffer = 0.0; // 设置最小缓冲值为0.0
                maxBuffer = 0.02; // 设置最大缓冲值为0.02
                break;
            default: // 如果所有case都不匹配
                minBuffer = 0.0; // 设置默认最小缓冲值为0.0
                maxBuffer = 0.0; // 设置默认最大缓冲值为0.0
        } // 结束switch分支
        return new double[]{minBuffer, maxBuffer}; // 返回缓冲区间数组
    } // 结束getThresholdBuffer方法

    /**
     * 判断是否需要检查双向阈值
     *
     * @param paramKey 参数键名
     * @return true:需要检查双向阈值 false:只检查单向阈值
     */
    public static boolean shouldCheckBothThresholds(String paramKey) {
        return !paramKey.equals("speed") && !paramKey.equals("ammonia") && !paramKey.equals("nitrite"); // 除风速、氨氮和亚硝酸盐外都检查双向阈值
    }

    /**
     * 检查预警阈值并返回预警类型
     *
     * @param value      当前值
     * @param minWarning 最小预警值
     * @param maxWarning 最大预警值
     * @param paramName  参数名称
     * @return 预警类型(" 接近低值预警 " / " 接近高值预警 " / null)
     */
    public static String checkWarningThresholds(double value, double minWarning, double maxWarning, String paramName) {
        if (value < minWarning) { // 如果值小于最小预警值
            return "接近低值预警"; // 返回接近低值预警信息
        }
        if (value > maxWarning) { // 如果值大于最大预警值
            return "接近高值预警"; // 返回接近高值预警信息
        }
        return null; // 如果在正常区间则返回null
    }

    /**
     * 生成预警消息
     *
     * @param value      当前值
     * @param alertType  预警类型
     * @param paramName  参数名称
     * @param minWarning 最小预警值
     * @param maxWarning 最大预警值
     * @return 格式化的预警消息
     */
    public static String generateWarningMessage(double value, String alertType, String paramName, double minWarning, double maxWarning) {
        if (alertType.contains("低值")) { // 如果预警类型包含"低值"
            return paramName + "接近下限：当前值" + value + "，警戒值" + minWarning; // 返回低值预警的消息
        } else {
            return paramName + "接近上限：当前值" + value + "，警戒值" + maxWarning; // 返回高值预警的消息
        }
    }

    /**
     * 设置预警信息的设备相关信息
     *
     * @param alert  预警对象
     * @param device 设备对象
     */
    public static void setAlertDeviceInfo(SensorAlert alert, Device device) {
        if (device != null) { // 如果设备不为空
            alert.setDeviceId(device.getDeviceId()); // 设置预警设备ID
            alert.setDeviceName(device.getDeviceName()); // 设置预警设备名称
            alert.setSensorType(device.getSensorType()); // 设置预警传感器类型
        }
    }

    /**
     * 设置预警信息的时间相关信息
     *
     * @param alert 预警对象
     */
    public static void setAlertTimeInfo(SensorAlert alert) {
        alert.setAlertTime(currentTimestamp()); // 设置预警时间为当前完整时间戳
        alert.setAlertDate(currentDate()); // 设置预警日期为当前日期
        alert.setStatus("0"); // 设置状态为"0"，表示未处理
    }

    /**
     * 获取当前系统的完整时间戳格式 yyyy-MM-dd HH:mm:ss
     */
    public static String currentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 返回格式化后的当前时间戳字符串
    }

    /**
     * 获取当前系统时间（时分秒格式）
     */
    public static String currentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date()); // 返回格式化后的当前时分秒字符串
    }

    /**
     * 获取当前系统日期（年-月-日格式）
     */
    public static String currentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // 返回格式化后的当前日期字符串
    }

    /**
     * 检查并处理预警信息
     *
     * @param alertType    预警类型
     * @param alertMessage 预警消息
     */
    private static void processAlert(AlertParams params, String alertType, String alertMessage) {

        // 如果已存在活跃的相同类型预警，则不重复生成
        if (hasActiveAlert(params.paramName, alertType, params.pastureId, params.batchId, params.device)) {
            return;
        }

        // 处理报警（严重警告）
        if (alertType.contains("报警")) {
            generateSeriousAlert(params, alertType, alertMessage);
            return;
        }

        // 处理预警
        if (alertType.contains("预警")) {
            generateWarning(params, alertType, alertMessage);
        }
    }

    /**
     * 检查是否存在未处理的相同类型预警
     *
     * @param paramName 参数名称
     * @param alertType 预警类型
     * @param pastureId 大棚ID
     * @param batchId   批次ID
     * @param device    设备对象
     * @return true:存在未处理的相同预警 false:不存在
     */
    public static boolean hasActiveAlert(String paramName, String alertType, String pastureId, String batchId, Device device) {
        try {
            SensorAlert queryAlert = new SensorAlert(); // 创建查询预警对象
            queryAlert.setParamName(paramName); // 设置参数名称
            queryAlert.setAlertType(alertType); // 设置预警类型
            queryAlert.setPastureId(pastureId); // 设置大棚ID
            queryAlert.setBatchId(batchId); // 设置批次ID
            queryAlert.setStatus("0"); // 设置状态为"0" 表示未处理

            queryAlert.setPastureType(isPastureTypeWater(paramName) ? "1" : "0"); // 根据参数判断大棚类型（水产/土壤）

            if (device != null) { // 如果设备不为空
                queryAlert.setSensorType(device.getSensorType()); // 设置设备的传感器类型
            }

            List<SensorAlert> existingAlerts = getSensorAlertMapper().selectSensorAlertList(queryAlert); // 查询当前符合条件的预警列表
            return existingAlerts != null && !existingAlerts.isEmpty(); // 如果存在预警则返回true，否则返回false
        } catch (Exception e) {
            log.error("查询现有预警失败: " + e.getMessage()); // 记录错误日志
            return false;
        }
    }

    /**
     * 更新活跃预警状态
     *
     * @param paramName 参数名称
     * @param pastureId 大棚ID
     * @param batchId   批次ID
     * @param device    设备对象
     */
    public static void updateActiveAlerts(String paramName, String pastureId, String batchId, Device device) {
        try {
            // 处理严重报警
            handleSeriousAlerts(createQueryAlert(paramName, pastureId, batchId, device, "1"));

            // 处理普通预警
            handleNormalAlerts(createQueryAlert(paramName, pastureId, batchId, device, "0"));
        } catch (Exception e) {
            log.error("更新预警状态失败: " + e.getMessage());
        }
    }

    /**
     * 创建预警查询对象
     * 根据传入的参数创建用于查询预警信息的查询对象
     *
     * @param paramName  参数名称
     * @param pastureId  大棚ID
     * @param batchId    批次ID
     * @param device     设备对象
     * @param alertLevel 预警级别（"0"：普通预警，"1"：严重报警）
     * @return 预警查询对象
     */
    private static SensorAlert createQueryAlert(String paramName, String pastureId, String batchId, Device device, String alertLevel) {
        SensorAlert queryAlert = new SensorAlert();
        queryAlert.setParamName(paramName);
        queryAlert.setPastureId(pastureId);
        queryAlert.setBatchId(batchId);
        queryAlert.setStatus("0");
        queryAlert.setAlertLevel(alertLevel);
        queryAlert.setPastureType(isPastureTypeWater(paramName) ? "1" : "0");

        if (device != null) {
            queryAlert.setSensorType(device.getSensorType());
        }

        return queryAlert;
    }

    /**
     * 处理严重报警
     * 查询并处理系统中的严重报警，包括更新报警状态和重置硬件设备
     *
     * @param queryAlert 查询对象
     */
    private static void handleSeriousAlerts(SensorAlert queryAlert) {
        // 查询当前活跃的严重报警列表
        List<SensorAlert> activeAlerts = getSensorAlertMapper().selectSensorAlertList(queryAlert);

        // 如果没有报警记录,或者不是严重报警(级别不为1),则直接返回
        if (activeAlerts == null || activeAlerts.isEmpty() || !activeAlerts.get(0).getAlertLevel().equals("1")) {
            return;
        }

        // 遍历所有活跃的报警记录
        for (SensorAlert alert : activeAlerts) {
            // 更新报警状态为已处理
            updateAlertStatus(alert, "自动处理报警");

            // 关闭硬件报警设备
            serialPortUtil.closeRedLight();  // 关闭报警红灯
            AudioPlayer.stopAlarmSound();    // 停止报警音频

            // 记录日志
            log.info("已发送数据恢复继电器控制命令");
        }
    }

    /**
     * 处理普通预警
     * 查询并处理系统中的普通预警，更新预警状态
     *
     * @param queryAlert 查询对象
     */
    private static void handleNormalAlerts(SensorAlert queryAlert) {
        List<SensorAlert> activeAlerts = getSensorAlertMapper().selectSensorAlertList(queryAlert);
        if (activeAlerts == null || activeAlerts.isEmpty()) {
            return;
        }

        for (SensorAlert alert : activeAlerts) {
            updateAlertStatus(alert, "自动处理预警");
        }
    }

    /**
     * 更新预警状态
     * 将预警状态更新为已处理，并记录处理日志
     *
     * @param alert     需要更新的预警对象
     * @param logPrefix 日志前缀（用于区分不同类型的预警处理）
     */
    private static void updateAlertStatus(SensorAlert alert, String logPrefix) {
        alert.setStatus("1");
        alert.setRemark("数据恢复正常，系统自动处理");
        alert.setUpdateTime(currentTimestamp());
        getSensorAlertMapper().updateSensorAlert(alert);
        log.info(logPrefix + ": " + alert.getParamName() + " 数据恢复正常");
    }

    /**
     * 生成预警信息
     */
    private static void generateWarning(AlertParams params, String alertType, String alertMessage) {
        SensorAlert alert = createBaseAlert(params, alertType, alertMessage);
        alert.setAlertLevel("0");
        saveAlert(alert, "预警");
    }


    /**
     * 创建基础预警对象
     */
    private static SensorAlert createBaseAlert(AlertParams params, String alertType, String alertMessage) {
        SensorAlert alert = new SensorAlert();
        alert.setAlertType(alertType);
        alert.setAlertMessage(alertMessage);
        alert.setParamName(params.paramName);
        alert.setParamValue(String.valueOf(params.value));
        alert.setThresholdMin(String.valueOf(params.thresholds[0]));
        alert.setThresholdMax(String.valueOf(params.thresholds[1]));
        alert.setPastureId(params.pastureId);
        alert.setBatchId(params.batchId);
        alert.setPastureType(isPastureTypeWater(params.paramKey) ? "1" : "0");
        setAlertDeviceInfo(alert, params.device);
        setAlertTimeInfo(alert);
        return alert;
    }

    /**
     * 保存预警信息到数据库
     */
    private static void saveAlert(SensorAlert alert, String alertTypeName) {
        try {
            getSensorAlertMapper().insertSensorAlert(alert); // 插入预警记录到数据库
            log.info("生成" + alertTypeName + "信息: " + alert.getAlertMessage()); // 记录生成预警信息的日志
            // 通过WebSocket推送预警信息到前端  全站工程师做
            AlertWebSocketServer.sendInfo(alert); // 通过WebSocket发送预警信息到前端

        } catch (Exception e) {
            log.error("保存" + alertTypeName + "信息失败: " + e.getMessage());
        }
    }


    /**
     * 判断是否为水质相关参数
     */
    private static boolean isPastureTypeWater(String paramKey) {
        return paramKey.startsWith("water_") || // 如果参数以"water_"开头
                paramKey.equals("oxygen") || // 或者等于"oxygen"
                paramKey.equals("ammonia") || // 或者等于"ammonia"
                paramKey.equals("nitrite"); // 或者等于"nitrite"
    }

    /**
     * 检查传感器数据是否超过阈值，并生成预警信息
     */
    public static void checkThresholdsAndGenerateAlerts(SoilSensorValue sensorValue, FishWaterQuality fishWaterQuality, Map<String, Device> sensorBindings) {
        // 检查土壤和环境数据
        checkAndAlertForSoilData(sensorValue, sensorBindings); // 检查土壤数据并生成预警

        // 检查水质数据
        if (fishWaterQuality != null && fishWaterQuality.getWaterTemperature() != null) { // 如果水质数据不为空且水温数据不为空
            checkAndAlertForWaterData(fishWaterQuality, sensorBindings.get("8")); // 检查水质数据并生成预警，传入相应设备
        }


    }

    /**
     * 检查土壤和环境数据是否超过阈值并生成预警
     */
    private static void checkAndAlertForSoilData(SoilSensorValue sensorValue, Map<String, Device> sensorBindings) {
        // 检查温度数据
        if (sensorValue.getTemperature() != null) { // 如果温度数据不为空
            try { // 使用try捕获转换异常
                // 将温度字符串转换为double类型
                double temperature = Double.parseDouble(sensorValue.getTemperature()); // 转换温度字符串为double
                // 检查温度是否超过阈值并生成预警
                checkThresholdAndAlert("temperature", temperature, "温度", sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("2")); // 调用检查方法处理温度数据
            } catch (NumberFormatException e) { // 捕获数字格式转换异常
                // 数据格式转换失败时记录警告日志
                log.warn("温度数据格式错误: " + sensorValue.getTemperature()); // 记录温度数据转换错误日志
            }
        }

        // 检查湿度数据
        if (sensorValue.getHumidity() != null) { // 如果湿度数据不为空
            try {
                // 将湿度字符串转换为double类型
                double humidity = Double.parseDouble(sensorValue.getHumidity()); // 将湿度字符串转换为double
                // 检查湿度是否超过阈值并生成预警
                checkThresholdAndAlert("humidity", humidity, "湿度", sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("2")); // 调用检查方法处理湿度数据
            } catch (NumberFormatException e) { // 捕获转换异常
                // 数据格式转换失败时记录警告日志
                log.warn("湿度数据格式错误: " + sensorValue.getHumidity()); // 记录湿度数据格式错误日志
            }
        }

        // 检查光照数据
        if (sensorValue.getLightLux() != null) { // 如果光照数据不为空
            try {
                // 将光照字符串转换为double类型
                double light = Double.parseDouble(sensorValue.getLightLux()); // 转换光照数据为double
                // 检查光照是否超过阈值并生成预警
                checkThresholdAndAlert("light", light, "光照", sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("2")); // 调用检查方法处理光照数据
            } catch (NumberFormatException e) { // 捕获转换异常
                // 数据格式转换失败时记录警告日志
                log.warn("光照数据格式错误: " + sensorValue.getLightLux()); // 记录光照数据转换错误日志
            }
        }

        // 检查风速数据
        if (sensorValue.getSpeed() != null) { // 如果风速数据不为空
            try { // 使用try捕获转换异常
                // 将风速字符串转换为double类型
                double speed = Double.parseDouble(sensorValue.getSpeed()); // 转换风速字符串为double
                // 检查风速是否超过阈值并生成预警
                checkThresholdAndAlert("speed", speed, "风速", sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("3")); // 调用检查方法处理风速数据
            } catch (NumberFormatException e) { // 捕获转换异常
                // 数据格式转换失败时记录警告日志
                log.warn("风速数据格式错误: " + sensorValue.getSpeed()); // 记录风速数据格式错误日志
            }
        }

        // 检查土壤温度数据
        if (sensorValue.getSoilTemperature() != null) { // 如果土壤温度数据不为空
            try { // 使用try捕获转换异常
                // 将土壤温度字符串转换为double类型
                double soilTemp = Double.parseDouble(sensorValue.getSoilTemperature()); // 转换土壤温度为double
                // 检查土壤温度是否超过阈值并生成预警
                checkThresholdAndAlert("soil_temperature", soilTemp, "土壤温度", sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("4")); // 调用检查方法处理土壤温度数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("土壤温度数据格式错误: " + sensorValue.getSoilTemperature()); // 记录土壤温度数据格式错误日志
            }
        }

        // 检查土壤pH数据
        if (sensorValue.getSoilPh() != null) { // 如果土壤pH数据不为空
            try {
                // 将土壤pH字符串转换为double类型
                double soilPh = Double.parseDouble(sensorValue.getSoilPh()); // 转换土壤pH为double
                // 检查土壤pH是否超过阈值并生成预警
                checkThresholdAndAlert("soil_ph", soilPh, "土壤pH", sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("5")); // 调用检查方法处理土壤pH数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("土壤pH数据格式错误: " + sensorValue.getSoilPh()); // 记录土壤pH数据格式错误日志
            }
        }

        // 检查土壤电导率数据
        if (sensorValue.getSoilConductivity() != null) { // 如果土壤电导率数据不为空
            try {
                // 将土壤电导率字符串转换为double类型
                double conductivity = Double.parseDouble(sensorValue.getSoilConductivity()); // 转换土壤电导率为double
                // 检查土壤电导率是否超过阈值并生成预警
                checkThresholdAndAlert("conductivity", conductivity, "土壤电导率", sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("6")); // 调用检查方法处理土壤电导率数据
            } catch (NumberFormatException e) { // 捕获转换异常
                // 数据格式转换失败时记录警告日志
                log.warn("土壤电导率数据格式错误: " + sensorValue.getSoilConductivity()); // 记录土壤电导率数据格式错误日志
            }
        }

        // 检查土壤水分数据
        if (sensorValue.getSoilMoisture() != null) { // 如果土壤水分数据不为空
            try {
                // 将土壤水分字符串转换为double类型
                double moisture = Double.parseDouble(sensorValue.getSoilMoisture()); // 转换土壤水分为double
                // 检查土壤水分是否超过阈值并生成预警
                checkThresholdAndAlert("moisture", moisture, "土壤水分", sensorValue.getPastureId(), sensorValue.getBatchId(), sensorBindings.get("6")); // 调用检查方法处理土壤水分数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("土壤水分数据格式错误: " + sensorValue.getSoilMoisture()); // 记录土壤水分数据格式错误日志
            }
        }
    }

    /**
     * 检查水质数据是否超过阈值并生成预警
     */
    private static void checkAndAlertForWaterData(FishWaterQuality fishWaterQuality, Device waterDevice) {
        // 从水质数据中获取大棚ID和批次ID
        String pastureId = String.valueOf(fishWaterQuality.getFishPastureId()); // 获取大棚ID并转换为字符串
        String batchId = String.valueOf(fishWaterQuality.getFishPastureBatchId()); // 获取批次ID并转换为字符串

        // 检查水温数据
        if (fishWaterQuality.getWaterTemperature() != null) { // 如果水温数据不为空
            try { // 使用try捕获转换异常
                // 将水温字符串转换为double类型
                double waterTemp = Double.parseDouble(fishWaterQuality.getWaterTemperature()); // 转换水温为double
                // 检查水温是否超过阈值并生成预警
                checkThresholdAndAlert("water_temperature", waterTemp, "水温", pastureId, batchId, waterDevice); // 调用检查方法处理水温数据
            } catch (NumberFormatException e) { // 捕获转换异常
                // 数据格式转换失败时记录警告日志
                log.warn("水温数据格式错误: " + fishWaterQuality.getWaterTemperature()); // 记录水温数据格式转换错误日志
            }
        }

        // 检查水pH值数据
        if (fishWaterQuality.getWaterPhValue() != null) { // 如果水pH数据不为空
            try {
                // 将pH值字符串转换为double类型
                double waterPh = Double.parseDouble(fishWaterQuality.getWaterPhValue()); // 转换水pH为double
                // 检查pH值是否超过阈值并生成预警
                checkThresholdAndAlert("water_ph", waterPh, "水pH", pastureId, batchId, waterDevice); // 调用检查方法处理水pH数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("水pH数据格式错误: " + fishWaterQuality.getWaterPhValue()); // 记录水pH数据格式转换错误日志
            }
        }

        // 检查溶解氧数据
        if (fishWaterQuality.getWaterOxygenContent() != null) { // 如果溶解氧数据不为空
            try {
                // 将溶解氧字符串转换为double类型
                double oxygen = Double.parseDouble(fishWaterQuality.getWaterOxygenContent()); // 转换溶解氧为double
                // 检查溶解氧是否超过阈值并生成预警
                checkThresholdAndAlert("oxygen", oxygen, "溶解氧", pastureId, batchId, waterDevice); // 调用检查方法处理溶解氧数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("溶解氧数据格式错误: " + fishWaterQuality.getWaterOxygenContent()); // 记录溶解氧数据格式转换错误日志
            }
        }

        // 检查氨氮数据
        if (fishWaterQuality.getWaterAmmoniaNitrogenContent() != null) { // 如果氨氮数据不为空
            try {
                // 将氨氮字符串转换为double类型
                double ammonia = Double.parseDouble(fishWaterQuality.getWaterAmmoniaNitrogenContent()); // 转换氨氮为double
                // 检查氨氮是否超过阈值并生成预警
                checkThresholdAndAlert("ammonia", ammonia, "氨氮", pastureId, batchId, waterDevice); // 调用检查方法处理氨氮数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("氨氮数据格式错误: " + fishWaterQuality.getWaterAmmoniaNitrogenContent()); // 记录氨氮数据格式转换错误日志
            }
        }

        // 检查亚硝酸盐数据
        if (fishWaterQuality.getWaterNitriteContent() != null) { // 如果亚硝酸盐数据不为空
            try {
                // 将亚硝酸盐字符串转换为double类型
                double nitrite = Double.parseDouble(fishWaterQuality.getWaterNitriteContent()); // 转换亚硝酸盐为double
                // 检查亚硝酸盐是否超过阈值并生成预警
                checkThresholdAndAlert("nitrite", nitrite, "亚硝酸盐", pastureId, batchId, waterDevice); // 调用检查方法处理亚硝酸盐数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("亚硝酸盐数据格式错误: " + fishWaterQuality.getWaterNitriteContent()); // 记录亚硝酸盐数据格式转换错误日志
            }
        }
    }


    /**
     * 检查并处理严重报警情况
     *
     * @param params 告警参数对象
     * @return true 如果生成了报警，false 如果没有报警
     */
    private static boolean checkAndProcessSeriousAlert(AlertParams params) {
        String alertType = null;
        String alertMessage = null;

        if (params.value < params.thresholds[0]) {
            alertMessage = params.paramName + "过低：当前值" + params.value + "，最小阈值" + params.thresholds[0];
            alertType = "严重低值报警";
        } else if (params.value > params.thresholds[1]) {
            alertMessage = params.paramName + "过高：当前值" + params.value + "，最大阈值" + params.thresholds[1];
            alertType = "严重高值报警";
        }

        if (alertType != null) {
            processAlert(params, alertType, alertMessage);
            return true;
        }

        return false;
    }

    /**
     * 检查并处理预警情况
     *
     * @return true 如果生成了预警，false 如果没有预警
     */
    private static boolean checkAndProcessWarning(AlertParams params) {
        String alertType = null;
        String alertMessage = null;

        if (params.value >= params.thresholds[0] && params.value <= params.minWarning) {
            alertMessage = params.paramName + "接近下限：当前值" + params.value + "，警戒值" + params.minWarning;
            alertType = "低值预警";
        } else if (params.value <= params.thresholds[1] && params.value >= params.maxWarning) {
            alertMessage = params.paramName + "接近上限：当前值" + params.value + "，警戒值" + params.maxWarning;
            alertType = "高值预警";
        }

        if (alertType != null) {
            processAlert(params, alertType, alertMessage);
            return true;
        }

        if (shouldCheckBothThresholds(params.paramKey)) {
            alertType = checkWarningThresholds(params.value, params.minWarning, params.maxWarning, params.paramName);
            if (alertType != null) {
                alertMessage = generateWarningMessage(params.value, alertType, params.paramName, params.minWarning, params.maxWarning);
                processAlert(params, alertType, alertMessage);
                return true;
            }
        }

        return false;
    }

    /**
     * 检查单个参数是否接近或超过阈值，并生成预警信息
     *
     * @param paramKey  参数键名(如:"temperature","humidity"等)
     * @param value     当前参数的实际值
     * @param paramName 参数的显示名称(如:"温度","湿度"等)
     * @param pastureId 大棚/养殖棚ID
     * @param batchId   批次ID
     * @param device    设备对象,包含设备ID、名称等信息
     */
    private static void checkThresholdAndAlert(String paramKey, double value, String paramName, String pastureId, String batchId, Device device) {
        // 获取阈值配置
        double[] thresholds = ThresholdConfigUtil.getThresholdByKey(paramKey);
        if (thresholds == null) {
            log.warn("未找到参数 " + paramKey + " 的阈值配置");
            return;
        }

        // 获取阈值缓冲区间
        double[] buffers = getThresholdBuffer(paramKey);
        double minBuffer = buffers[0];
        double maxBuffer = buffers[1];

        // 计算预警阈值
        double minWarning = thresholds[0] + minBuffer;
        double maxWarning = thresholds[1] - maxBuffer;

        // 创建参数对象
        AlertParams params = new AlertParams(paramKey, paramName, value, thresholds, minWarning, maxWarning, pastureId, batchId, device);

        // 首先检查是否需要报警（优先级更高）
        if (checkAndProcessSeriousAlert(params)) {
            return;
        }

        // 如果不需要报警，检查是否需要预警
        if (checkAndProcessWarning(params)) {
            return;
        }

        // 如果数据恢复正常，更新未处理的预警状态
        updateActiveAlerts(paramName, pastureId, batchId, device);
    }


    /**
     * 生成报警信息并执行相应的警报操作
     *
     * @param alertType    警告类型（如"严重低值报警"、"严重高值报警"等）
     * @param alertMessage 警告消息内容
     */
    private static void generateSeriousAlert(AlertParams params, String alertType, String alertMessage) {
        // 创建基础警告对象
        SensorAlert alert = createBaseAlert(params, alertType, alertMessage);
        String snowflakeId = BaseUtil.getSnowflakeId();
        alert.setId(Long.valueOf(snowflakeId));
        alert.setAlertLevel("1"); // 1表示报警级别

        //区块链工程师拿到报警信息之后 进行上链操作
        try {
            Client client = SpringUtils.getBean(Client.class);
            SensorAlertService sensorAlertService = SensorAlertService.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
            TransactionReceipt transactionReceipt = sensorAlertService.addSensorAlertData(BigInteger.valueOf(alert.getId()),
                    alert.getAlertType(), alert.getAlertMessage(), alert.getParamName(), alert.getParamValue(),
                    alert.getThresholdMin(), alert.getThresholdMax(), alert.getAlertTime(), alert.getAlertLevel());
            if (transactionReceipt.isStatusOK()) {
                alert.setContractAddress(sensorAlertService.getContractAddress());
            } else {
                throw new ServerException("合约地址不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
        }
        // 触发红灯警报
        serialPortUtil.openRedLight();
        AudioPlayer.playAlarmSound();

        // 保存警告信息并推送到前端
        saveAlert(alert, "报警");
    }


    /**
     * 告警参数数据传输对象
     */
    private static class AlertParams {
        private final String paramKey;
        private final String paramName;
        private final double value;
        private final double[] thresholds;
        private final double minWarning;
        private final double maxWarning;
        private final String pastureId;
        private final String batchId;
        private final Device device;

        /**
         * 构造函数
         *
         * @param paramKey   参数键名
         * @param paramName  参数显示名称
         * @param value      参数当前值
         * @param thresholds 阈值数组
         * @param minWarning 最小预警值
         * @param maxWarning 最大预警值
         * @param pastureId  大棚/鱼棚ID
         * @param batchId    批次ID
         * @param device     设备对象
         */
        public AlertParams(String paramKey, String paramName, double value, double[] thresholds, double minWarning, double maxWarning, String pastureId, String batchId, Device device) {
            this.paramKey = paramKey;
            this.paramName = paramName;
            this.value = value;
            this.thresholds = thresholds;
            this.minWarning = minWarning;
            this.maxWarning = maxWarning;
            this.pastureId = pastureId;
            this.batchId = batchId;
            this.device = device;
        }
    }
}
