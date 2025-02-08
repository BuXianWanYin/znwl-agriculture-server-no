package com.frog.agriculture.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 预警信息对象 warning_info
 * 
 * @author 何奎
 * @date 2024-10-20
 */
public class WarningInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预警信息ID */
    private Long id;

    /** 预警大棚 */
    @Excel(name = "预警大棚")
    private String greenhouse;

    /** 预警区域 */
    @Excel(name = "预警区域")
    private String partitionInfo;

    /** 预警对象批次 */
    @Excel(name = "预警对象批次")
    private String cropBatch;

    /** 区域责任人 */
    @Excel(name = "区域责任人")
    private String responsiblePerson;

    /** 预警详情 */
    @Excel(name = "预警详情")
    private String thresholdValue;

    /** 预警当前状态 */
    @Excel(name = "预警当前状态")
    private String warningStatus;

    /** 上链合约地址 */
    @Excel(name = "上链合约地址")
    private String handler;

    /** 预警触发时间 */

    private Date startTime;

    /** 预警解除时间 */

    private Date endTime;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;

    /** 软删除 */
    private Long delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setGreenhouse(String greenhouse) 
    {
        this.greenhouse = greenhouse;
    }

    public String getGreenhouse() 
    {
        return greenhouse;
    }
    public void setPartitionInfo(String partitionInfo) 
    {
        this.partitionInfo = partitionInfo;
    }

    public String getPartitionInfo() 
    {
        return partitionInfo;
    }
    public void setCropBatch(String cropBatch) 
    {
        this.cropBatch = cropBatch;
    }

    public String getCropBatch() 
    {
        return cropBatch;
    }
    public void setResponsiblePerson(String responsiblePerson) 
    {
        this.responsiblePerson = responsiblePerson;
    }

    public String getResponsiblePerson() 
    {
        return responsiblePerson;
    }
    public void setThresholdValue(String thresholdValue) 
    {
        this.thresholdValue = thresholdValue;
    }

    public String getThresholdValue() 
    {
        return thresholdValue;
    }
    public void setWarningStatus(String warningStatus) 
    {
        this.warningStatus = warningStatus;
    }

    public String getWarningStatus() 
    {
        return warningStatus;
    }
    public void setHandler(String handler) 
    {
        this.handler = handler;
    }

    public String getHandler() 
    {
        return handler;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }
    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }
    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("greenhouse", getGreenhouse())
            .append("partitionInfo", getPartitionInfo())
            .append("cropBatch", getCropBatch())
            .append("responsiblePerson", getResponsiblePerson())
            .append("thresholdValue", getThresholdValue())
            .append("warningStatus", getWarningStatus())
            .append("handler", getHandler())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
