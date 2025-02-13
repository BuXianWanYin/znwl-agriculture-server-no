package com.frog.domain;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Species extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 鱼种ID */
    private Long speciesId;

    /** 作物名称 */
    @Excel(name = "鱼名称")
    private String fishName;

    /** 作物英文名称 */
    @Excel(name = "鱼种英文名称")
    private String fishEnName;

    /** 鱼种名称 */
    @Excel(name = "鱼种名称")
    private String speciesName;

    /** 鱼种英文名称 */
    @Excel(name = "鱼种英文名称")
    private String speciesEnName;

    /** 鱼种图片 */
    @Excel(name = "鱼种图片")
    private String speciesImg;

    /** 宣传语 */
    @Excel(name = "宣传语")
    private String speciesDes;

    /** 状态 */
    private String status;

    public Long getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(Long speciesId) {
        this.speciesId = speciesId;
    }

    public String getFishName() {
        return fishName;
    }

    public void setFishName(String fishName) {
        this.fishName = fishName;
    }

    public String getFishEnName() {
        return fishEnName;
    }

    public void setFishEnName(String fishEnName) {
        this.fishEnName = fishEnName;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getSpeciesEnName() {
        return speciesEnName;
    }

    public void setSpeciesEnName(String speciesEnName) {
        this.speciesEnName = speciesEnName;
    }

    public String getSpeciesImg() {
        return speciesImg;
    }

    public void setSpeciesImg(String speciesImg) {
        this.speciesImg = speciesImg;
    }

    public String getSpeciesDes() {
        return speciesDes;
    }

    public void setSpeciesDes(String speciesDes) {
        this.speciesDes = speciesDes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    /** 排序 */
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

   

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("speciesId", getSpeciesId())
                .append("fishName", getFishName())
                .append("fishEnName", getFishEnName())
                .append("speciesName", getSpeciesName())
                .append("speciesEnName", getSpeciesEnName())
                .append("speciesImg", getSpeciesImg())
                .append("speciesDes", getSpeciesDes())
                .append("remark", getRemark())
                .append("status", getStatus())
                .append("orderNum", getOrderNum())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
