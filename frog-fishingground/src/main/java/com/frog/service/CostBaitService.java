package com.frog.service;

import java.util.HashMap;
import java.util.List;
import com.frog.domain.CostBait;

/**
 * 饵料用量Service接口
 *
 * @author aj
 * @date 2024-02-15
 */
public interface CostBaitService
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
     * 批量删除饵料用量
     *
     * @param costIds 需要删除的饵料用量主键集合
     * @return 结果
     */
    public int deleteCostBaitByCostIds(Long[] costIds);

    /**
     * 删除饵料用量信息
     *
     * @param costId 饵料用量主键
     * @return 结果
     */
    public int deleteCostBaitByCostId(Long costId);

    public List<HashMap> selectBaitGroupByBaitName(Long taskId);
}
