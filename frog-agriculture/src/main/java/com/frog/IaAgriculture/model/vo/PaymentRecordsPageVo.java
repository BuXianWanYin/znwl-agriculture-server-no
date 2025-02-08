package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class PaymentRecordsPageVo {

    private Long id;

    private Long pensionId;

    private String pensionAddress;

    private String nickName;

    private Long employerId;

    private String employerAddress;
    private String employerName;

    private Long securityBureauId;

    private String securityBureauAddress;

    private String securityBureauName;

    private BigInteger personalFund;

    private BigInteger overallFund;


    private BigInteger personalAmount;

    private BigInteger overallAmount;

    //创建时间
    private LocalDateTime createTime;


}
