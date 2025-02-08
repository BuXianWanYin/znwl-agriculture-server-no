package com.frog.IaAgriculture.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "分页分区食品查询")
@Data
public class IaPartitionFoodPageDTO extends BaseDTO {
    @ApiModelProperty(value = "分区id", example = "2341", required = true)
    private String partitionId;
}
