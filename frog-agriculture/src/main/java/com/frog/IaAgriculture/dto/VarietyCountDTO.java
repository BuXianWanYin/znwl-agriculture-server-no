package com.frog.IaAgriculture.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ Description   :  种类数量查询返回实体
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/13 14:03
 * @ Version       :  1.0
 */
@Data
@ApiModel(value = "VarietyCountDTO", description = "种类统计数据传输对象")
public class VarietyCountDTO {

    @ApiModelProperty(value = "种类", example = "菜")
    private String variety;

    @ApiModelProperty(value = "该种类的数量", example = "100")
    private Long count;
}