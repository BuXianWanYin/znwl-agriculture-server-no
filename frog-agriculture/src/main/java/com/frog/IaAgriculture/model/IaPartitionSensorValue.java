package com.frog.IaAgriculture.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 分区每一天的环境信息
 */
@ApiModel(value = "分区每一天的环境信息")
@Data
public class IaPartitionSensorValue implements Serializable {
    /**
     * id
     */
    @ApiModelProperty(value = "id", example = "1")
    private String id;
    /**
     * 分区id
     */
    @ApiModelProperty(value = "分区id", example = "1")
    private String iaPartitionId;
    /**
     * 大棚id
     */
    @ApiModelProperty(value = "大棚id", example = "1")
    private String pastureId;
    /**
     * 温度
     */
    @ApiModelProperty(value = "温度")
    private BigDecimal temperature;
    /**
     * 湿度
     */
    @ApiModelProperty(value = "湿度")
    private BigDecimal humidity;
    /**
     * 空气质量
     */
    @ApiModelProperty(value = "空气质量")
    private BigDecimal airquality;
    /**
     * 大气压强
     */
    @ApiModelProperty(value = "大气压强")
    private BigDecimal pressure;
    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private Date date;

    @TableField(exist = false)
    private String contractAddr;
}
