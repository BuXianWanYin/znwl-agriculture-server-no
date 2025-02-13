package com.frog.dto;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.IaAgriculture.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "获取鱼棚状态信息")
@Data
public class FishPastureSensorValuePageDTO extends BaseDTO {
    @ApiModelProperty(value = "鱼棚id", example = "123", required = true)
    @NotBlank(message = "鱼棚id为空")
    private String pastureId;
}
