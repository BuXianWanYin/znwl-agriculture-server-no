package com.frog.IaAgriculture.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.frog.IaAgriculture.vo.CommonContant;

import java.io.Serializable;

/**
 * 基础支撑base

 */
@ApiModel(value = "基础支撑base")
@Data
public class BaseDTO implements Serializable {


    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", example = "1", required = false)
    private Integer currentPage;

    /**
     * 获取条数
     */
    @ApiModelProperty(value = "当前页条数", example = "15",required = false)
    private Integer pageSize;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字搜索", example = "1",required = false)
    private String keyword;
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", example = "2020-10-10",required = false)
    private String startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", example = "2020-10-10",required = false)
    private String endTime;

    public Integer getPageSize() {
        return this.pageSize == null ? CommonContant.DEFAULT_PAGE_SIZE : this.pageSize;
    }

    public Integer getCurrentPage() {
        return this.currentPage == null ? CommonContant.DEFAULT_CURRENT_PAGE : this.currentPage;
    }
}
