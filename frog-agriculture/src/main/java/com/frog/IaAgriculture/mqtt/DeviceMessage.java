package com.frog.IaAgriculture.mqtt;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 设备消息类，用于封装设备采集的各种环境数据
 */
@Data
public class DeviceMessage {
    // 设备ID
    private String id;
    // 温度读数
    private String temp;
    // 湿度读数
    private String hum;
    // 气压读数，使用BigDecimal以精确表示浮点数
    private BigDecimal p;
    // 光照强度读数
    private String lux;
    // 时间戳，记录数据采集的时间
    private String time;
    // 日期戳，记录数据采集的日期
    private String date;
    // 火灾警报标志，表示是否有火灾风险
    private Integer fire;
    // 土壤湿度读数
    private String soil;
    // 空气质量读数，使用BigDecimal以精确表示浮点数
    private BigDecimal air;
    // 继电器状态标志
    private Integer relay;

    /**
     * 重写toString方法，以便于打印设备消息对象
     * @return 设备消息的字符串表示
     */
    @Override
    public String toString() {
        return "DeviceMessage{" +
                "id='" + id + '\'' +
                ", temp='" + temp + '\'' +
                ", hum='" + hum + '\'' +
                ", p=" + p +
                ", lux='" + lux + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", fire=" + fire +
                ", soil='" + soil + '\'' +
                ", air=" + air +
                ", relay=" + relay +
                '}';
    }
}
