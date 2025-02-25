package com.frog.agriculture.mapper;

import java.util.List;
import com.frog.agriculture.domain.SoilSensorValue;
import org.apache.ibatis.annotations.Select;

/**
 * 土壤环境数据Mapper接口
 * 
 * @author nealtsiao
 * @date 2025-02-23
 */
public interface SoilSensorValueMapper 
{
    /**
     * 查询土壤环境数据
     * 
     * @param id 土壤环境数据主键
     * @return 土壤环境数据
     */
    public SoilSensorValue selectSoilSensorValueById(String id);

    /**
     * 查询土壤环境数据列表
     * 
     * @param soilSensorValue 土壤环境数据
     * @return 土壤环境数据集合
     */
    public List<SoilSensorValue> selectSoilSensorValueList(SoilSensorValue soilSensorValue);

    /**
     * 查询大棚环境数据列表
     *
     * @param pastureId 大棚ID
     * @return 土壤环境数据集合
     */
    @Select("SELECT * FROM soil_sensor_value WHERE pasture_id = #{pastureId} AND del_flag = '0'")
    List<SoilSensorValue> findByPastureId(String pastureId);

    /**
     * 新增土壤环境数据
     * 
     * @param soilSensorValue 土壤环境数据
     * @return 结果
     */
    public int insertSoilSensorValue(SoilSensorValue soilSensorValue);

    /**
     * 修改土壤环境数据
     * 
     * @param soilSensorValue 土壤环境数据
     * @return 结果
     */
    public int updateSoilSensorValue(SoilSensorValue soilSensorValue);

    /**
     * 删除土壤环境数据
     * 
     * @param id 土壤环境数据主键
     * @return 结果
     */
    public int deleteSoilSensorValueById(String id);

    /**
     * 批量删除土壤环境数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSoilSensorValueByIds(String[] ids);
}
