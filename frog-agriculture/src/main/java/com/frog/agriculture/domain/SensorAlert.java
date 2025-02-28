package com.frog.agriculture.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 传感器预警信息实体类
 *
 * @author buxianwanyin
 * @date 2025-02-23
 */
@Data
public class SensorAlert implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 预警ID
     */
    private Long id;

    /**
     * 预警类型
     */
    private String alertType;

    /**
     * 预警消息
     */
    private String alertMessage;

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 阈值下限
     */
    private String thresholdMin;

    /**
     * 阈值上限
     */
    private String thresholdMax;

    /**
     * 大棚/鱼棚ID
     */
    private String pastureId;

    /**
     * 分区ID
     */
    private String batchId;

    /**
     * 大棚/鱼棚名称
     */
    private String pastureName;

    //责任人id
    private String batchHead;

    /**
     * 分区名称
     */
    private String batchName;
    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 传感器类型
     */
    private String sensorType;

    /**
     * 预警时间
     */
    private String alertTime;

    /**
     * 预警日期
     */
    private String alertDate;

    /**
     * 区分大棚还是鱼棚  0是大棚 1是鱼棚
     */
    private String pastureType;


    /**
     * 处理状态（0未处理，1已处理）
     */
    private String status;

    /**
     * 处理备注
     */
    private String remark;

    /**
     * 处理备注
     */
    private String updateTime;

} 