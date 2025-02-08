package com.frog.agriculture.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;

/**
 * 作物批次对象 agriculture_crop_batch
 * 
 * @author nealtsiao
 * @date 2023-05-13
 */
@Data
public class CropBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 批次ID */
    private Long batchId;

    /** 批次名称 */
    @Excel(name = "批次名称")
    private String batchName;
    @Excel(name = "品种")
    private String variety;
    @Excel(name = "合约地址")
    private String contractAddress;

    /** 种质ID */
    @Excel(name = "种质ID")
    private Long germplasmId;

    /** 地块ID */
    @Excel(name = "地块ID")
    private Long landId;

    /** 种植面积（亩） */
    @Excel(name = "种植面积", readConverterExp = "亩=")
    private BigDecimal cropArea;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 负责人 */
    @Excel(name = "负责人")
    private Long batchHead;

    /** 状态 */
    private String status;

    /** 排序 */
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    private String germplasmImg;

}
