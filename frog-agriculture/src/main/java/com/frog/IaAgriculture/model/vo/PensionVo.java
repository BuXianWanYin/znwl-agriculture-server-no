package com.frog.IaAgriculture.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PensionVo {


    private Long id;

    private Long userId;

    private String address;

    private Long phone;

    private String position;

    private String nickName;

    private LocalDate birthday;

    private String idNumber;

    private Long employerId;

    private String employerAddress;

    private String employerName;

    private LocalDateTime startDate;

    private BigDecimal salary;

    private Long paymentBase;

    private LocalDateTime createTime;

}
