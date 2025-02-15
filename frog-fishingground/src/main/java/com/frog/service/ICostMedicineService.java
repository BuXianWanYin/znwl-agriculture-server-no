package com.frog.service;

import com.frog.domain.CostMedicine;

import java.util.HashMap;
import java.util.List;

/**
 * 农资用量Service接口
 *
 * @author xuweidong
 * @date 2023-05-24
 */
public interface ICostMedicineService
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
     * 批量删除农资用量
     *
     * @param costIds 需要删除的农资用量主键集合
     * @return 结果
     */
    public int deleteCostMedicineByCostIds(Long[] costIds);

    /**
     * 删除农资用量信息
     *
     * @param costId 农资用量主键
     * @return 结果
     */
    public int deleteCostMedicineByCostId(Long costId);

    public List<HashMap> selectMedicineGroupByMedicineName(Long taskId);
}
