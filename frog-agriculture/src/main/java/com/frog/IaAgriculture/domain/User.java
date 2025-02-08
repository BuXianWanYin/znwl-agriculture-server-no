package com.frog.IaAgriculture.domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ Description   :  用户实体类
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/11 18:49
 * @ Version       :  1.0
 */
@Data
@ApiModel(value = "用户信息")
public class User {
    @ApiModelProperty(value = "用户名称", example = "tom", required = true)
    private String username;
    @ApiModelProperty(value = "密码", example = "123", required = true)
    private String password;
    @ApiModelProperty(value = "用户id", example = "123", required = false)
    private String id;
}
