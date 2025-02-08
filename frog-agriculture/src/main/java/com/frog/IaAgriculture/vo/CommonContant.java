package com.frog.IaAgriculture.vo;

/**
 * 常量池
 */
public class CommonContant {

    /**
     * 分页默认查询当前页
     */
    public static final Integer DEFAULT_CURRENT_PAGE = 1;

    /**
     * 分页默认条数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 15;

    /**
     * 合约返回
     */
    public static final String SUCCESS_MESSAGE = "Success";

    /**
     * 牛未出栏
     */
    public static final Integer LIVESTOCK_STATE_IN_FENCE = 0;

    /**
     * 牛已出栏
     */
    public static final Integer LIVESTOCK_STATE_OUT_FENCE = 1;

    /**
     * 牛未屠宰
     */
    public static final Integer LIVESTOCK_STATE_NOT_KILL = 0;

    /**
     * 牛屠宰中
     */
    public static final Integer LIVESTOCK_STATE_KILLING = 1;

    /**
     * 牛已屠宰
     */
    public static final Integer LIVESTOCK_STATE_KILLED = 2;
}
