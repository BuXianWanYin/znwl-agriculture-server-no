package com.frog.mapper;

import com.frog.domain.CostMedicine;

import java.util.HashMap;
import java.util.List;

/**
 * 药品用量Mapper接口
 *
 * @author xuweidong
 * @date 2023-05-24
 */
public interface CostMedicineMapper
{
    /**
     * 查询药品用量
     *
     * @param costId 药品用量主键
     * @return 药品用量
     */
    public CostMedicine selectCostMedicineByCostId(Long costId);

    /**
     * 查询药品用量列表
     *
     * @param costMedicine 药品用量
     * @return 药品用量集合
     */
    public List<CostMedicine> selectCostMedicineList(CostMedicine costMedicine);

    /**
     * 新增药品用量
     *
     * @param costMedicine 药品用量
     * @return 结果
     */
    public int insertCostMedicine(CostMedicine costMedicine);

    /**
     * 修改药品用量
     *
     * @param costMedicine 药品用量
     * @return 结果
     */
    public int updateCostMedicine(CostMedicine costMedicine);

    /**
     * 删除药品用量
     *
     * @param costId 药品用量主键
     * @return 结果
     */
    public int deleteCostMedicineByCostId(Long costId);

    /**
     * 批量删除药品用量
     *
     * @param costIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCostMedicineByCostIds(Long[] costIds);

    /**
     * 手机端任务详情使用，按照药品类别统计用量
     * @param taskId
     * @return
     */
    public List<HashMap> selectMedicineGroupByMedicineName(Long taskId);
}
