package com.frog.IaAgriculture.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 自定义异常处理器
 */

@RestControllerAdvice
@Slf4j
@Component("customGlobalExceptionHandler")  // 自定义Bean名称
public class GlobalExceptionHandler {


    @ExceptionHandler(ServerException.class)
    public ResultVO serverException(ServerException e) {
        return ResultVO.failed(e.getMessage());
    }


    /**
     * 请求参数异常
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultVO validationException(MethodArgumentNotValidException e) {
        return createErrorResultVO(e.getBindingResult().getAllErrors());
    }
    private ResultVO createErrorResultVO(List<ObjectError> allErrors) {
        // 错误信息
        StringBuilder message = new StringBuilder();

        if (!CollectionUtils.isEmpty(allErrors)) {
            allErrors.forEach(error -> {
                message.append(" -- ");
                message.append(error.getDefaultMessage());
            });
        }
        log.info("客户端请求参数错误: {}", message.toString().substring(4));
        return new ResultVO(ErrorCodeEnum.PARAMTER_ERROR.getCode(), message.toString().substring(4));
    }
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultVO constraintViolationException(ConstraintViolationException exception) {
        log.error("客户端请求参数错误: {}", exception.getMessage());

        StringBuilder msg = new StringBuilder();
        for (ConstraintViolation cv : exception.getConstraintViolations()) {
            msg.append(" - ").append(cv.getMessage());
        }

        return new ResultVO(ErrorCodeEnum.PARAMTER_ERROR.getCode(), msg.toString().substring(3));
    }
    @ExceptionHandler(Exception.class)
    public ResultVO exceptionAll(Exception e) {
        System.out.println(e.getClass());
        log.error(e.getMessage());
        return ResultVO.failed(ErrorCodeEnum.SERVICE_ERROR);
    }
}
