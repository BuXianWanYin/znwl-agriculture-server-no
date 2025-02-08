package com.frog.agriculture.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 溯源产品对象 agriculture_trace_sellpro
 * 
 * @author nealtsiao
 * @date 2023-08-07
 */
@Data
public class TraceSellpro extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 产品ID */
    private Long sellproId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String sellproName;

    /** 产地 */
    @Excel(name = "产地")
    private String sellproArea;

    /** 产品重量 */
    @Excel(name = "产品重量")
    private String sellproWeight;

    /** 产品规格 */
    @Excel(name = "产品规格")
    private String sellproGuige;

    /** 产品图片 */
    @Excel(name = "产品图片")
    private String sellproImg;

    /** 溯源页面模版ID */
    private Long templateId;

    /** 溯源码模版ID */
    private Long codeTemplateId;

    /** 状态 1可用 2不可用 trace_status */
    @Excel(name = "状态 1可用 2不可用 trace_status")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;
}
