package com.frog.IaAgriculture.vo;

import java.io.Serializable;
import java.util.Date;

public class SoilSensorValueVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 温度 */
    private Double temperature;

    /** 湿度 */
    private Double humidity;

    /** 光照强度 */
    private Double lightLux;

    /** 土壤含水量 */
    private Double soilMoisture;

    /** 土壤温度 */
    private Double soilTemperature;

    /** 土壤 PH 值 */
    private Double soilPh;

    /** 土壤导电率 */
    private Double soilConductivity;

    /** 方向 */
    private String direction;

    /** 速度 */
    private Double speed;

    /** 时间 */
    private String time;

    /** 日期 */
    private String date;

    /** 大棚名称 */
    private String pastureName;

    /** 分区名称 */
    private String batchName;

    // Getter and Setter 方法
    public Double getTemperature() {
        return temperature;
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    public Double getHumidity() {
        return humidity;
    }
    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }
    public Double getLightLux() {
        return lightLux;
    }
    public void setLightLux(Double lightLux) {
        this.lightLux = lightLux;
    }
    public Double getSoilMoisture() {
        return soilMoisture;
    }
    public void setSoilMoisture(Double soilMoisture) {
        this.soilMoisture = soilMoisture;
    }
    public Double getSoilTemperature() {
        return soilTemperature;
    }
    public void setSoilTemperature(Double soilTemperature) {
        this.soilTemperature = soilTemperature;
    }
    public Double getSoilPh() {
        return soilPh;
    }
    public void setSoilPh(Double soilPh) {
        this.soilPh = soilPh;
    }
    public Double getSoilConductivity() {
        return soilConductivity;
    }
    public void setSoilConductivity(Double soilConductivity) {
        this.soilConductivity = soilConductivity;
    }
    public String getDirection() {
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public Double getSpeed() {
        return speed;
    }
    public void setSpeed(Double speed) {
        this.speed = speed;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getPastureName() {
        return pastureName;
    }
    public void setPastureName(String pastureName) {
        this.pastureName = pastureName;
    }
    public String getBatchName() {
        return batchName;
    }
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }
}