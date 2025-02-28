package com.frog.agriculture.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.mapper.DeviceMapper;
import com.frog.common.utils.DeviceStatusTracker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备状态工具类
 */
public class DeviceStatusUtil {
    private static final Log log = LogFactory.getLog(DeviceStatusUtil.class);
    private static final int OFFLINE_THRESHOLD = 3; // 连续失败次数阈值

    /**
     * 更新设备状态
     */
    public static void updateDeviceStatus(Map<String, Boolean> statusMap,
                                          DeviceMapper deviceMapper,
                                          Map<String, Device> deviceCache,
                                          DeviceStatusTracker statusTracker) {
        statusMap.forEach((sensorType, isOnline) -> {
            try {
                Device device = getCachedDevice(sensorType, deviceMapper, deviceCache);
                if (device == null) {
                    log.warn("未找到传感器类型为" + sensorType + "的设备");
                    return;
                }

                String currentStatus = device.getStatus();
                String newStatus = currentStatus;

                if (isOnline) {
                    newStatus = "1";
                    statusTracker.resetFailureCount(sensorType);
                } else {
                    int failureCount = statusTracker.getFailureCount(sensorType);
                    if (failureCount >= OFFLINE_THRESHOLD) {
                        newStatus = "0";
                    }
                }

                if (!newStatus.equals(currentStatus)) {
                    device.setStatus(newStatus);
                    deviceMapper.updateById(device);
                    log.info("设备:" + device.getDeviceId() + " 状态更新为" +
                            (newStatus.equals("1") ? "在线" : "离线"));
                }
            } catch (Exception e) {
                log.error("更新设备状态失败:" + e.getMessage());
            }
        });
    }

    /**
     * 从缓存获取设备信息
     */
    public static Device getCachedDevice(String sensorType,
                                         DeviceMapper deviceMapper,
                                         Map<String, Device> deviceCache) {
        return deviceCache.computeIfAbsent(sensorType, key ->
                deviceMapper.selectOne(new QueryWrapper<Device>()
                        .eq("sensorType", sensorType)
                        .last("LIMIT 1"))
        );
    }

    /**
     * 初始化传感器指令
     */
    public static void initializeSensorCommands(DeviceMapper deviceMapper,
                                                Map<String, String> sensorCommands) {
        QueryWrapper<Device> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("sensorType");
        queryWrapper.isNotNull("sensor_command");
        deviceMapper.selectList(queryWrapper).forEach(device -> {
            String sensorType = device.getSensorType().trim();
            String command = device.getSensorCommand().trim();
            if (!sensorType.isEmpty() && !command.isEmpty()) {
                sensorCommands.put(sensorType, command);
                log.info("加载传感器指令 - 类型：" + sensorType + "，指令：" + command);
            }
        });

        if (sensorCommands.isEmpty()) {
            log.error("未从数据库加载到任何传感器指令！");
        }
    }
}