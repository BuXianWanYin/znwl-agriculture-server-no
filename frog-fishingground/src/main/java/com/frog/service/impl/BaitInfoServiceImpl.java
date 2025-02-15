package com.frog.service.impl;

import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.BaitInfo;
import com.frog.mapper.BaitInfoMapper;
import com.frog.service.BaitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 饵料信息Service业务层处理
 *
 * @author aj
 * @date 2025-02-15
 */
@Service
public class BaitInfoServiceImpl implements BaitInfoService 
{
    @Autowired
    private BaitInfoMapper baitInfoMapper;

    /**
     * 查询饵料信息
     * 
     * @param BaitId 饵料信息主键
     * @return 饵料信息
     */
    @Override
    public BaitInfo selectBaitInfoByBaitId(Long BaitId)
    {
        return baitInfoMapper.selectBaitInfoByBaitId(BaitId);
    }

    /**
     * 查询饵料信息列表
     * 
     * @param BaitInfo 饵料信息
     * @return 饵料信息
     */
    @Override
    public List<BaitInfo> selectBaitInfoList(BaitInfo BaitInfo)
    {
        return baitInfoMapper.selectBaitInfoList(BaitInfo);
    }

    /**
     * 新增饵料信息
     * 
     * @param BaitInfo 饵料信息
     * @return 结果
     */
    @Override
    public int insertBaitInfo(BaitInfo BaitInfo)
    {
        BaitInfo.setCreateBy(SecurityUtils.getUserId().toString());
        BaitInfo.setCreateTime(DateUtils.getNowDate());
        return baitInfoMapper.insertBaitInfo(BaitInfo);
    }

    /**
     * 修改饵料信息
     * 
     * @param BaitInfo 饵料信息
     * @return 结果
     */
    @Override
    public int updateBaitInfo(BaitInfo BaitInfo)
    {
        BaitInfo.setUpdateBy(SecurityUtils.getUserId().toString());
        BaitInfo.setUpdateTime(DateUtils.getNowDate());
        return baitInfoMapper.updateBaitInfo(BaitInfo);
    }

    /**
     * 批量删除饵料信息
     * 
     * @param BaitIds 需要删除的饵料信息主键
     * @return 结果
     */
    @Override
    public int deleteBaitInfoByBaitIds(Long[] BaitIds)
    {
        return baitInfoMapper.deleteBaitInfoByBaitIds(BaitIds);
    }

    /**
     * 删除饵料信息信息
     * 
     * @param BaitId 饵料信息主键
     * @return 结果
     */
    @Override
    public int deleteBaitInfoByBaitId(Long BaitId)
    {
        return baitInfoMapper.deleteBaitInfoByBaitId(BaitId);
    }
}
