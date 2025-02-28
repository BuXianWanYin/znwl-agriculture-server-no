package com.frog.IaAgriculture.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 溯源码查询记录表
 */
@TableName(value = "ia_partition_trace_record")
public class AgricultureTraceRecord {

    /**
     * 记录ID
     */
    @Id
    @TableField(value = "id")  // 指定数据库列名
    private Long id;

    /**
     * 溯源产品名
     */
    private String name;

    /**
     * 鱼或者菜 0 1
     */
    private Long type;

    /**
     * 溯源码
     */
    private String traceCode;

    /**
     * 查询时间
     */
    private Date queryDate;

    /**
     * 查询地点
     */
    private String queryAddress;

    /**
     * 查询坐标
     */
    private String queryCoordinate;

    /**
     * 查询城市
     */
    private String queryCity;

    /**
     * 城市代码
     */
    private String cityCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private String status;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 创建者ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人ID
     */
    private Long updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getTraceCode() {
        return traceCode;
    }

    public void setTraceCode(String traceCode) {
        this.traceCode = traceCode;
    }

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }

    public String getQueryAddress() {
        return queryAddress;
    }

    public void setQueryAddress(String queryAddress) {
        this.queryAddress = queryAddress;
    }

    public String getQueryCoordinate() {
        return queryCoordinate;
    }

    public void setQueryCoordinate(String queryCoordinate) {
        this.queryCoordinate = queryCoordinate;
    }

    public String getQueryCity() {
        return queryCity;
    }

    public void setQueryCity(String queryCity) {
        this.queryCity = queryCity;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
