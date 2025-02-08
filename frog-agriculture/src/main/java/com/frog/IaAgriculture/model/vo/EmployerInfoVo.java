package com.frog.IaAgriculture.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployerInfoVo {

    private Long id;

    private Long userId;

    private Long phone;

    private String position;

    private String address;

    private String employerName;



    private String securityBureauId;

    private String securityBureauName;

    private String securityBureauAddress;


    private String cityId;

    private String cityAddress;

    private String cityName;

    private BigDecimal balance;

}
