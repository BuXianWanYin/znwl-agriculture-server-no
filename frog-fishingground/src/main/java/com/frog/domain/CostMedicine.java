package com.frog.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 药品用量对象 agriculture_cost_medicine
 *
 * @author xuweidong
 * @date 2023-05-24
 */
public class CostMedicine extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long costId;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 药品ID */
    @Excel(name = "药品ID")
    private Long medicineId;

    /** 使用数量 */
    @Excel(name = "使用数量")
    private Long medicineCount;

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

    public void setCostId(Long costId)
    {
        this.costId = costId;
    }

    public Long getCostId()
    {
        return costId;
    }
    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public Long getTaskId()
    {
        return taskId;
    }
    public void setMedicineId(Long medicineId)
    {
        this.medicineId = medicineId;
    }

    public Long getMedicineId()
    {
        return medicineId;
    }
    public void setMedicineCount(Long medicineCount)
    {
        this.medicineCount = medicineCount;
    }

    public Long getMedicineCount()
    {
        return medicineCount;
    }
    public void setMeasureUnit(String measureUnit)
    {
        this.measureUnit = measureUnit;
    }

    public String getMeasureUnit()
    {
        return measureUnit;
    }
    public void setWorkingStart(Date workingStart)
    {
        this.workingStart = workingStart;
    }

    public Date getWorkingStart()
    {
        return workingStart;
    }
    public void setWorkingFinish(Date workingFinish)
    {
        this.workingFinish = workingFinish;
    }

    public Date getWorkingFinish()
    {
        return workingFinish;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setOrderNum(Long orderNum)
    {
        this.orderNum = orderNum;
    }

    public Long getOrderNum()
    {
        return orderNum;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("costId", getCostId())
            .append("taskId", getTaskId())
            .append("medicineId", getMedicineId())
            .append("medicineCount", getMedicineCount())
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
