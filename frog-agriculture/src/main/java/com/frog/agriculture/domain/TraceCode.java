package com.frog.agriculture.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * 溯源码对象 agriculture_trace_code
 * 
 * @author nealtsiao
 * @date 2023-08-04
 */
@Data
public class TraceCode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 溯源码ID */
    private Long codeId;

    /** 溯源产品ID */
    private Long sellproId;

    /** 溯源版本ID */
    private Long versionId;

    /** 溯源码 */
    @Excel(name = "溯源码")
    private String traceCode;

    /** 溯源链接 */
    @Excel(name = "溯源链接")
    private String traceUrl;

    private Integer traceTime;

    /** 状态 1可用 2不可用 trace_status */
    private String status;

    /** 排序 */
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;
}
