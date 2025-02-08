package com.frog.IaAgriculture.exception;


import lombok.Data;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;

/**
 * 自定义一个异常
 */
@Data
public class ServerException extends RuntimeException{
    private ErrorCodeEnum errorCodeEnum;
    public ServerException(String msg){
        super(msg);
    }

    public ServerException(ErrorCodeEnum errorCodeEnum){
        super(errorCodeEnum.getMsg());
        this.errorCodeEnum = errorCodeEnum;
    }
}
