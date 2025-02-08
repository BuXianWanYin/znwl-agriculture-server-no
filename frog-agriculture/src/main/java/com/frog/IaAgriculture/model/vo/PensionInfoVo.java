package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.time.LocalDate;

@Data
public class PensionInfoVo {

    private Long id;

    private Long userId;

    private String nickName;

    private String address;

    private String idNumber;

    private Long phone;

    private Long employerId;

    private LocalDate birthday;

    private SecurityBureauAccountInfoVo securityBureauAccount;

    private EmployerInfoVo employer;

    private CityInfoVo city;
}
