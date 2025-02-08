package com.frog.agriculture.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 溯源模版对象 agriculture_trace_template
 *
 * @author nealtsiao
 * @date 2024-04-14
 */
public class TraceTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模版ID */
    private Long templateId;

    /** 模版名称 */
    @Excel(name = "模版名称")
    private String templateName;

    /** 模版JSON */
    @Excel(name = "模版JSON")
    private String templateJson;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 排序 */
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** $column.columnComment */
    private String baseCoordinate;

    public void setTemplateId(Long templateId)
    {
        this.templateId = templateId;
    }

    public Long getTemplateId()
    {
        return templateId;
    }
    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public String getTemplateName()
    {
        return templateName;
    }
    public void setTemplateJson(String templateJson)
    {
        this.templateJson = templateJson;
    }

    public String getTemplateJson()
    {
        return templateJson;
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
    public void setBaseCoordinate(String baseCoordinate)
    {
        this.baseCoordinate = baseCoordinate;
    }

    public String getBaseCoordinate()
    {
        return baseCoordinate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("templateId", getTemplateId())
                .append("templateName", getTemplateName())
                .append("remark", getRemark())
                .append("templateJson", getTemplateJson())
                .append("status", getStatus())
                .append("orderNum", getOrderNum())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .append("baseCoordinate", getBaseCoordinate())
                .toString();
    }
}