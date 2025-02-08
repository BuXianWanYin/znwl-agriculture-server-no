package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class PensionPageVo {


    private Long id;

    private Long userId;

    private String cityName;

    private String cityAddress;

    private String securityBureauAddress;

    private String securityBureauName;

    private String nickName;

    private String idNumber;

    private BigInteger personalFund;

    private BigInteger salary;

    private BigInteger overallFund;

    private String address;

    private Long phone;

    private String position;

    //创建时间
    private LocalDateTime createTime;


}
