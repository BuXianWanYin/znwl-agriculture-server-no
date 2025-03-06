package com.frog.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.frog.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * AI最优参数分析对象 ai_optimal_analysis
 *
 * @author aj
 * @date 2024-03-16
 */
@TableName(value = "ai_optimal_analysis")
public class AiOptimalAnalysis {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 鱼种ID
     */
    private Long speciesId;

    /**
     * 种质ID
     */
    private Long germplasmId;

    /**
     * 最适温度范围
     */
    private String optimalTemperature;

    /**
     * 最适湿度范围
     */
    private String optimalHumidity;

    /**
     * 最适光照范围
     */
    private String optimalLight;

    /**
     * 最适土壤pH范围
     */
    private String optimalSoilPh;

    /**
     * 最适风向
     */
    private String optimalWindDirection;

    /**
     * 最适风速范围(m/s)
     */
    private String optimalWindSpeed;

    /**
     * 最适水温范围
     */
    private String optimalWaterTemperature;

    /**
     * 最适水质pH范围
     */
    private String optimalWaterPh;

    /**
     * 最适溶解氧范围(mg/L)
     */
    private String optimalDissolvedOxygen;

    /**
     * 最适氨氮范围(mg/L)
     */
    private String optimalAmmonia;

    /**
     * 最适亚硝酸盐范围(mg/L)
     */
    private String optimalNitrite;

    /**
     * 生长速度(%)
     */
    private Integer growthRate;

    /**
     * 抗病能力(%)
     */
    private Integer diseaseResistance;

    /**
     * 饲料/肥料转化率(%)
     */
    private Integer feedConversion;

    /**
     * 市场认可度(%)
     */
    private Integer marketAcceptance;

    /**
     * 水质/水分管理建议
     */
    private String waterManagement;

    /**
     * 投喂/肥料管理建议
     */
    private String feedingManagement;

    /**
     * 疾病防控/病虫害防治建议
     */
    private String diseasePrevention;

    /**
     * 环境监控/环境监控建议
     */
    private String environmentMonitoring;

    /**
     * 生长评估
     */
    private String growthAssessment;

    /**
     * 养殖/种植难度
     */
    private String cultivationDifficulty;

    /**
     * 综合建议
     */
    private String generalRecommendations;

    /**
     * 市场分析
     */
    private String marketAnalysis;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSpeciesId(Long speciesId) {
        this.speciesId = speciesId;
    }

    public Long getSpeciesId() {
        return speciesId;
    }

    public void setGermplasmId(Long germplasmId) {
        this.germplasmId = germplasmId;
    }

    public Long getGermplasmId() {
        return germplasmId;
    }

    public void setOptimalTemperature(String optimalTemperature) {
        this.optimalTemperature = optimalTemperature;
    }

    public String getOptimalTemperature() {
        return optimalTemperature;
    }

    public void setOptimalHumidity(String optimalHumidity) {
        this.optimalHumidity = optimalHumidity;
    }

    public String getOptimalHumidity() {
        return optimalHumidity;
    }

    public void setOptimalLight(String optimalLight) {
        this.optimalLight = optimalLight;
    }

    public String getOptimalLight() {
        return optimalLight;
    }

    public void setOptimalSoilPh(String optimalSoilPh) {
        this.optimalSoilPh = optimalSoilPh;
    }

    public String getOptimalSoilPh() {
        return optimalSoilPh;
    }

    public void setOptimalWindDirection(String optimalWindDirection) {
        this.optimalWindDirection = optimalWindDirection;
    }

    public String getOptimalWindDirection() {
        return optimalWindDirection;
    }

    public void setOptimalWindSpeed(String optimalWindSpeed) {
        this.optimalWindSpeed = optimalWindSpeed;
    }

    public String getOptimalWindSpeed() {
        return optimalWindSpeed;
    }

    public void setOptimalWaterTemperature(String optimalWaterTemperature) {
        this.optimalWaterTemperature = optimalWaterTemperature;
    }

    public String getOptimalWaterTemperature() {
        return optimalWaterTemperature;
    }

    public void setOptimalWaterPh(String optimalWaterPh) {
        this.optimalWaterPh = optimalWaterPh;
    }

    public String getOptimalWaterPh() {
        return optimalWaterPh;
    }

    public void setOptimalDissolvedOxygen(String optimalDissolvedOxygen) {
        this.optimalDissolvedOxygen = optimalDissolvedOxygen;
    }

    public String getOptimalDissolvedOxygen() {
        return optimalDissolvedOxygen;
    }

    public void setOptimalAmmonia(String optimalAmmonia) {
        this.optimalAmmonia = optimalAmmonia;
    }

    public String getOptimalAmmonia() {
        return optimalAmmonia;
    }

    public void setOptimalNitrite(String optimalNitrite) {
        this.optimalNitrite = optimalNitrite;
    }

    public String getOptimalNitrite() {
        return optimalNitrite;
    }

    public void setGrowthRate(Integer growthRate) {
        this.growthRate = growthRate;
    }

    public Integer getGrowthRate() {
        return growthRate;
    }

    public void setDiseaseResistance(Integer diseaseResistance) {
        this.diseaseResistance = diseaseResistance;
    }

    public Integer getDiseaseResistance() {
        return diseaseResistance;
    }

    public void setFeedConversion(Integer feedConversion) {
        this.feedConversion = feedConversion;
    }

    public Integer getFeedConversion() {
        return feedConversion;
    }

    public void setMarketAcceptance(Integer marketAcceptance) {
        this.marketAcceptance = marketAcceptance;
    }

    public Integer getMarketAcceptance() {
        return marketAcceptance;
    }

    public void setWaterManagement(String waterManagement) {
        this.waterManagement = waterManagement;
    }

    public String getWaterManagement() {
        return waterManagement;
    }

    public void setFeedingManagement(String feedingManagement) {
        this.feedingManagement = feedingManagement;
    }

    public String getFeedingManagement() {
        return feedingManagement;
    }

    public void setDiseasePrevention(String diseasePrevention) {
        this.diseasePrevention = diseasePrevention;
    }

    public String getDiseasePrevention() {
        return diseasePrevention;
    }

    public void setEnvironmentMonitoring(String environmentMonitoring) {
        this.environmentMonitoring = environmentMonitoring;
    }

    public String getEnvironmentMonitoring() {
        return environmentMonitoring;
    }

    public void setGrowthAssessment(String growthAssessment) {
        this.growthAssessment = growthAssessment;
    }

    public String getGrowthAssessment() {
        return growthAssessment;
    }

    public void setCultivationDifficulty(String cultivationDifficulty) {
        this.cultivationDifficulty = cultivationDifficulty;
    }

    public String getCultivationDifficulty() {
        return cultivationDifficulty;
    }

    public void setGeneralRecommendations(String generalRecommendations) {
        this.generalRecommendations = generalRecommendations;
    }

    public String getGeneralRecommendations() {
        return generalRecommendations;
    }

    public void setMarketAnalysis(String marketAnalysis) {
        this.marketAnalysis = marketAnalysis;
    }

    public String getMarketAnalysis() {
        return marketAnalysis;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("speciesId", getSpeciesId())
                .append("germplasmId", getGermplasmId())
                .append("optimalTemperature", getOptimalTemperature())
                .append("optimalHumidity", getOptimalHumidity())
                .append("optimalLight", getOptimalLight())
                .append("optimalSoilPh", getOptimalSoilPh())
                .append("optimalWindDirection", getOptimalWindDirection())
                .append("optimalWindSpeed", getOptimalWindSpeed())
                .append("optimalWaterTemperature", getOptimalWaterTemperature())
                .append("optimalWaterPh", getOptimalWaterPh())
                .append("optimalDissolvedOxygen", getOptimalDissolvedOxygen())
                .append("optimalAmmonia", getOptimalAmmonia())
                .append("optimalNitrite", getOptimalNitrite())
                .append("growthRate", getGrowthRate())
                .append("diseaseResistance", getDiseaseResistance())
                .append("feedConversion", getFeedConversion())
                .append("marketAcceptance", getMarketAcceptance())
                .append("waterManagement", getWaterManagement())
                .append("feedingManagement", getFeedingManagement())
                .append("diseasePrevention", getDiseasePrevention())
                .append("environmentMonitoring", getEnvironmentMonitoring())
                .append("growthAssessment", getGrowthAssessment())
                .append("cultivationDifficulty", getCultivationDifficulty())
                .append("generalRecommendations", getGeneralRecommendations())
                .append("marketAnalysis", getMarketAnalysis())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("remark", getRemark())
                .toString();
    }
}
