package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PensionDetailsVo {


    private Long id;

    private Long userId;

    private String nickName;

    private LocalDate birthday;

    private String idNumber;

    private Long employerId;

    private String address;

    private Long phone;

    private String employerName;

    private LocalDateTime startDate;

    private BigDecimal salary;

    private BigInteger paymentBase;
}
