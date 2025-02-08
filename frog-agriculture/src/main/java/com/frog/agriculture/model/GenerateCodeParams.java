package com.frog.agriculture.model;

import lombok.Data;

/**
 * 接受生产溯源码参数的实体类
 * @author neal
 * @date 2024/4/19  08:17
 */
@Data
public class GenerateCodeParams {
    private Long sellproId;
    private String perfix;
    private int number;
    private String url;
}
