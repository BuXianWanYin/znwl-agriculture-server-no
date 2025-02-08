package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BankInfoVo {

    private Long id;

    private Long userId;
    //手机号
    private Long phone;
    //地址
    private String position;
    //账户地址
    private String address;
    //银行名称
    private String bankName;

    private String securityBureauId;
    //社保局名称
    private String securityBureauName;
    //社保局地址
    private String securityBureauAddress;
    //城市地址
    private String cityAddress;
    //城市名称
    private String cityName;

    private LocalDateTime createTime;

}
