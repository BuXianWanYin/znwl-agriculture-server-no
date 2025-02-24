package com.frog.agriculture.mapper;

import java.util.List;
import com.frog.agriculture.domain.FishWaterQuality;

/**
 * 水质数据Mapper接口
 * 
 * @author nealtsiao
 * @date 2025-02-23
 */
public interface FishWaterQualityMapper 
{
    /**
     * 查询水质数据
     * 
     * @param id 水质数据主键
     * @return 水质数据
     */
    public FishWaterQuality selectFishWaterQualityById(Long id);

    /**
     * 查询水质数据列表
     * 
     * @param fishWaterQuality 水质数据
     * @return 水质数据集合
     */
    public List<FishWaterQuality> selectFishWaterQualityList(FishWaterQuality fishWaterQuality);

    /**
     * 新增水质数据
     * 
     * @param fishWaterQuality 水质数据
     * @return 结果
     */
    public int insertFishWaterQuality(FishWaterQuality fishWaterQuality);

    /**
     * 修改水质数据
     * 
     * @param fishWaterQuality 水质数据
     * @return 结果
     */
    public int updateFishWaterQuality(FishWaterQuality fishWaterQuality);

    /**
     * 删除水质数据
     * 
     * @param id 水质数据主键
     * @return 结果
     */
    public int deleteFishWaterQualityById(Long id);

    /**
     * 批量删除水质数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFishWaterQualityByIds(Long[] ids);
}
