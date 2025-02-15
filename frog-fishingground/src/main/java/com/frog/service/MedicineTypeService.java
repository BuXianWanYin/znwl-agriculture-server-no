package com.frog.service;

import com.frog.domain.MedicineType;

import java.util.List;

/**
 * 饵料类别Service接口
 *
 * @author aj
 * @date 2025-02-15
 */
public interface MedicineTypeService
{
    /**
     * 查询饵料类别
     *
     * @param MedicineTypeId 饵料类别主键
     * @return 饵料类别
     */
    public MedicineType selectMedicineTypeByMedicineTypeId(Long MedicineTypeId);

    /**
     * 查询饵料类别列表
     *
     * @param MedicineType 饵料类别
     * @return 饵料类别集合
     */
    public List<MedicineType> selectMedicineTypeList(MedicineType MedicineType);

    /**
     * 新增饵料类别
     *
     * @param MedicineType 饵料类别
     * @return 结果
     */
    public int insertMedicineType(MedicineType MedicineType);

    /**
     * 修改饵料类别
     *
     * @param MedicineType 饵料类别
     * @return 结果
     */
    public int updateMedicineType(MedicineType MedicineType);

    /**
     * 批量删除饵料类别
     *
     * @param MedicineTypeIds 需要删除的饵料类别主键集合
     * @return 结果
     */
    public int deleteMedicineTypeByMedicineTypeIds(Long[] MedicineTypeIds);

    /**
     * 删除饵料类别信息
     *
     * @param MedicineTypeId 饵料类别主键
     * @return 结果
     */
    public int deleteMedicineTypeByMedicineTypeId(Long MedicineTypeId);
}
