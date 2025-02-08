package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.math.BigInteger;

@Data
public class SecurityBureauAccountPageVo {

    private Long id;

    private Long pensionId;

    private BigInteger personalFund;

    private BigInteger overallFund;

    private String nickName;

    private String address;

    private String SecurityBureauName;

    private String SecurityBureauAddress;

    private String employerName;

    private String employerAddress;
}
