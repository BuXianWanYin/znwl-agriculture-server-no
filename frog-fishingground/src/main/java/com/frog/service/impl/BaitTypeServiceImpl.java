package com.frog.service.impl;

import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.BaitType;
import com.frog.mapper.BaitTypeMapper;
import com.frog.service.BaitTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 饵料类别Service业务层处理
 *
 * @author aj
 * @date 2025-02-15
 */
@Service
public class BaitTypeServiceImpl implements BaitTypeService
{
    @Autowired
    private BaitTypeMapper baitTypeMapper;

    /**
     * 查询饵料类别
     * 
     * @param BaitTypeId 饵料类别主键
     * @return 饵料类别
     */
    @Override
    public BaitType selectBaitTypeByBaitTypeId(Long BaitTypeId)
    {
        return baitTypeMapper.selectBaitTypeByBaitTypeId(BaitTypeId);
    }

    /**
     * 查询饵料类别列表
     * 
     * @param BaitType 饵料类别
     * @return 饵料类别
     */
    @Override
    public List<BaitType> selectBaitTypeList(BaitType BaitType)
    {
        return baitTypeMapper.selectBaitTypeList(BaitType);
    }

    /**
     * 新增饵料类别
     * 
     * @param BaitType 饵料类别
     * @return 结果
     */
    @Override
    public int insertBaitType(BaitType BaitType)
    {
        BaitType.setCreateBy(SecurityUtils.getUserId().toString());
        BaitType.setCreateTime(DateUtils.getNowDate());
        return baitTypeMapper.insertBaitType(BaitType);
    }

    /**
     * 修改饵料类别
     * 
     * @param BaitType 饵料类别
     * @return 结果
     */
    @Override
    public int updateBaitType(BaitType BaitType)
    {
        BaitType.setUpdateBy(SecurityUtils.getUserId().toString());
        BaitType.setUpdateTime(DateUtils.getNowDate());
        return baitTypeMapper.updateBaitType(BaitType);
    }

    /**
     * 批量删除饵料类别
     * 
     * @param BaitTypeIds 需要删除的饵料类别主键
     * @return 结果
     */
    @Override
    public int deleteBaitTypeByBaitTypeIds(Long[] BaitTypeIds)
    {
        return baitTypeMapper.deleteBaitTypeByBaitTypeIds(BaitTypeIds);
    }

    /**
     * 删除饵料类别信息
     * 
     * @param BaitTypeId 饵料类别主键
     * @return 结果
     */
    @Override
    public int deleteBaitTypeByBaitTypeId(Long BaitTypeId)
    {
        return baitTypeMapper.deleteBaitTypeByBaitTypeId(BaitTypeId);
    }
}
