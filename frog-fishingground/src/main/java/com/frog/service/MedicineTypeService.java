package com.frog.service;

import com.frog.domain.MedicineType;

import java.util.List;

/**
 * 药品类别Service接口
 *
 * @author aj
 * @date 2025-02-15
 */
public interface MedicineTypeService
{
    /**
     * 查询药品类别
     *
     * @param MedicineTypeId 药品类别主键
     * @return 药品类别
     */
    public MedicineType selectMedicineTypeByMedicineTypeId(Long MedicineTypeId);

    /**
     * 查询药品类别列表
     *
     * @param MedicineType 药品类别
     * @return 药品类别集合
     */
    public List<MedicineType> selectMedicineTypeList(MedicineType MedicineType);

    /**
     * 新增药品类别
     *
     * @param MedicineType 药品类别
     * @return 结果
     */
    public int insertMedicineType(MedicineType MedicineType);

    /**
     * 修改药品类别
     *
     * @param MedicineType 药品类别
     * @return 结果
     */
    public int updateMedicineType(MedicineType MedicineType);

    /**
     * 批量删除药品类别
     *
     * @param MedicineTypeIds 需要删除的药品类别主键集合
     * @return 结果
     */
    public int deleteMedicineTypeByMedicineTypeIds(Long[] MedicineTypeIds);

    /**
     * 删除药品类别信息
     *
     * @param MedicineTypeId 药品类别主键
     * @return 结果
     */
    public int deleteMedicineTypeByMedicineTypeId(Long MedicineTypeId);
}
