package com.frog.mapper;


import com.frog.domain.MedicineType;

import java.util.List;

/**
 * 机药品类别Mapper接口
 *
 * @author aj
 * @date 2025-02-15
 *
 */
public interface MedicineTypeMapper {

    /**
     * 查询机药品类别
     *
     * @param MedicineTypeId 机药品类别主键
     * @return 机药品类别
     */
    public MedicineType selectMedicineTypeByMedicineTypeId(Long MedicineTypeId);

    /**
     * 查询机药品类别列表
     *
     * @param MedicineType 机药品类别
     * @return 机药品类别集合
     */
    public List<MedicineType> selectMedicineTypeList(MedicineType MedicineType);

    /**
     * 新增机药品类别
     *
     * @param MedicineType 机药品类别
     * @return 结果
     */
    public int insertMedicineType(MedicineType MedicineType);

    /**
     * 修改机药品类别
     *
     * @param MedicineType 机药品类别
     * @return 结果
     */
    public int updateMedicineType(MedicineType MedicineType);

    /**
     * 删除机药品类别
     *
     * @param MedicineTypeId 机药品类别主键
     * @return 结果
     */
    public int deleteMedicineTypeByMedicineTypeId(Long MedicineTypeId);

    /**
     * 批量删除机药品类别
     *
     * @param MedicineTypeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMedicineTypeByMedicineTypeIds(Long[] MedicineTypeIds);
}
