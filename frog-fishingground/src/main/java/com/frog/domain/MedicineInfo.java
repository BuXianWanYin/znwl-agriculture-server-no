package com.frog.domain;

import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 药品信息对象 fish_medicine_info
 *
 * @author AJ
 * @date 2024-02-15
 */
public class MedicineInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 药品ID */
    private Long medicineId;

    /** 药品编码 */
    @Excel(name = "药品编码")
    private String medicineCode;

    /** 药品名称 */
    @Excel(name = "药品名称")
    private String medicineName;

    /** 药品类别 */
    @Excel(name = "药品类别")
    private Long medicineTypeId;

    /** 药品单位 */
    @Excel(name = "计量单位")
    private String measureUnit;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 排序 */
    @Excel(name = "排序")
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public Long getMedicineTypeId() {
        return medicineTypeId;
    }

    public void setMedicineTypeId(Long medicineTypeId) {
        this.medicineTypeId = medicineTypeId;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
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
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("medicineId", getMedicineId())
            .append("medicineCode", getMedicineCode())
            .append("medicineName", getMedicineName())
            .append("medicineTypeId", getMedicineTypeId())
            .append("measureUnit", getMeasureUnit())
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
