package com.frog.mapper;

import com.frog.domain.CostMedicine;

import java.util.HashMap;
import java.util.List;

/**
 * 农资用量Mapper接口
 *
 * @author xuweidong
 * @date 2023-05-24
 */
public interface CostMedicineMapper
{
    /**
     * 查询农资用量
     *
     * @param costId 农资用量主键
     * @return 农资用量
     */
    public CostMedicine selectCostMedicineByCostId(Long costId);

    /**
     * 查询农资用量列表
     *
     * @param costMedicine 农资用量
     * @return 农资用量集合
     */
    public List<CostMedicine> selectCostMedicineList(CostMedicine costMedicine);

    /**
     * 新增农资用量
     *
     * @param costMedicine 农资用量
     * @return 结果
     */
    public int insertCostMedicine(CostMedicine costMedicine);

    /**
     * 修改农资用量
     *
     * @param costMedicine 农资用量
     * @return 结果
     */
    public int updateCostMedicine(CostMedicine costMedicine);

    /**
     * 删除农资用量
     *
     * @param costId 农资用量主键
     * @return 结果
     */
    public int deleteCostMedicineByCostId(Long costId);

    /**
     * 批量删除农资用量
     *
     * @param costIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCostMedicineByCostIds(Long[] costIds);

    /**
     * 手机端任务详情使用，按照农资类别统计用量
     * @param taskId
     * @return
     */
    public List<HashMap> selectMedicineGroupByMedicineName(Long taskId);
}
