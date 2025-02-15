package com.frog.service.impl;

import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.MedicineType;
import com.frog.mapper.MedicineTypeMapper;
import com.frog.service.MedicineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 饵料类别Service业务层处理
 *
 * @author aj
 * @date 2025-02-15
 */
@Service
public class MedicineTypeServiceImpl implements MedicineTypeService
{
    @Autowired
    private MedicineTypeMapper medicineTypeMapper;

    /**
     * 查询饵料类别
     *
     * @param MedicineTypeId 饵料类别主键
     * @return 饵料类别
     */
    @Override
    public MedicineType selectMedicineTypeByMedicineTypeId(Long MedicineTypeId)
    {
        return medicineTypeMapper.selectMedicineTypeByMedicineTypeId(MedicineTypeId);
    }

    /**
     * 查询饵料类别列表
     *
     * @param MedicineType 饵料类别
     * @return 饵料类别
     */
    @Override
    public List<MedicineType> selectMedicineTypeList(MedicineType MedicineType)
    {
        return medicineTypeMapper.selectMedicineTypeList(MedicineType);
    }

    /**
     * 新增饵料类别
     *
     * @param MedicineType 饵料类别
     * @return 结果
     */
    @Override
    public int insertMedicineType(MedicineType MedicineType)
    {
        MedicineType.setCreateBy(SecurityUtils.getUserId().toString());
        MedicineType.setCreateTime(DateUtils.getNowDate());
        return medicineTypeMapper.insertMedicineType(MedicineType);
    }

    /**
     * 修改饵料类别
     *
     * @param MedicineType 饵料类别
     * @return 结果
     */
    @Override
    public int updateMedicineType(MedicineType MedicineType)
    {
        MedicineType.setUpdateBy(SecurityUtils.getUserId().toString());
        MedicineType.setUpdateTime(DateUtils.getNowDate());
        return medicineTypeMapper.updateMedicineType(MedicineType);
    }

    /**
     * 批量删除饵料类别
     *
     * @param MedicineTypeIds 需要删除的饵料类别主键
     * @return 结果
     */
    @Override
    public int deleteMedicineTypeByMedicineTypeIds(Long[] MedicineTypeIds)
    {
        return medicineTypeMapper.deleteMedicineTypeByMedicineTypeIds(MedicineTypeIds);
    }

    /**
     * 删除饵料类别信息
     *
     * @param MedicineTypeId 饵料类别主键
     * @return 结果
     */
    @Override
    public int deleteMedicineTypeByMedicineTypeId(Long MedicineTypeId)
    {
        return medicineTypeMapper.deleteMedicineTypeByMedicineTypeId(MedicineTypeId);
    }
}
