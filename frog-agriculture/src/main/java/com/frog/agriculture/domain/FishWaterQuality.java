package com.frog.agriculture.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 水质数据对象 fish_water_quality
 *
 * @author nealtsiao
 * @date 2025-02-23
 */
public class FishWaterQuality extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 设备id
     */
    @Excel(name = "设备id")
    private Long deviceId;

    /**
     * 鱼养殖棚id
     */
    @Excel(name = "鱼养殖棚id")
    private Long fishPastureId;

    /**
     * 鱼分区(批次)id
     */
    @Excel(name = "鱼分区(批次)id")
    private Long fishPastureBatchId;

    /**
     * 水温
     */
    @Excel(name = "水温")
    private String waterTemperature;

    /**
     * ph值
     */
    @Excel(name = "ph值")
    private String waterPhValue;

    /**
     * 溶解氧含量
     */
    @Excel(name = "溶解氧含量")
    private String waterOxygenContent;

    /**
     * 亚硝酸盐含量
     */
    @Excel(name = "亚硝酸盐含量")
    private String waterNitriteContent;



    /**
     * 养殖棚名字
     */
    @Excel(name = "养殖棚名字")
    private String fishPastureName;

    public String getFishPastureBatchName() {
        return fishPastureBatchName;
    }

    public void setFishPastureBatchName(String fishPastureBatchName) {
        this.fishPastureBatchName = fishPastureBatchName;
    }

    public String getFishPastureName() {
        return fishPastureName;
    }

    public void setFishPastureName(String fishPastureName) {
        this.fishPastureName = fishPastureName;
    }

    /**
     * 鱼分区名字
     */
    @Excel(name = "鱼分区名字")
    private String fishPastureBatchName;

    public String getWaterAmmoniaNitrogenContent() {
        return waterAmmoniaNitrogenContent;
    }

    public void setWaterAmmoniaNitrogenContent(String waterAmmoniaNitrogenContent) {
        this.waterAmmoniaNitrogenContent = waterAmmoniaNitrogenContent;
    }

    /**
     * 氨氮含量
     */
    @Excel(name = "氨氮含量")
    private String waterAmmoniaNitrogenContent;
    /**
     * 时间
     */
    @Excel(name = "时间")
    private String time;

    /**
     * 日期
     */
    @Excel(name = "日期")
    private String date;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setFishPastureId(Long fishPastureId) {
        this.fishPastureId = fishPastureId;
    }

    public Long getFishPastureId() {
        return fishPastureId;
    }

    public void setFishPastureBatchId(Long fishPastureBatchId) {
        this.fishPastureBatchId = fishPastureBatchId;
    }

    public Long getFishPastureBatchId() {
        return fishPastureBatchId;
    }

    public void setWaterTemperature(String waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public String getWaterTemperature() {
        return waterTemperature;
    }

    public void setWaterPhValue(String waterPhValue) {
        this.waterPhValue = waterPhValue;
    }

    public String getWaterPhValue() {
        return waterPhValue;
    }

    public void setWaterOxygenContent(String waterOxygenContent) {
        this.waterOxygenContent = waterOxygenContent;
    }

    public String getWaterOxygenContent() {
        return waterOxygenContent;
    }

    public void setWaterNitriteContent(String waterNitriteContent) {
        this.waterNitriteContent = waterNitriteContent;
    }

    public String getWaterNitriteContent() {
        return waterNitriteContent;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deviceId", getDeviceId())
                .append("fishPastureId", getFishPastureId())
                .append("fishPastureBatchId", getFishPastureBatchId())
                .append("waterTemperature", getWaterTemperature())
                .append("waterPhValue", getWaterPhValue())
                .append("waterOxygenContent", getWaterOxygenContent())
                .append("waterNitriteContent", getWaterNitriteContent())
                .append("waterAmmoniaNitrogenContent", getWaterAmmoniaNitrogenContent())
                .append("time", getTime())
                .append("date", getDate())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
