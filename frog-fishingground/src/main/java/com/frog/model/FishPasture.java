package com.frog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.frog.IaAgriculture.domain.Device;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ApiModel(value = "大棚")
@Data
public class FishPasture implements Serializable {
    private String id;
    //大棚名称
    @ApiModelProperty(value = "大棚名称", example = "伊利", required = true)
    @NotBlank(message = "大棚名称为空")
    private String name;
    @ApiModelProperty(hidden = true)
    //合约地址
    private String contractAddr;

    //当前分区数量
    @ApiModelProperty(value = "当前分区数量", example = "123123")
    private Long breedingQuantity;
    //大棚位置
    @ApiModelProperty(value = "大棚位置", example = "新疆", required = true)
    @NotBlank(message = "大棚位置为空")
    private String address;
    //备注
    @ApiModelProperty(value = "备注", example = "test")
    @NotBlank(message = "备注为空")
    private String description;
    //大棚面积
    @ApiModelProperty(value = "大棚面积", example = "1234")
    @NotBlank(message = "大棚面积为空")
    private String area;
    //最大养殖数
    @ApiModelProperty(value = "最大分区数量", example = "123123", required = true)
    @NotNull(message = "最大分区数量为空")
    private Integer bigBreedingQuantity;
    @ApiModelProperty(value = "设备id", example = "test", required = true)
    @NotBlank(message = "设备id")
    @TableField(exist = false)
    private String deviceId;
    @ApiModelProperty(value = "温度")
    @TableField(exist = false)
    private BigDecimal temperature;
    /**
     * 湿度
     */
    @ApiModelProperty(value = "湿度")
    @TableField(exist = false)
    private BigDecimal humidity;
    /**
     * 空气质量
     */
    @ApiModelProperty(value = "空气质量")
    @TableField(exist = false)
    private BigDecimal airquality;
    /**
     * 大气压强
     */
    @ApiModelProperty(value = "大气压强")
    @TableField(exist = false)
    private BigDecimal pressure;
    @ApiModelProperty(value = "设备列表")
    @TableField(exist = false)
    private List<Device> devices;
}
