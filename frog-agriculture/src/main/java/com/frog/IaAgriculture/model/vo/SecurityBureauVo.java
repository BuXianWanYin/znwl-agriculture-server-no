package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SecurityBureauVo {

    private Long id;

    private String address;

    private Long cityId;

    private String cityName;

    private String cityAddress;

    private String securityBureauName;

    private Long securityBureauPhone;

    private String physicalAddress;

    private LocalDateTime createTime;
}
