package com.frog.domain;

import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 饵料信息对象 fish_bait_info
 * 
 * @author AJ
 * @date 2024-02-15
 */
public class BaitInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 饵料ID */
    private Long baitId;

    /** 饵料编码 */
    @Excel(name = "饵料编码")
    private String baitCode;

    /** 饵料名称 */
    @Excel(name = "饵料名称")
    private String baitName;

    /** 饵料类别 */
    @Excel(name = "饵料类别")
    private Long baitTypeId;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String measureUnit;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getBaitId() {
        return baitId;
    }

    public void setBaitId(Long baitId) {
        this.baitId = baitId;
    }

    public String getBaitCode() {
        return baitCode;
    }

    public void setBaitCode(String baitCode) {
        this.baitCode = baitCode;
    }

    public String getBaitName() {
        return baitName;
    }

    public void setBaitName(String baitName) {
        this.baitName = baitName;
    }

    public Long getBaitTypeId() {
        return baitTypeId;
    }

    public void setBaitTypeId(Long baitTypeId) {
        this.baitTypeId = baitTypeId;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("baitId", getBaitId())
            .append("baitCode", getBaitCode())
            .append("baitName", getBaitName())
            .append("baitTypeId", getBaitTypeId())
            .append("measureUnit", getMeasureUnit())
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
