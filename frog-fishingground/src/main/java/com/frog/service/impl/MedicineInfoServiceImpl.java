package com.frog.service.impl;

import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.MedicineInfo;
import com.frog.mapper.MedicineInfoMapper;
import com.frog.service.MedicineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 饵料信息Service业务层处理
 *
 * @author aj
 * @date 2025-02-15
 */
@Service
public class MedicineInfoServiceImpl implements MedicineInfoService
{
    @Autowired
    private MedicineInfoMapper medicineInfoMapper;

    /**
     * 查询饵料信息
     *
     * @param MedicineId 饵料信息主键
     * @return 饵料信息
     */
    @Override
    public MedicineInfo selectMedicineInfoByMedicineId(Long MedicineId)
    {
        return medicineInfoMapper.selectMedicineInfoByMedicineId(MedicineId);
    }

    /**
     * 查询饵料信息列表
     *
     * @param MedicineInfo 饵料信息
     * @return 饵料信息
     */
    @Override
    public List<MedicineInfo> selectMedicineInfoList(MedicineInfo MedicineInfo)
    {
        return medicineInfoMapper.selectMedicineInfoList(MedicineInfo);
    }

    /**
     * 新增饵料信息
     *
     * @param MedicineInfo 饵料信息
     * @return 结果
     */
    @Override
    public int insertMedicineInfo(MedicineInfo MedicineInfo)
    {
        MedicineInfo.setCreateBy(SecurityUtils.getUserId().toString());
        MedicineInfo.setCreateTime(DateUtils.getNowDate());
        return medicineInfoMapper.insertMedicineInfo(MedicineInfo);
    }

    /**
     * 修改饵料信息
     *
     * @param MedicineInfo 饵料信息
     * @return 结果
     */
    @Override
    public int updateMedicineInfo(MedicineInfo MedicineInfo)
    {
        MedicineInfo.setUpdateBy(SecurityUtils.getUserId().toString());
        MedicineInfo.setUpdateTime(DateUtils.getNowDate());
        return medicineInfoMapper.updateMedicineInfo(MedicineInfo);
    }

    /**
     * 批量删除饵料信息
     *
     * @param MedicineIds 需要删除的饵料信息主键
     * @return 结果
     */
    @Override
    public int deleteMedicineInfoByMedicineIds(Long[] MedicineIds)
    {
        return medicineInfoMapper.deleteMedicineInfoByMedicineIds(MedicineIds);
    }

    /**
     * 删除饵料信息信息
     *
     * @param MedicineId 饵料信息主键
     * @return 结果
     */
    @Override
    public int deleteMedicineInfoByMedicineId(Long MedicineId)
    {
        return medicineInfoMapper.deleteMedicineInfoByMedicineId(MedicineId);
    }
}
