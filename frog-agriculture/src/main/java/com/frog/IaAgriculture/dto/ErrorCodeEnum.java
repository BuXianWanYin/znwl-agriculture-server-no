package com.frog.IaAgriculture.dto;

/**
 * @author Administrator
 */

public enum ErrorCodeEnum {
    SUCCESS(0,"操作成功"),
    ERROR(-1,"操作失败"),

    CONTENT_SERVER_ERROR(100,"合约服务出错"),
    CONTENT_PARSE_ADDR_ERROR(101,"合约地址有误"),

    AUTH_ERROR(403,"认证失败"),

    ERROR_DATA_EXISTS(300,"数据已存在"),

    PARAMTER_ERROR(99,"输入参数错误"),

    SERVICE_ERROR(500,"服务器异常"),
    PARAMETER_ERROR(501,"参数错误"),
    DATA_ALREADY_EXIST(502,"数据已存在"),
    DATA_NOT_EXIST(503,"数据不存在"),
    LIVESTOCK_NOT_OUT_FENCE(504,"牲畜未出栏，无法完成屠宰"),
    LIVESTOCK_NOT_KILLING(505,"牲畜不在屠宰中，无法完成屠宰"),
    PARTITION_NOT_HARVEST(506,"菜未成熟，无法完成加工"),
    LIVESTOCK_NOT_PROCESSING(507,"菜不在加工中，无法完成加工"),

    ;
    private Integer code;
    private String msg;

    ErrorCodeEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
