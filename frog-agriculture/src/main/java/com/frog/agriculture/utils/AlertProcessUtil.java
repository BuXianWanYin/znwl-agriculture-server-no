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
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;

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
    public static String generateWarningMessage(double value, String alertType, String paramName,
                                                double minWarning, double maxWarning) {
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
    public static void setAlertDeviceInfo(com.frog.agriculture.domain.SensorAlert alert, Device device) {
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
    public static void setAlertTimeInfo(com.frog.agriculture.domain.SensorAlert alert) {
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
     * @param paramKey          参数键名
     * @param paramName         参数名称
     * @param value             当前值
     * @param thresholds        阈值数组
     * @param minWarning        最小预警值
     * @param maxWarning        最大预警值
     * @param pastureId         大棚ID
     * @param batchId           批次ID
     * @param device            设备对象
     * @param alertType         预警类型
     * @param alertMessage      预警消息
     * @param sensorAlertMapper 预警信息数据库操作接口
     * @param serialPortUtil    串口工具类
     */
    public static void processAlert(String paramKey, String paramName, double value, double[] thresholds,
                                    double minWarning, double maxWarning, String pastureId, String batchId,
                                    Device device, String alertType, String alertMessage,
                                    SensorAlertMapper sensorAlertMapper, SerialPortUtil serialPortUtil) {

        if (!hasActiveAlert(paramName, alertType, pastureId, batchId, device, sensorAlertMapper)) { // 如果不存在未处理的相同预警
            if (alertType.contains("严重")) { // 如果预警类型包含"严重"

                //调用方法生成报警
                generateSeriousAlert(paramKey, paramName, value, thresholds, pastureId, batchId,
                        device, alertType, alertMessage, sensorAlertMapper, serialPortUtil);
            } else {
                generateWarning(paramKey, paramName, value, thresholds, minWarning, maxWarning,
                        pastureId, batchId, device, alertType, alertMessage, sensorAlertMapper); // 生成普通预警
            }
        }
    }

    /**
     * 检查是否存在未处理的相同类型预警
     *
     * @param paramName         参数名称
     * @param alertType         预警类型
     * @param pastureId         大棚ID
     * @param batchId           批次ID
     * @param device            设备对象
     * @param sensorAlertMapper 预警信息数据库操作接口
     * @return true:存在未处理的相同预警 false:不存在
     */
    public static boolean hasActiveAlert(String paramName, String alertType, String pastureId,
                                         String batchId, Device device, SensorAlertMapper sensorAlertMapper) {
        try {
            com.frog.agriculture.domain.SensorAlert queryAlert = new com.frog.agriculture.domain.SensorAlert(); // 创建查询预警对象
            queryAlert.setParamName(paramName); // 设置参数名称
            queryAlert.setAlertType(alertType); // 设置预警类型
            queryAlert.setPastureId(pastureId); // 设置大棚ID
            queryAlert.setBatchId(batchId); // 设置批次ID
            queryAlert.setStatus("0"); // 设置状态为"0" 表示未处理

            queryAlert.setPastureType(isPastureTypeWater(paramName) ? "1" : "0"); // 根据参数判断大棚类型（水产/土壤）

            if (device != null) { // 如果设备不为空
                queryAlert.setSensorType(device.getSensorType()); // 设置设备的传感器类型
            }

            List<com.frog.agriculture.domain.SensorAlert> existingAlerts = sensorAlertMapper.selectSensorAlertList(queryAlert); // 查询当前符合条件的预警列表
            return existingAlerts != null && !existingAlerts.isEmpty(); // 如果存在预警则返回true，否则返回false
        } catch (Exception e) {
            log.error("查询现有预警失败: " + e.getMessage()); // 记录错误日志
            return false;
        }
    }

    /**
     * 更新活跃预警状态
     *
     * @param paramName         参数名称
     * @param pastureId         大棚ID
     * @param batchId           批次ID
     * @param device            设备对象
     * @param sensorAlertMapper 预警信息数据库操作接口
     * @param serialPortUtil    串口工具类
     */
    public static void updateActiveAlerts(String paramName, String pastureId, String batchId,
                                          Device device, SensorAlertMapper sensorAlertMapper,
                                          SerialPortUtil serialPortUtil) {
        try {
            // 创建查询对象
            com.frog.agriculture.domain.SensorAlert queryAlert = new com.frog.agriculture.domain.SensorAlert(); // 实例化查询预警对象
            // 设置参数名称
            queryAlert.setParamName(paramName); // 设置参数名称
            // 设置大棚ID
            queryAlert.setPastureId(pastureId); // 设置大棚ID
            // 设置批次ID
            queryAlert.setBatchId(batchId); // 设置批次ID
            // 设置状态为未处理
            queryAlert.setStatus("0"); // 设置状态为"0"：未处理
            // 设置警告级别为严重警告
            queryAlert.setAlertLevel("1"); // 设置预警级别为"1"（严重警告）
            // 设置大棚类型（水产/土壤）
            queryAlert.setPastureType(isPastureTypeWater(paramName) ? "1" : "0"); // 判断大棚类型并设置

            // 如果设备不为空，设置传感器类型
            if (device != null) { // 如果设备不为空
                queryAlert.setSensorType(device.getSensorType()); // 设置设备传感器类型
            }

            // 查询当前活跃的严重警告
            List<com.frog.agriculture.domain.SensorAlert> activeAlerts = sensorAlertMapper.selectSensorAlertList(queryAlert); // 根据条件查询严重预警

            // 处理严重警告   getAlertLevel eq 1
            if (activeAlerts != null && !activeAlerts.isEmpty() && activeAlerts.get(0).getAlertLevel().equals("1")) { // 如果存在严重警告
                // 遍历所有活跃的严重警告
                for (com.frog.agriculture.domain.SensorAlert alert : activeAlerts) { // 循环处理每个预警
                    // 更新警告状态为已处理
                    alert.setStatus("1"); // 设置状态为"1"表示已处理
                    // 添加处理备注
                    alert.setRemark("数据恢复正常，系统自动处理"); // 添加备注说明数据恢复正常
                    // 更新处理时间
                    alert.setUpdateTime(currentTimestamp()); // 设置更新处理时间为当前时间戳
                    // 保存更新到数据库
                    sensorAlertMapper.updateSensorAlert(alert); // 更新预警记录至数据库
                    // 记录日志
                    log.info("自动处理严重警告: " + paramName + " 数据恢复正常"); // 记录处理严重警告的信息

                    try {
                        // 发送关闭所有设备的命令
                        serialPortUtil.sendAllClose(); // 调用方法关闭所有设备
                        // 停止警报音频播放
                        AudioPlayer.stopAlarmSound(); // 停止播放报警声音
                        // 等待2秒
                        Thread.sleep(2000); // 线程休眠2秒
                        // 收回推杆
                        serialPortUtil.sendRelay4(); // 发送命令控制推杆回位
                        // 记录命令发送日志
                        log.info("已发送数据恢复继电器控制命令"); // 记录继电器控制命令发送成功的信息
                    } catch (Exception e) {
                        // 记录错误日志
                        log.error("发送继电器控制命令失败: " + e.getMessage()); // 记录发送继电器命令失败的错误日志
                    }
                }
            } else { // 如果不存在严重警告，则处理普通预警
                // 处理普通预警
                // 修改查询条件为普通预警级别
                queryAlert.setAlertLevel("0"); // 将查询条件设置为普通预警级别"0"
                // 查询活跃的普通预警
                activeAlerts = sensorAlertMapper.selectSensorAlertList(queryAlert); // 查询普通级别的活跃预警
                // 如果存在普通预警
                if (activeAlerts != null && !activeAlerts.isEmpty()) { // 如果查询结果不为空
                    // 遍历所有普通预警
                    for (com.frog.agriculture.domain.SensorAlert alert : activeAlerts) { // 循环处理每个普通预警
                        // 更新预警状态为已处理
                        alert.setStatus("1"); // 设置状态为"1"表示已处理
                        // 添加处理备注
                        alert.setRemark("数据恢复正常，系统自动处理"); // 添加备注信息
                        // 更新处理时间
                        alert.setUpdateTime(currentTimestamp()); // 设置更新处理时间为当前时间戳
                        // 保存更新到数据库
                        sensorAlertMapper.updateSensorAlert(alert); // 更新数据库中的预警记录
                        // 记录日志
                        log.info("自动处理预警: " + paramName + " 数据恢复正常"); // 记录处理普通预警信息到日志
                    } // 结束循环遍历普通预警
                }
            }
        } catch (Exception e) { // 捕获外部异常
            // 记录错误日志
            log.error("更新预警状态失败: " + e.getMessage()); // 记录更新预警状态失败的错误日志
        }
    }

    /**
     * 生成预警信息
     */
    private static void generateWarning(String paramKey, String paramName, double value, double[] thresholds,
                                        double minWarning, double maxWarning, String pastureId, String batchId,
                                        Device device, String alertType, String alertMessage,
                                        SensorAlertMapper sensorAlertMapper) {
        com.frog.agriculture.domain.SensorAlert alert = createBaseAlert(paramKey, paramName, value, thresholds, pastureId,
                batchId, device, alertType, alertMessage); // 根据基础参数创建预警对象
        alert.setAlertLevel("0"); // 设置预警级别为"0"表示普通预警
        saveAlert(alert, "预警", sensorAlertMapper); // 保存预警信息到数据库并推送前端
    }

    /**
     * 生成报警信息并执行相应的警报操作
     *
     * @param paramKey          参数键名，用于标识传感器类型（如"temperature"、"humidity"等）
     * @param paramName         参数的显示名称，用于在警告消息中显示（如"温度"、"湿度"等）
     * @param value             当前传感器读数值
     * @param thresholds        阈值数组，[0]为最小阈值，[1]为最大阈值
     * @param pastureId         大棚 鱼棚 ID，用于标识警告发生的位置
     * @param batchId           批次ID，用于标识警告发生的生产批次
     * @param device            设备对象，包含设备ID、名称等设备相关信息
     * @param alertType         警告类型（如"严重低值警告"、"严重高值警告"等）
     * @param alertMessage      警告消息内容
     * @param sensorAlertMapper 数据库操作接口，用于保存警告信息
     * @param serialPortUtil    串口工具类，用于控制硬件设备（继电器等）
     */
    private static void generateSeriousAlert(String paramKey, String paramName, double value, double[] thresholds,
                                             String pastureId, String batchId, Device device, String alertType,
                                             String alertMessage, SensorAlertMapper sensorAlertMapper,
                                             SerialPortUtil serialPortUtil) {


        // 创建基础警告对象
        SensorAlert alert = createBaseAlert(paramKey, paramName, value, thresholds, pastureId,
                batchId, device, alertType, alertMessage);

        //区块链工程师拿到报警信息之后 进行上链操作
        try {
            Client client = SpringUtils.getBean(Client.class);
            SensorAlertService sensorAlertService = SensorAlertService.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
            TransactionReceipt transactionReceipt = sensorAlertService.addSensorAlertData(BigInteger.valueOf(alert.getId()), alert.getAlertType(), alert.getAlertMessage(), alert.getParamName(), alert.getParamValue(), alert.getThresholdMin(), alert.getThresholdMax(), alert.getSensorType(), alert.getAlertTime(), alert.getAlertLevel());
            if (transactionReceipt.isStatusOK()) {
                alert.setContractAddress(transactionReceipt.getContractAddress());
            } else {
                throw new ServerException("合约地址不存在"); // 抛出服务器异常
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器错误异常
        }

        // 设置为严重警告级别
        alert.setAlertLevel("1"); // 1表示严重警告级别

        // 触发硬件警报
        serialPortUtil.sendMultipleRelays(); // 激活多个继电器进行警报

        AudioPlayer.playAlarmSound(); // 播放声音警报

        // 保存警告信息并推送到前端   全栈工程师做
        saveAlert(alert, "严重警告", sensorAlertMapper);
    }

    /**
     * 创建基础预警对象
     */
    private static com.frog.agriculture.domain.SensorAlert createBaseAlert(String paramKey, String paramName, double value,
                                                                           double[] thresholds, String pastureId, String batchId,
                                                                           Device device, String alertType, String alertMessage) {
        com.frog.agriculture.domain.SensorAlert alert = new com.frog.agriculture.domain.SensorAlert(); // 实例化一个新的SensorAlert对象
        alert.setAlertType(alertType); // 设置预警类型
        alert.setAlertMessage(alertMessage); // 设置预警消息
        alert.setParamName(paramName); // 设置参数名称
        alert.setParamValue(String.valueOf(value)); // 设置当前参数的值
        alert.setThresholdMin(String.valueOf(thresholds[0])); // 设置阈值下限
        alert.setThresholdMax(String.valueOf(thresholds[1])); // 设置阈值上限
        alert.setPastureId(pastureId); // 设置大棚ID
        alert.setBatchId(batchId); // 设置批次ID
        alert.setPastureType(isPastureTypeWater(paramKey) ? "1" : "0"); // 根据参数判断大棚类型
        setAlertDeviceInfo(alert, device); // 设置设备相关预警信息
        setAlertTimeInfo(alert); // 设置时间相关预警信息
        return alert; // 返回创建的预警对象
    }

    /**
     * 保存预警信息到数据库
     */
    private static void saveAlert(SensorAlert alert, String alertTypeName, SensorAlertMapper sensorAlertMapper) {
        try {
            sensorAlertMapper.insertSensorAlert(alert); // 插入预警记录到数据库
            log.info("生成" + alertTypeName + "信息: " + alert.getAlertMessage()); // 记录生成预警信息的日志

            // 通过WebSocket推送预警信息到前端  全站工程师做
            AlertWebSocketServer.sendInfo(alert); // 通过WebSocket发送预警信息到前端

            log.info("发送预警信息"); // 记录预警信息发送日志
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
    public static void checkThresholdsAndGenerateAlerts(SoilSensorValue sensorValue,
                                                        FishWaterQuality fishWaterQuality,
                                                        Map<String, Device> sensorBindings,
                                                        SensorAlertMapper sensorAlertMapper,
                                                        com.frog.common.utils.SerialPortUtil serialPortUtil) {
        // 检查土壤和环境数据
        checkAndAlertForSoilData(sensorValue, sensorBindings, sensorAlertMapper, serialPortUtil); // 检查土壤数据并生成预警

        // 检查水质数据
        if (fishWaterQuality != null && fishWaterQuality.getWaterTemperature() != null) { // 如果水质数据不为空且水温数据不为空
            checkAndAlertForWaterData(fishWaterQuality, sensorBindings.get("8"), sensorAlertMapper, serialPortUtil); // 检查水质数据并生成预警，传入相应设备
        }


    }

    /**
     * 检查土壤和环境数据是否超过阈值并生成预警
     */
    private static void checkAndAlertForSoilData(SoilSensorValue sensorValue,
                                                 Map<String, Device> sensorBindings,
                                                 SensorAlertMapper sensorAlertMapper,
                                                 com.frog.common.utils.SerialPortUtil serialPortUtil) {
        // 检查温度数据
        if (sensorValue.getTemperature() != null) { // 如果温度数据不为空
            try { // 使用try捕获转换异常
                // 将温度字符串转换为double类型
                double temperature = Double.parseDouble(sensorValue.getTemperature()); // 转换温度字符串为double
                // 检查温度是否超过阈值并生成预警
                checkThresholdAndAlert("temperature", temperature, "温度",
                        sensorValue.getPastureId(), sensorValue.getBatchId(),
                        sensorBindings.get("2"), sensorAlertMapper, serialPortUtil); // 调用检查方法处理温度数据
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
                checkThresholdAndAlert("humidity", humidity, "湿度",
                        sensorValue.getPastureId(), sensorValue.getBatchId(),
                        sensorBindings.get("2"), sensorAlertMapper, serialPortUtil); // 调用检查方法处理湿度数据
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
                checkThresholdAndAlert("light", light, "光照",
                        sensorValue.getPastureId(), sensorValue.getBatchId(),
                        sensorBindings.get("2"), sensorAlertMapper, serialPortUtil); // 调用检查方法处理光照数据
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
                checkThresholdAndAlert("speed", speed, "风速",
                        sensorValue.getPastureId(), sensorValue.getBatchId(),
                        sensorBindings.get("3"), sensorAlertMapper, serialPortUtil); // 调用检查方法处理风速数据
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
                checkThresholdAndAlert("soil_temperature", soilTemp, "土壤温度",
                        sensorValue.getPastureId(), sensorValue.getBatchId(),
                        sensorBindings.get("4"), sensorAlertMapper, serialPortUtil); // 调用检查方法处理土壤温度数据
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
                checkThresholdAndAlert("soil_ph", soilPh, "土壤pH",
                        sensorValue.getPastureId(), sensorValue.getBatchId(),
                        sensorBindings.get("5"), sensorAlertMapper, serialPortUtil); // 调用检查方法处理土壤pH数据
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
                checkThresholdAndAlert("conductivity", conductivity, "土壤电导率",
                        sensorValue.getPastureId(), sensorValue.getBatchId(),
                        sensorBindings.get("6"), sensorAlertMapper, serialPortUtil); // 调用检查方法处理土壤电导率数据
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
                checkThresholdAndAlert("moisture", moisture, "土壤水分",
                        sensorValue.getPastureId(), sensorValue.getBatchId(),
                        sensorBindings.get("6"), sensorAlertMapper, serialPortUtil); // 调用检查方法处理土壤水分数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("土壤水分数据格式错误: " + sensorValue.getSoilMoisture()); // 记录土壤水分数据格式错误日志
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
        // 从水质数据中获取大棚ID和批次ID
        String pastureId = String.valueOf(fishWaterQuality.getFishPastureId()); // 获取大棚ID并转换为字符串
        String batchId = String.valueOf(fishWaterQuality.getFishPastureBatchId()); // 获取批次ID并转换为字符串

        // 检查水温数据
        if (fishWaterQuality.getWaterTemperature() != null) { // 如果水温数据不为空
            try { // 使用try捕获转换异常
                // 将水温字符串转换为double类型
                double waterTemp = Double.parseDouble(fishWaterQuality.getWaterTemperature()); // 转换水温为double
                // 检查水温是否超过阈值并生成预警
                checkThresholdAndAlert("water_temperature", waterTemp, "水温",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil); // 调用检查方法处理水温数据
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
                checkThresholdAndAlert("water_ph", waterPh, "水pH",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil); // 调用检查方法处理水pH数据
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
                checkThresholdAndAlert("oxygen", oxygen, "溶解氧",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil); // 调用检查方法处理溶解氧数据
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
                checkThresholdAndAlert("ammonia", ammonia, "氨氮",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil); // 调用检查方法处理氨氮数据
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
                checkThresholdAndAlert("nitrite", nitrite, "亚硝酸盐",
                        pastureId, batchId, waterDevice, sensorAlertMapper, serialPortUtil); // 调用检查方法处理亚硝酸盐数据
            } catch (NumberFormatException e) { // 捕获异常
                // 数据格式转换失败时记录警告日志
                log.warn("亚硝酸盐数据格式错误: " + fishWaterQuality.getWaterNitriteContent()); // 记录亚硝酸盐数据格式转换错误日志
            }
        }
    }


    /**
     * 检查单个参数是否接近或超过阈值，并生成预警信息
     *
     * @param paramKey          参数键名(如:"temperature","humidity"等)
     * @param value             当前参数的实际值
     * @param paramName         参数的显示名称(如:"温度","湿度"等)
     * @param pastureId         大棚/养殖棚ID
     * @param batchId           批次ID
     * @param device            设备对象,包含设备ID、名称等信息
     * @param sensorAlertMapper 预警信息数据库操作接口
     * @param serialPortUtil    串口工具类,用于控制继电器等硬件操作
     */
    private static void checkThresholdAndAlert(String paramKey, double value, String paramName,
                                               String pastureId, String batchId, Device device,
                                               SensorAlertMapper sensorAlertMapper,
                                               SerialPortUtil serialPortUtil) {
        // 根据参数键获取阈值配置
        double[] thresholds = ThresholdConfigUtil.getThresholdByKey(paramKey); // 调用工具方法获取阈值配置
        if (thresholds == null) { // 如果未找到阈值配置
            // 如果未找到阈值配置，记录警告日志并返回
            log.warn("未找到参数 " + paramKey + " 的阈值配置"); // 记录未找到阈值配置的警告
            return;
        }

        // 获取阈值缓冲区间
        double[] buffers = getThresholdBuffer(paramKey); // 调用方法获取阈值缓冲区间
        double minBuffer = buffers[0];  // 取出最小缓冲值
        double maxBuffer = buffers[1];  // 取出最大缓冲值

        // 计算预警阈值
        double minWarning = thresholds[0] + minBuffer;  // 最小预警值为阈值下限加缓冲
        double maxWarning = thresholds[1] - maxBuffer;  // 最大预警值为阈值上限减缓冲

        String alertType = null;    // 定义预警类型变量
        String alertMessage = null; // 定义预警消息变量

        // 检查是否超出阈值范围
        if (value < thresholds[0]) { // 如果当前值小于阈值下限
            // 当前值低于最小阈值
            alertMessage = paramName + "过低：当前值" + value + "，最小阈值" + thresholds[0]; // 构造低于阈值的预警消息
            // 如果低于最小阈值的80%，则为严重警告，否则为普通预警
            alertType = value < thresholds[0] * 0.8 ? "严重低值警告" : "低值预警"; // 根据比例判断预警严重程度
        } else if (value > thresholds[1]) { // 如果当前值大于阈值上限
            // 当前值高于最大阈值
            alertMessage = paramName + "过高：当前值" + value + "，最大阈值" + thresholds[1]; // 构造高于阈值的预警消息
            // 如果高于最大阈值的120%，则为严重警告，否则为普通预警
            alertType = value > thresholds[1] * 1.2 ? "严重高值警告" : "高值预警"; // 根据比例判断严重程度
        }

        // 如果存在预警，处理预警信息
        if (alertType != null) { // 如果预警类型不为空
            processAlert(paramKey, paramName, value, thresholds, minWarning, maxWarning,
                    pastureId, batchId, device, alertType, alertMessage, sensorAlertMapper, serialPortUtil); // 调用处理预警方法
            return;
        }

        // 检查是否需要生成预警（对于需要检查双向阈值的参数）
        if (shouldCheckBothThresholds(paramKey)) { // 如果需要检查双向阈值
            // 检查是否接近预警阈值
            alertType = checkWarningThresholds(value, minWarning, maxWarning, paramName); // 调用方法检查是否接近预警阈值
            if (alertType != null) { // 如果返回预警类型不为空
                // 生成预警消息
                alertMessage = generateWarningMessage(value, alertType, paramName, minWarning, maxWarning); // 生成预警消息
                // 处理预警信息
                processAlert(paramKey, paramName, value, thresholds, minWarning, maxWarning,
                        pastureId, batchId, device, alertType, alertMessage, sensorAlertMapper, serialPortUtil); // 调用方法处理预警信息
                return;
            }
        }

        // 如果数据恢复正常，更新未处理的预警状态
        updateActiveAlerts(paramName, pastureId, batchId, device, sensorAlertMapper, serialPortUtil); // 调用方法更新已存在预警的状态
    }
}
