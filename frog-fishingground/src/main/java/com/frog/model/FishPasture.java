package com.frog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.frog.IaAgriculture.domain.Device;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ApiModel(value = "鱼棚实体类", description = "代表鱼棚信息")
@TableName("fish_pasture")
@Data
public class FishPasture implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(value = "id", example = "202310010000000001", required = true)
    private String id;

    @ApiModelProperty(value = "名称", example = "清水鱼棚", required = true)
    private String name;

    @ApiModelProperty(value = "合约地址", example = "0x1234567890abcdef1234567890abcdef12345678", required = false)
    private String contractAddr;

    @ApiModelProperty(value = "养殖棚位置", example = "江苏省某地养殖区", required = false)
    private String address;

    @ApiModelProperty(value = "ph值（1-14）", example = "7.5", required = false)
    private String phValue;

    @ApiModelProperty(value = "含氧量mg/L", example = "6.5", required = false)
    private String dissolvedOxygen;

    @ApiModelProperty(value = "亚硝酸盐", example = "0.1", required = false)
    private String nitriteNitrogen;

    @ApiModelProperty(value = "大棚面积", example = "500平方米", required = false)
    private String area;

    @ApiModelProperty(value = "最大分区数量", example = "123123", required = true)
    @NotNull(message = "最大分区数量为空")
    private Integer bigBreedingQuantity;

    @ApiModelProperty(value = "当前分区数量", example = "123123")
    private Long breedingQuantity;

    //备注
    @ApiModelProperty(value = "备注", example = "test")
    @NotBlank(message = "备注为空")
    private String description;


    @ApiModelProperty(value = "设备id", example = "test", required = true)
    @NotBlank(message = "设备id")
    @TableField(exist = false)
    private String deviceId;

    @ApiModelProperty(value = "设备列表")
    @TableField(exist = false)
    private List<Device> devices;
}
