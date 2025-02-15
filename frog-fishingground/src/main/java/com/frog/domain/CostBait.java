package com.frog.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 饵料用量对象 fish_cost_bait
 *
 * @author aj
 * @date 2024-02-15
 */
public class CostBait extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long costId;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 饵料ID */
    @Excel(name = "饵料ID")
    private Long baitId;

    /** 使用数量 */
    @Excel(name = "使用数量")
    private Long baitCount;

    /** 计量单位 */
    @Excel(name = "计量单位")
    private String measureUnit;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date workingStart;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date workingFinish;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getBaitId() {
        return baitId;
    }

    public void setBaitId(Long baitId) {
        this.baitId = baitId;
    }

    public Long getBaitCount() {
        return baitCount;
    }

    public void setBaitCount(Long baitCount) {
        this.baitCount = baitCount;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public Date getWorkingStart() {
        return workingStart;
    }

    public void setWorkingStart(Date workingStart) {
        this.workingStart = workingStart;
    }

    public Date getWorkingFinish() {
        return workingFinish;
    }

    public void setWorkingFinish(Date workingFinish) {
        this.workingFinish = workingFinish;
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
                .append("costId", getCostId())
                .append("taskId", getTaskId())
                .append("baitId", getBaitId())
                .append("baitCount", getBaitCount())
                .append("measureUnit", getMeasureUnit())
                .append("workingStart", getWorkingStart())
                .append("workingFinish", getWorkingFinish())
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
