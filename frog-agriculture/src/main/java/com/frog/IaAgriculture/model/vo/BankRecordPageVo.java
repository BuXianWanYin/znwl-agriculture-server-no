package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BankRecordPageVo {

    private Long id;

    private Long pensionId;

    private String pensionAddress;
    private String idNumber;
    private String nickName;

    private Long cityId;
    private String cityAddress;
    private String cityName;

    private Long securityBureauId;
    private String securityBureauAddress;
    private String securityBureauName;

    private LocalDateTime createTime;

}
