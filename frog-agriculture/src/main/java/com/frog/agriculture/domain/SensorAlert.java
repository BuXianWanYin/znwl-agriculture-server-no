package com.frog.agriculture.domain;

import java.io.Serializable;

/**
 * 传感器预警信息实体类
 * 
 * @author buxianwanyin
 * @date 2025-02-23
 */
public class SensorAlert implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 预警ID */
    private Long id;
    
    /** 预警类型 */
    private String alertType;
    
    /** 预警消息 */
    private String alertMessage;
    
    /** 参数名称 */
    private String paramName;
    
    /** 参数值 */
    private String paramValue;
    
    /** 阈值下限 */
    private String thresholdMin;
    
    /** 阈值上限 */
    private String thresholdMax;
    
    /** 大棚/鱼棚ID */
    private String pastureId;
    
    /** 分区ID */
    private String batchId;
    
    /** 设备ID */
    private String deviceId;
    
    /** 设备名称 */
    private String deviceName;
    
    /** 传感器类型 */
    private String sensorType;
    
    /** 预警时间 */
    private String alertTime;
    
    /** 预警日期 */
    private String alertDate;
    
    /** 处理状态（0未处理，1已处理） */
    private String status;
    
    /** 处理备注 */
    private String remark;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getThresholdMin() {
        return thresholdMin;
    }

    public void setThresholdMin(String thresholdMin) {
        this.thresholdMin = thresholdMin;
    }

    public String getThresholdMax() {
        return thresholdMax;
    }

    public void setThresholdMax(String thresholdMax) {
        this.thresholdMax = thresholdMax;
    }

    public String getPastureId() {
        return pastureId;
    }

    public void setPastureId(String pastureId) {
        this.pastureId = pastureId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(String alertTime) {
        this.alertTime = alertTime;
    }

    public String getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(String alertDate) {
        this.alertDate = alertDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
} 