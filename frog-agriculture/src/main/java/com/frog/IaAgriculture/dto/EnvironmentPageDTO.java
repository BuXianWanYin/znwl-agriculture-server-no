package com.frog.IaAgriculture.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "溯源环境分页查询")
@Data
public class EnvironmentPageDTO extends BaseDTO {
    @ApiModelProperty(value = "溯源码/溯源id为空", example = "123", required = true)
    @NotBlank(message = "溯源码/溯源id为空")
    private String iaPartitionFoodId;
}
