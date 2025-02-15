package com.frog.mapper;


import java.util.List;

import com.frog.domain.BaitType;

/**
 * 机饵料类别Mapper接口
 *
 * @author aj
 * @date 2025-02-15
 * 
 */
public interface BaitTypeMapper {

    /**
     * 查询机饵料类别
     *
     * @param BaitTypeId 机饵料类别主键
     * @return 机饵料类别
     */
    public BaitType selectBaitTypeByBaitTypeId(Long BaitTypeId);

    /**
     * 查询机饵料类别列表
     *
     * @param BaitType 机饵料类别
     * @return 机饵料类别集合
     */
    public List<BaitType> selectBaitTypeList(BaitType BaitType);

    /**
     * 新增机饵料类别
     *
     * @param BaitType 机饵料类别
     * @return 结果
     */
    public int insertBaitType(BaitType BaitType);

    /**
     * 修改机饵料类别
     *
     * @param BaitType 机饵料类别
     * @return 结果
     */
    public int updateBaitType(BaitType BaitType);

    /**
     * 删除机饵料类别
     *
     * @param BaitTypeId 机饵料类别主键
     * @return 结果
     */
    public int deleteBaitTypeByBaitTypeId(Long BaitTypeId);

    /**
     * 批量删除机饵料类别
     *
     * @param BaitTypeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBaitTypeByBaitTypeIds(Long[] BaitTypeIds);
}
