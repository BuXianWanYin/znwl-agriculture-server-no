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

    /** 鱼种名称 */
    @Excel(name = "鱼名称")
    private String fishName;

    /** 鱼种英文名称 */
    @Excel(name = "鱼种英文名称")
    private String fishEnName;

    /** 鱼种类名称 */
    @Excel(name = "鱼种类名称")
    private String fishSpeciesName;

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

    public String getFishSpeciesName() {
        return fishSpeciesName;
    }

    public void setFishSpeciesName(String fishSpeciesName) {
        this.fishSpeciesName = fishSpeciesName;
    }

    public String getFishSpeciesEnName() {
        return fishSpeciesEnName;
    }

    public void setFishSpeciesEnName(String fishSpeciesEnName) {
        this.fishSpeciesEnName = fishSpeciesEnName;
    }

    public String getFishSpeciesImg() {
        return fishSpeciesImg;
    }

    public void setFishSpeciesImg(String fishSpeciesImg) {
        this.fishSpeciesImg = fishSpeciesImg;
    }

    public String getFishSpeciesDes() {
        return fishSpeciesDes;
    }

    public void setFishSpeciesDes(String fishSpeciesDes) {
        this.fishSpeciesDes = fishSpeciesDes;
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

    /** 鱼种类英文名称 */
    @Excel(name = "鱼种英文名称")
    private String fishSpeciesEnName;

    /** 鱼种图片 */
    @Excel(name = "鱼种图片")
    private String fishSpeciesImg;

    /** 宣传语 */
    @Excel(name = "宣传语")
    private String fishSpeciesDes;

    /** 状态 */
    private String status;

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
                .append("fishspeciesName", getFishSpeciesName())
                .append("fishspeciesEnName", getFishSpeciesEnName())
                .append("fishspeciesImg", getFishSpeciesImg())
                .append("fishspeciesDes", getFishSpeciesDes())
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
