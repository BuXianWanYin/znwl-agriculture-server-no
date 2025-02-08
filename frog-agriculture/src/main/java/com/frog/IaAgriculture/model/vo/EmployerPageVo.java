package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EmployerPageVo {

    private Long id;

    private String employerName;


    private BigDecimal balance;

    private String address;

    private String position;

    private Long phone;

    private String securityBureauName;

    private String securityBureauAddress;

    private LocalDateTime createTime;


}
