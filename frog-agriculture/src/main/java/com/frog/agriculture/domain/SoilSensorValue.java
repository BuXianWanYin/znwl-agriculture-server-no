package com.frog.agriculture.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 菜的环境数据对象 soil_sensor_value
 * 
 * @author nealtsiao
 * @date 2025-02-23
 */
@Data
public class SoilSensorValue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String id;

    /** 设备id */
    @Excel(name = "设备id")
    private String deviceId;

    /** 大棚id，空为没有绑定大棚 */
    @Excel(name = "大棚id，空为没有绑定大棚")
    private String pastureId;

    /** 分区id，空为没有绑定分区 */
    @Excel(name = "分区id，空为没有绑定分区")
    private String batchId;

    /** 环境温度 */
    @Excel(name = "环境温度")
    private String temperature;

    /** 环境湿度 */
    @Excel(name = "环境湿度")
    private String humidity;

    /** 光照强度 */
    @Excel(name = "光照强度")
    private String lightLux;

    /** 土壤含水率 */
    @Excel(name = "土壤含水率")
    private String soilMoisture;

    /** 土壤温度 */
    @Excel(name = "土壤温度")
    private String soilTemperature;

    /** 土壤ph值 */
    @Excel(name = "土壤ph值")
    private String soilPh;

    /** 土壤电导率 */
    @Excel(name = "土壤电导率")
    private String soilConductivity;

    /** 风向 */
    @Excel(name = "风向")
    private String direction;

    /** 风速 */
    @Excel(name = "风速")
    private String speed;

    /** 时间 */
    @Excel(name = "时间")
    private String time;

    /** 日期 */
    @Excel(name = "日期")
    private String date;

    /** 日期 */
    @Excel(name = "删除标志")
    private String del_flag;

}
