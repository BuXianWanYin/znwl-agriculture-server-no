package com.frog.mapper;

import com.frog.domain.MedicineInfo;

import java.util.List;

/**
 * 药品信息Mapper接口
 *
 * @author aj
 * @date 2025-02-15
 */
public interface MedicineInfoMapper
{
    /**
     * 查询药品信息
     *
     * @param MedicineId 药品信息主键
     * @return 药品信息
     */
    public MedicineInfo selectMedicineInfoByMedicineId(Long MedicineId);

    /**
     * 查询药品信息列表
     *
     * @param MedicineInfo 药品信息
     * @return 药品信息集合
     */
    public List<MedicineInfo> selectMedicineInfoList(MedicineInfo MedicineInfo);

    /**
     * 新增药品信息
     *
     * @param MedicineInfo 药品信息
     * @return 结果
     */
    public int insertMedicineInfo(MedicineInfo MedicineInfo);

    /**
     * 修改药品信息
     *
     * @param MedicineInfo 药品信息
     * @return 结果
     */
    public int updateMedicineInfo(MedicineInfo MedicineInfo);

    /**
     * 删除药品信息
     *
     * @param MedicineId 药品信息主键
     * @return 结果
     */
    public int deleteMedicineInfoByMedicineId(Long MedicineId);

    /**
     * 批量删除药品信息
     *
     * @param MedicineIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMedicineInfoByMedicineIds(Long[] MedicineIds);
}
