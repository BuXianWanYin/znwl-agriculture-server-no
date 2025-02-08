package com.frog.IaAgriculture.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "获取大棚状态信息")
@Data
public class IaPastureSensorValuePageDTO extends BaseDTO {
    @ApiModelProperty(value = "大棚id", example = "123", required = true)
    @NotBlank(message = "大棚id为空")
    private String pastureId;
}
