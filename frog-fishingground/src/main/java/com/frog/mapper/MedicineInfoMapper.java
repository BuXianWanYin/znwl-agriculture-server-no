package com.frog.mapper;

import com.frog.domain.MedicineInfo;

import java.util.List;

/**
 * 饵料信息Mapper接口
 *
 * @author aj
 * @date 2025-02-15
 */
public interface MedicineInfoMapper
{
    /**
     * 查询饵料信息
     *
     * @param MedicineId 饵料信息主键
     * @return 饵料信息
     */
    public MedicineInfo selectMedicineInfoByMedicineId(Long MedicineId);

    /**
     * 查询饵料信息列表
     *
     * @param MedicineInfo 饵料信息
     * @return 饵料信息集合
     */
    public List<MedicineInfo> selectMedicineInfoList(MedicineInfo MedicineInfo);

    /**
     * 新增饵料信息
     *
     * @param MedicineInfo 饵料信息
     * @return 结果
     */
    public int insertMedicineInfo(MedicineInfo MedicineInfo);

    /**
     * 修改饵料信息
     *
     * @param MedicineInfo 饵料信息
     * @return 结果
     */
    public int updateMedicineInfo(MedicineInfo MedicineInfo);

    /**
     * 删除饵料信息
     *
     * @param MedicineId 饵料信息主键
     * @return 结果
     */
    public int deleteMedicineInfoByMedicineId(Long MedicineId);

    /**
     * 批量删除饵料信息
     *
     * @param MedicineIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMedicineInfoByMedicineIds(Long[] MedicineIds);
}
