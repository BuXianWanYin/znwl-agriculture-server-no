package com.frog.agriculture.mapper;

import java.util.List;
import com.frog.agriculture.domain.SoilSensorValue;

/**
 * 菜的环境数据Mapper接口
 * 
 * @author nealtsiao
 * @date 2025-02-23
 */
public interface SoilSensorValueMapper 
{
    /**
     * 查询菜的环境数据
     * 
     * @param id 菜的环境数据主键
     * @return 菜的环境数据
     */
    public SoilSensorValue selectSoilSensorValueById(String id);

    /**
     * 查询菜的环境数据列表
     * 
     * @param soilSensorValue 菜的环境数据
     * @return 菜的环境数据集合
     */
    public List<SoilSensorValue> selectSoilSensorValueList(SoilSensorValue soilSensorValue);

    /**
     * 新增菜的环境数据
     * 
     * @param soilSensorValue 菜的环境数据
     * @return 结果
     */
    public int insertSoilSensorValue(SoilSensorValue soilSensorValue);

    /**
     * 修改菜的环境数据
     * 
     * @param soilSensorValue 菜的环境数据
     * @return 结果
     */
    public int updateSoilSensorValue(SoilSensorValue soilSensorValue);

    /**
     * 删除菜的环境数据
     * 
     * @param id 菜的环境数据主键
     * @return 结果
     */
    public int deleteSoilSensorValueById(String id);

    /**
     * 批量删除菜的环境数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSoilSensorValueByIds(String[] ids);
}
