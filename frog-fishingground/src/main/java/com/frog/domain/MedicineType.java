package com.frog.domain;

import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 药品类别对象 fish_medicine_type
 *
 * @author aj
 * @date 2024-02-15
 */
public class MedicineType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 药品类别ID
     */
    private Long medicineTypeId;

    /**
     * 药品类别名称
     */
    @Excel(name = "饵料类别名称")
    private String medicineTypeName;

    /**
     * 状态
     */
    private String status;

    /**
     * 排序
     */
    @Excel(name = "排序")
    private Long orderNum;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    public Long getMedicineTypeId() {
        return medicineTypeId;
    }

    public void setMedicineTypeId(Long medicineTypeId) {
        this.medicineTypeId = medicineTypeId;
    }

    public String getMedicineTypeName() {
        return medicineTypeName;
    }

    public void setMedicineTypeName(String medicineTypeName) {
        this.medicineTypeName = medicineTypeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("medicineTypeId", getMedicineTypeId())
                .append("medicineTypeName", getMedicineTypeName())
                .append("remark", getRemark())
                .append("status", getStatus())
                .append("orderNum", getOrderNum())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}
