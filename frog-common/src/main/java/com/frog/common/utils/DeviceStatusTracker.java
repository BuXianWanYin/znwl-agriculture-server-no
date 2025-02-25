package com.frog.common.utils;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 设备状态跟踪器
public class DeviceStatusTracker {
    private Map<String, Integer> failureCounts = new ConcurrentHashMap<>();
    private Map<String, Boolean> currentStatus = new ConcurrentHashMap<>();
    private static final int MAX_RETRY = 3;

    public void recordSuccess(String sensorType) {
        failureCounts.remove(sensorType);
        currentStatus.put(sensorType, true);
    }

    public void recordFailure(String sensorType) {
        int count = failureCounts.getOrDefault(sensorType, 0) + 1;
        failureCounts.put(sensorType, count);
        currentStatus.put(sensorType, count >= MAX_RETRY ? false : currentStatus.getOrDefault(sensorType, true));
    }

    public Map<String, Boolean> getCurrentStatus() {
        return new HashMap<>(currentStatus);
    }
}
