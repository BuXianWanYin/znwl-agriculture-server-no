package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CityInfoVo {


     private  Long id;

     private String cityName;

     private String address;

     private String position;

     private LocalDateTime createTime;
}
