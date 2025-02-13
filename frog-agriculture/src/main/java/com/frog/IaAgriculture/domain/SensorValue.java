package com.frog.IaAgriculture.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SensorValue {


        /**
         * id
         */
        private String id;

        /**
         * 设备id
         */
        private String deviceId;
        /**
         * 农场id，-1为没有绑定牧场
         */
        private String pastureId;
        /**
         * 温度
         */
        private String temp;
        /**
         * 湿度
         */
        private String hum;
        /**
         * 光照
         */
        private String lux;
        /**
         * 时间
         */
        private String time;
        /**
         * 日期
         */
        private String date;
        /**
         * 火焰
         */
        private String fire;
        /**
         * 土壤
         */
        private String soil;
        /**
         * 空气
         */
        private String air;
        /**
         *
         */
        private String relay;

        /**
         * 大气压
         */
        private String p;

        /**
         * 温度
         */
        private BigDecimal temperature;
        /**
         * 湿度
         */
        private BigDecimal humidity;
        /**
         * 空气质量
         */
        private BigDecimal airquality;
        /**
         * 大气压强
         */
        private BigDecimal pressure;

        private LocalDateTime dateTime;
        

}
