package com.frog.service.impl;

import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.CostBait;
import com.frog.mapper.CostBaitMapper;
import com.frog.service.CostBaitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 饵料用量Service业务层处理
 *
 * @author aj
 * @date 2024-02-15
 */
@Service
public class CostBaitServiceImpl implements CostBaitService
{
    @Autowired
    private CostBaitMapper costBaitMapper;

    /**
     * 查询饵料用量
     * 
     * @param costId 饵料用量主键
     * @return 饵料用量
     */
    @Override
    public CostBait selectCostBaitByCostId(Long costId)
    {
        return costBaitMapper.selectCostBaitByCostId(costId);
    }

    /**
     * 查询饵料用量列表
     * 
     * @param CostBait 饵料用量
     * @return 饵料用量
     */
    @Override
    public List<CostBait> selectCostBaitList(CostBait CostBait)
    {
        return costBaitMapper.selectCostBaitList(CostBait);
    }

    /**
     * 新增饵料用量
     * 
     * @param CostBait 饵料用量
     * @return 结果
     */
    @Override
    public int insertCostBait(CostBait CostBait)
    {
        CostBait.setCreateBy(SecurityUtils.getUserId().toString());
        CostBait.setCreateTime(DateUtils.getNowDate());
        return costBaitMapper.insertCostBait(CostBait);
    }

    /**
     * 修改饵料用量
     * 
     * @param CostBait 饵料用量
     * @return 结果
     */
    @Override
    public int updateCostBait(CostBait CostBait)
    {
        CostBait.setUpdateBy(SecurityUtils.getUserId().toString());
        CostBait.setUpdateTime(DateUtils.getNowDate());
        return costBaitMapper.updateCostBait(CostBait);
    }

    /**
     * 批量删除饵料用量
     * 
     * @param costIds 需要删除的饵料用量主键
     * @return 结果
     */
    @Override
    public int deleteCostBaitByCostIds(Long[] costIds)
    {
        return costBaitMapper.deleteCostBaitByCostIds(costIds);
    }

    /**
     * 删除饵料用量信息
     * 
     * @param costId 饵料用量主键
     * @return 结果
     */
    @Override
    public int deleteCostBaitByCostId(Long costId)
    {
        return costBaitMapper.deleteCostBaitByCostId(costId);
    }

    /**
     * 手机端任务详情使用，按照饵料分组统计用量
     * @param taskId
     * @return
     */
    @Override
    public List<HashMap> selectBaitGroupByBaitName(Long taskId) {
        return costBaitMapper.selectBaitGroupByBaitName(taskId);
    }
}
