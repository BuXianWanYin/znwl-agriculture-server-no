package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class CityVo {

    private Long id;
    //城市地址
    private String address;
    //城市名称
    private String cityName;
    //电话
    private Long  phone;
    //地址
    private String position;
    //最大保险基数
    private BigInteger maxInsuranceBase;
    //最小保险基数
    private BigInteger minInsuranceBase;
    //个人缴费率
    private BigInteger personalRate;
    //统筹缴费率
    private BigInteger overAllRate;

    private LocalDateTime createTime;

}
