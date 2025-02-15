package com.frog.service;

import com.frog.domain.BaitType;

import java.util.List;

/**
 * 饵料类别Service接口
 * 
 * @author aj
 * @date 2025-02-15
 */
public interface BaitTypeService 
{
    /**
     * 查询饵料类别
     * 
     * @param BaitTypeId 饵料类别主键
     * @return 饵料类别
     */
    public BaitType selectBaitTypeByBaitTypeId(Long BaitTypeId);

    /**
     * 查询饵料类别列表
     * 
     * @param BaitType 饵料类别
     * @return 饵料类别集合
     */
    public List<BaitType> selectBaitTypeList(BaitType BaitType);

    /**
     * 新增饵料类别
     * 
     * @param BaitType 饵料类别
     * @return 结果
     */
    public int insertBaitType(BaitType BaitType);

    /**
     * 修改饵料类别
     * 
     * @param BaitType 饵料类别
     * @return 结果
     */
    public int updateBaitType(BaitType BaitType);

    /**
     * 批量删除饵料类别
     * 
     * @param BaitTypeIds 需要删除的饵料类别主键集合
     * @return 结果
     */
    public int deleteBaitTypeByBaitTypeIds(Long[] BaitTypeIds);

    /**
     * 删除饵料类别信息
     * 
     * @param BaitTypeId 饵料类别主键
     * @return 结果
     */
    public int deleteBaitTypeByBaitTypeId(Long BaitTypeId);
}
