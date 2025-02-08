package com.frog.agriculture.domain;

import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 溯源码模版对象 agriculture_trace_code_template
 * 
 * @author nealtsiao
 * @date 2024-04-02
 */
@Data
public class TraceCodeTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模版ID */
    private Long templateId;
    /** 模版名称 */
    private String templateName;

    /** 模版背景图 */
    @Excel(name = "模版背景图")
    private String templateBgi;

    /** 模版宽度(PX) */
    private Integer templateWidth;

    /** 模版高度(PX) */
    private Integer templateHeight;

    /** 二维码宽度(PX) */
    private Integer qrcodeWidth;

    /** 二维码高度(PX) */
    private Integer qrcodeHeight;

    /** 二维码横坐标位置(PX) */
    private Integer qrcodeX;

    /** 二维码纵坐标位置(PX) */
    private Integer qrcodeY;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 排序 */
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** $column.columnComment */
    private String baseCoordinate;
}
