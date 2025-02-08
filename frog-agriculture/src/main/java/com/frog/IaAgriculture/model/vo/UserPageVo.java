package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPageVo {


    private Long id;

    //登录名称
    private String name;

    private String address;

    private String position;

    //审核状态
    private String verifyStatus;

    //角色类型
    private String roleType;

    //创建时间
    private LocalDateTime createTime;

}
