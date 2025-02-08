package com.frog.agriculture.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 溯源版本对象 agriculture_trace_version
 * 
 * @author nealtsiao
 * @date 2023-08-04
 */
@Data
public class TraceVersion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 溯源版本ID */
    private Long versionId;

    /** 溯源产品 */
    @Excel(name = "溯源产品")
    private Long sellproId;

    /** 溯源版本编号 */
    @Excel(name = "编码范围")
    private String codeRange;

    /** 状态 1可用 2不可用 trace_status */
    private String status;

    /** 溯源码使用数据 */
    private String useDate;

    /** 排序 */
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;
}
