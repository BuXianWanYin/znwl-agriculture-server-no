package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class SecurityBureauAccountInfoVo {

    private Long id;

    private BigInteger personalFund;

    private BigInteger overallFund;

    private String securityBureauName;

    private String securityBureauAddress;

    private LocalDateTime paymentTimeStamp;
}
