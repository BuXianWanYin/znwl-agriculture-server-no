package com.frog.mapper;

import com.frog.domain.BaitInfo;

import java.util.List;

/**
 * 饵料信息Mapper接口
 * 
 * @author aj
 * @date 2025-02-15
 */
public interface BaitInfoMapper 
{
    /**
     * 查询饵料信息
     * 
     * @param BaitId 饵料信息主键
     * @return 饵料信息
     */
    public BaitInfo selectBaitInfoByBaitId(Long BaitId);

    /**
     * 查询饵料信息列表
     * 
     * @param BaitInfo 饵料信息
     * @return 饵料信息集合
     */
    public List<BaitInfo> selectBaitInfoList(BaitInfo BaitInfo);

    /**
     * 新增饵料信息
     * 
     * @param BaitInfo 饵料信息
     * @return 结果
     */
    public int insertBaitInfo(BaitInfo BaitInfo);

    /**
     * 修改饵料信息
     * 
     * @param BaitInfo 饵料信息
     * @return 结果
     */
    public int updateBaitInfo(BaitInfo BaitInfo);

    /**
     * 删除饵料信息
     * 
     * @param BaitId 饵料信息主键
     * @return 结果
     */
    public int deleteBaitInfoByBaitId(Long BaitId);

    /**
     * 批量删除饵料信息
     * 
     * @param BaitIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBaitInfoByBaitIds(Long[] BaitIds);
}
