package com.frog.agriculture.mapper;

import java.util.List;
import com.frog.agriculture.domain.WarningInfo;

/**
 * 预警信息Mapper接口
 * 
 * @author 何奎
 * @date 2024-10-20
 */
public interface WarningInfoMapper 
{
    /**
     * 查询预警信息
     * 
     * @param id 预警信息主键
     * @return 预警信息
     */
    public WarningInfo selectWarningInfoById(Long id);

    /**
     * 查询预警信息列表
     * 
     * @param warningInfo 预警信息
     * @return 预警信息集合
     */
    public List<WarningInfo> selectWarningInfoList(WarningInfo warningInfo);

    /**
     * 新增预警信息
     * 
     * @param warningInfo 预警信息
     * @return 结果
     */
    public int insertWarningInfo(WarningInfo warningInfo);

    /**
     * 修改预警信息
     * 
     * @param warningInfo 预警信息
     * @return 结果
     */
    public int updateWarningInfo(WarningInfo warningInfo);

    /**
     * 删除预警信息
     * 
     * @param id 预警信息主键
     * @return 结果
     */
    public int deleteWarningInfoById(Long id);

    /**
     * 批量删除预警信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWarningInfoByIds(Long[] ids);
}
