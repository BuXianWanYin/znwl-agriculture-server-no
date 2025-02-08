package com.frog.agriculture.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 种植单元对象 agriculture_unit
 * 
 * @author nealtsiao
 * @date 2024-07-26
 */
public class Unit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 地块id */
    @Excel(name = "地块id")
    private Long landId;

    /** 单元要素类型：1设备 2监控 3种植批次 4自动化场景  其他类型可自由拓展 */
    @Excel(name = "单元要素类型：1设备 2监控 3种植批次 4自动化场景  其他类型可自由拓展")
    private Integer type;

    /** 要素id */
    @Excel(name = "要素id")
    private Long elementId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setLandId(Long landId) 
    {
        this.landId = landId;
    }

    public Long getLandId() 
    {
        return landId;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }
    public void setElementId(Long elementId) 
    {
        this.elementId = elementId;
    }

    public Long getElementId() 
    {
        return elementId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("landId", getLandId())
            .append("type", getType())
            .append("elementId", getElementId())
            .toString();
    }
}
