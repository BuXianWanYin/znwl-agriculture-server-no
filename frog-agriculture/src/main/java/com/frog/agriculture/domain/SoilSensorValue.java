package com.frog.agriculture.domain;

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

    /** 土壤湿度 */
    @Excel(name = "土壤湿度")
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

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setDeviceId(String deviceId) 
    {
        this.deviceId = deviceId;
    }

    public String getDeviceId() 
    {
        return deviceId;
    }
    public void setPastureId(String pastureId) 
    {
        this.pastureId = pastureId;
    }

    public String getPastureId() 
    {
        return pastureId;
    }
    public void setBatchId(String batchId) 
    {
        this.batchId = batchId;
    }

    public String getBatchId() 
    {
        return batchId;
    }
    public void setTemperature(String temperature) 
    {
        this.temperature = temperature;
    }

    public String getTemperature() 
    {
        return temperature;
    }
    public void setHumidity(String humidity) 
    {
        this.humidity = humidity;
    }

    public String getHumidity() 
    {
        return humidity;
    }
    public void setLightLux(String lightLux) 
    {
        this.lightLux = lightLux;
    }

    public String getLightLux() 
    {
        return lightLux;
    }
    public void setSoilMoisture(String soilMoisture) 
    {
        this.soilMoisture = soilMoisture;
    }

    public String getSoilMoisture() 
    {
        return soilMoisture;
    }
    public void setSoilTemperature(String soilTemperature) 
    {
        this.soilTemperature = soilTemperature;
    }

    public String getSoilTemperature() 
    {
        return soilTemperature;
    }
    public void setSoilPh(String soilPh) 
    {
        this.soilPh = soilPh;
    }

    public String getSoilPh() 
    {
        return soilPh;
    }
    public void setSoilConductivity(String soilConductivity) 
    {
        this.soilConductivity = soilConductivity;
    }

    public String getSoilConductivity() 
    {
        return soilConductivity;
    }
    public void setDirection(String direction) 
    {
        this.direction = direction;
    }

    public String getDirection() 
    {
        return direction;
    }
    public void setSpeed(String speed) 
    {
        this.speed = speed;
    }

    public String getSpeed() 
    {
        return speed;
    }
    public void setTime(String time) 
    {
        this.time = time;
    }

    public String getTime() 
    {
        return time;
    }
    public void setDate(String date) 
    {
        this.date = date;
    }

    public String getDate() 
    {
        return date;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceId", getDeviceId())
            .append("pastureId", getPastureId())
            .append("batchId", getBatchId())
            .append("temperature", getTemperature())
            .append("humidity", getHumidity())
            .append("lightLux", getLightLux())
            .append("soilMoisture", getSoilMoisture())
            .append("soilTemperature", getSoilTemperature())
            .append("soilPh", getSoilPh())
            .append("soilConductivity", getSoilConductivity())
            .append("direction", getDirection())
            .append("speed", getSpeed())
            .append("time", getTime())
            .append("date", getDate())
            .toString();
    }
}
