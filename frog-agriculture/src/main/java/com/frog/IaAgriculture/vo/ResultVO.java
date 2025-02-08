package com.frog.IaAgriculture.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应对象
 *
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = -993309571681498675L;

    //响应数据
    private T data;

    //响应状态码
    private Integer code;

    //响应消息
    private String msg;

    @JsonIgnore
    public boolean checkIs200() {
        return null == code ? false : code.intValue() == ErrorCodeEnum.SUCCESS.getCode().intValue();
    }

    /**
     * 操作成功，返回默认响应 code 0
     *
     * @param <T>
     * @return
     */
    public static <T> ResultVO<T> succeed() {
        return succeedWith(null, ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMsg());
    }

    /**
     * 操作失败，返回默认响应 code -1
     *
     * @param <T>
     * @return
     */
    public static <T> ResultVO<T> failed() {
        return failedWith(null, ErrorCodeEnum.ERROR.getCode(), ErrorCodeEnum.ERROR.getMsg());
    }

    public ResultVO(ErrorCodeEnum codeEnum) {
        this.data = null;
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }

    public ResultVO(Integer code, String meg) {
        this.data = null;
        this.code = code;
        this.msg = meg;
    }

    public ResultVO(ErrorCodeEnum codeEnum, T result) {
        this.data = result;
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }

    public static <T> ResultVO<T> succeed(String msg) {
        return succeedWith(null, ErrorCodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> ResultVO<T> succeed(T model, String msg) {
        return succeedWith(model, ErrorCodeEnum.SUCCESS.getCode(), msg);
    }

    public static <T> ResultVO<T> succeed(T data) {
        return succeedWith(data, ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMsg());
    }

    public static <T> ResultVO<T> succeed(Integer code, String msg) {
        return new ResultVO<T>(null, code, msg);
    }

    public static <T> ResultVO<T> succeedWith(T data, Integer code, String msg) {
        return new ResultVO<T>(data, code, msg);
    }

    public static <T> ResultVO<T> failed(String msg) {
        return failedWith(null, ErrorCodeEnum.ERROR.getCode(), msg);
    }

    public static <T> ResultVO<T> failed(T data, String msg) {
        return failedWith(data, ErrorCodeEnum.ERROR.getCode(), msg);
    }

    /**
     * 操作失败，传入CodeEnum
     *
     * @param <T>
     * @return
     */
    public static <T> ResultVO<T> failed(ErrorCodeEnum codeEnum) {
        return new ResultVO<T>(null, codeEnum.getCode(), codeEnum.getMsg());
    }

    public static <T> ResultVO<T> failedWith(T data, Integer code, String msg) {
        return new ResultVO<T>(data, code, msg);
    }

    /**
     * 返回分页信息
     *
     * @param iPage
     * @param <T>
     * @return
     */
    public static <T> ResultVO succeedPage(IPage<T> iPage) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("list", iPage.getRecords());
        map.put("total", iPage.getTotal());
        ResultVO resultVo = new ResultVO(ErrorCodeEnum.SUCCESS, map);
        return resultVo;
    }

}
