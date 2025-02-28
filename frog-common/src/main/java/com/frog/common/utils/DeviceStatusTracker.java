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

    public void recordSuccess(String deviceId) {
        currentStatus.put(deviceId, true);
        resetFailureCount(deviceId);
    }

    public void recordFailure(String deviceId) {
        currentStatus.put(deviceId, false);
        failureCounts.merge(deviceId, 1, Integer::sum);
    }

    public void resetFailureCount(String deviceId) {
        failureCounts.put(deviceId, 0);
    }

    public int getFailureCount(String deviceId) {
        return failureCounts.getOrDefault(deviceId, 0);
    }

    public Map<String, Boolean> getCurrentStatus() {
        return currentStatus;
    }
}
