package com.frog.IaAgriculture.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @ Description   :  月度返回
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/13 14:36
 * @ Version       :  1.0
 */
@Data
@ApiModel(value = "MonthlyProcessCountDTO", description = "月度返回")
public class MonthlyProcessCountDTO {
    private int month;
    private int count;
}