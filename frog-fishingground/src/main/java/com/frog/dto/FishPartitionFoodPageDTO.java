package com.frog.dto;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.IaAgriculture.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "鱼的分页分区食品查询")
@Data
public class FishPartitionFoodPageDTO extends BaseDTO {
    @ApiModelProperty(value = "分区id", example = "2341", required = true)
    private String partitionId;
}
