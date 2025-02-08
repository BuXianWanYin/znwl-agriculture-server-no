package com.frog.IaAgriculture.model.vo;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserInfoVo  {


    private Long id;

    //登录名称
    private String name;

    private String address;
    //图片
    private String cover;

    //角色类型
    private String roleType;


    private Long phone;

    private String position;

    //创建时间
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;

    //雇主信息

    private EmployerInfoVo employer;

    //职工信息
    private PensionVo pension;

    private BankInfoVo bank;
}
