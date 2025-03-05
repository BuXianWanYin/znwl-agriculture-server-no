package com.frog.agriculture.service.impl;

import java.util.List;

import com.frog.agriculture.domain.FishWaterQuality;
import com.frog.agriculture.mapper.FishWaterQualityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.service.IFishWaterQualityService;

/**
 * 水质数据Service业务层处理
 * 
 * @author nealtsiao
 * @date 2025-02-23
 */
@Service
public class FishWaterQualityServiceImpl implements IFishWaterQualityService 
{
    @Autowired
    private FishWaterQualityMapper fishWaterQualityMapper;
    /**
     * 查询水质数据
     * 
     * @param id 水质数据主键
     * @return 水质数据
     */
    @Override
    public FishWaterQuality selectFishWaterQualityById(Long id)
    {
        return fishWaterQualityMapper.selectFishWaterQualityById(id);
    }

    /**
     * 查询水质数据列表
     * 
     * @param fishWaterQuality 水质数据
     * @return 水质数据
     */
    @Override
    public List<FishWaterQuality> selectFishWaterQualityList(FishWaterQuality fishWaterQuality)
    {

        return fishWaterQualityMapper.selectFishWaterQualityList(fishWaterQuality);
    }

    /**
     * 新增水质数据
     * 
     * @param fishWaterQuality 水质数据
     * @return 结果
     */
    @Override
    public int insertFishWaterQuality(FishWaterQuality fishWaterQuality)
    {
        return fishWaterQualityMapper.insertFishWaterQuality(fishWaterQuality);
    }

    /**
     * 修改水质数据
     * 
     * @param fishWaterQuality 水质数据
     * @return 结果
     */
    @Override
    public int updateFishWaterQuality(FishWaterQuality fishWaterQuality)
    {
        return fishWaterQualityMapper.updateFishWaterQuality(fishWaterQuality);
    }

    /**
     * 批量删除水质数据
     * 
     * @param ids 需要删除的水质数据主键
     * @return 结果
     */
    @Override
    public int deleteFishWaterQualityByIds(Long[] ids)
    {
        return fishWaterQualityMapper.deleteFishWaterQualityByIds(ids);
    }

    /**
     * 删除水质数据信息
     * 
     * @param id 水质数据主键
     * @return 结果
     */
    @Override
    public int deleteFishWaterQualityById(Long id)
    {
        return fishWaterQualityMapper.deleteFishWaterQualityById(id);
    }

    @Override
    public List<FishWaterQuality> selectFishWaterQualityByBatchIdAndDateRange(Long batchId, String startDate, String endDate) {
        return fishWaterQualityMapper.selectFishWaterQualityByBatchIdAndDateRange(batchId, startDate, endDate);
    }
}
