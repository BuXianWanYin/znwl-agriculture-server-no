package com.frog.mapper;

import com.frog.domain.CostBait;

import java.util.HashMap;
import java.util.List;

/**
 * 饵料用量Mapper接口
 *
 * @author aj
 * @date 2024-02-15
 */
public interface CostBaitMapper 
{
    /**
     * 查询饵料用量
     * 
     * @param costId 饵料用量主键
     * @return 饵料用量
     */
    public CostBait selectCostBaitByCostId(Long costId);

    /**
     * 查询饵料用量列表
     * 
     * @param CostBait 饵料用量
     * @return 饵料用量集合
     */
    public List<CostBait> selectCostBaitList(CostBait CostBait);

    /**
     * 新增饵料用量
     * 
     * @param CostBait 饵料用量
     * @return 结果
     */
    public int insertCostBait(CostBait CostBait);

    /**
     * 修改饵料用量
     * 
     * @param CostBait 饵料用量
     * @return 结果
     */
    public int updateCostBait(CostBait CostBait);

    /**
     * 删除饵料用量
     * 
     * @param costId 饵料用量主键
     * @return 结果
     */
    public int deleteCostBaitByCostId(Long costId);

    /**
     * 批量删除饵料用量
     * 
     * @param costIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCostBaitByCostIds(Long[] costIds);

    /**
     * 手机端任务详情使用，按照饵料类别统计用量
     * @param taskId
     * @return
     */
    public List<HashMap> selectBaitGroupByBaitName(Long taskId);
}
