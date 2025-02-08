package com.frog.agriculture.service.impl;

import java.util.List;

import com.frog.agriculture.service.IWarningInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.WarningInfoMapper;
import com.frog.agriculture.domain.WarningInfo;

/**
 * 预警信息Service业务层处理
 * 
 * @author 何奎
 * @date 2024-10-20
 */
@Service
public class WarningInfoServiceImpl implements IWarningInfoService
{
    @Autowired
    private WarningInfoMapper warningInfoMapper;

    /**
     * 查询预警信息
     * 
     * @param id 预警信息主键
     * @return 预警信息
     */
    @Override
    public WarningInfo selectWarningInfoById(Long id)
    {
        return warningInfoMapper.selectWarningInfoById(id);
    }

    /**
     * 查询预警信息列表
     * 
     * @param warningInfo 预警信息
     * @return 预警信息
     */
    @Override
    public List<WarningInfo> selectWarningInfoList(WarningInfo warningInfo)
    {
        return warningInfoMapper.selectWarningInfoList(warningInfo);
    }

    /**
     * 新增预警信息
     * 
     * @param warningInfo 预警信息
     * @return 结果
     */
    @Override
    public int insertWarningInfo(WarningInfo warningInfo)
    {
        return warningInfoMapper.insertWarningInfo(warningInfo);
    }

    /**
     * 修改预警信息
     * 
     * @param warningInfo 预警信息
     * @return 结果
     */
    @Override
    public int updateWarningInfo(WarningInfo warningInfo)
    {
        return warningInfoMapper.updateWarningInfo(warningInfo);
    }

    /**
     * 批量删除预警信息
     * 
     * @param ids 需要删除的预警信息主键
     * @return 结果
     */
    @Override
    public int deleteWarningInfoByIds(Long[] ids)
    {
        return warningInfoMapper.deleteWarningInfoByIds(ids);
    }

    /**
     * 删除预警信息信息
     * 
     * @param id 预警信息主键
     * @return 结果
     */
    @Override
    public int deleteWarningInfoById(Long id)
    {
        return warningInfoMapper.deleteWarningInfoById(id);
    }
}
