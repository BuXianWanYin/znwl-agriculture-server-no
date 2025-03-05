package com.frog.agriculture.mapper;

import java.util.List;
import com.frog.agriculture.domain.FishWaterQuality;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 根据批次ID和日期范围查询水质数据
     *
     * @param batchId 批次ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 水质数据列表
     */
    @Select("SELECT * FROM fish_water_quality WHERE fish_pasture_batch_id = #{batchId} AND date BETWEEN #{startDate} AND #{endDate}")
    List<FishWaterQuality> selectFishWaterQualityByBatchIdAndDateRange(@Param("batchId") Long batchId,
                                                                       @Param("startDate") String startDate,
                                                                       @Param("endDate") String endDate);
}
