package com.frog.service.impl;

import com.frog.domain.CostMedicine;
import com.frog.mapper.CostMedicineMapper;
import com.frog.service.ICostMedicineService;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 农资用量Service业务层处理
 *
 * @author xuweidong
 * @date 2023-05-24
 */
@Service
public class CostMedicineServiceImpl implements ICostMedicineService
{
    @Autowired
    private CostMedicineMapper costMedicineMapper;

    /**
     * 查询农资用量
     *
     * @param costId 农资用量主键
     * @return 农资用量
     */
    @Override
    public CostMedicine selectCostMedicineByCostId(Long costId)
    {
        return costMedicineMapper.selectCostMedicineByCostId(costId);
    }

    /**
     * 查询农资用量列表
     *
     * @param costMedicine 农资用量
     * @return 农资用量
     */
    @Override
    public List<CostMedicine> selectCostMedicineList(CostMedicine costMedicine)
    {
        return costMedicineMapper.selectCostMedicineList(costMedicine);
    }

    /**
     * 新增农资用量
     *
     * @param costMedicine 农资用量
     * @return 结果
     */
    @Override
    public int insertCostMedicine(CostMedicine costMedicine)
    {
        costMedicine.setCreateBy(SecurityUtils.getUserId().toString());
        costMedicine.setCreateTime(DateUtils.getNowDate());
        return costMedicineMapper.insertCostMedicine(costMedicine);
    }

    /**
     * 修改农资用量
     *
     * @param costMedicine 农资用量
     * @return 结果
     */
    @Override
    public int updateCostMedicine(CostMedicine costMedicine)
    {
        costMedicine.setUpdateBy(SecurityUtils.getUserId().toString());
        costMedicine.setUpdateTime(DateUtils.getNowDate());
        return costMedicineMapper.updateCostMedicine(costMedicine);
    }

    /**
     * 批量删除农资用量
     *
     * @param costIds 需要删除的农资用量主键
     * @return 结果
     */
    @Override
    public int deleteCostMedicineByCostIds(Long[] costIds)
    {
        return costMedicineMapper.deleteCostMedicineByCostIds(costIds);
    }

    /**
     * 删除农资用量信息
     *
     * @param costId 农资用量主键
     * @return 结果
     */
    @Override
    public int deleteCostMedicineByCostId(Long costId)
    {
        return costMedicineMapper.deleteCostMedicineByCostId(costId);
    }

    /**
     * 手机端任务详情使用，按照农资分组统计用量
     * @param costMedicine
     * @return
     */
    @Override
    public List<HashMap> selectMedicineGroupByMedicineName(Long taskId) {
        return costMedicineMapper.selectMedicineGroupByMedicineName(taskId);
    }
}
