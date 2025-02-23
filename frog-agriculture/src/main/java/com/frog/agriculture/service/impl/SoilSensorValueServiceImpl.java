package com.frog.agriculture.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.SoilSensorValueMapper;
import com.frog.agriculture.domain.SoilSensorValue;
import com.frog.agriculture.service.ISoilSensorValueService;

/**
 * 菜的环境数据Service业务层处理
 * 
 * @author nealtsiao
 * @date 2025-02-23
 */
@Service
public class SoilSensorValueServiceImpl implements ISoilSensorValueService 
{
    @Autowired
    private SoilSensorValueMapper soilSensorValueMapper;

    /**
     * 查询菜的环境数据
     * 
     * @param id 菜的环境数据主键
     * @return 菜的环境数据
     */
    @Override
    public SoilSensorValue selectSoilSensorValueById(String id)
    {
        return soilSensorValueMapper.selectSoilSensorValueById(id);
    }

    /**
     * 查询菜的环境数据列表
     * 
     * @param soilSensorValue 菜的环境数据
     * @return 菜的环境数据
     */
    @Override
    public List<SoilSensorValue> selectSoilSensorValueList(SoilSensorValue soilSensorValue)
    {
        return soilSensorValueMapper.selectSoilSensorValueList(soilSensorValue);
    }

    /**
     * 新增菜的环境数据
     * 
     * @param soilSensorValue 菜的环境数据
     * @return 结果
     */
    @Override
    public int insertSoilSensorValue(SoilSensorValue soilSensorValue)
    {
        return soilSensorValueMapper.insertSoilSensorValue(soilSensorValue);
    }

    /**
     * 修改菜的环境数据
     * 
     * @param soilSensorValue 菜的环境数据
     * @return 结果
     */
    @Override
    public int updateSoilSensorValue(SoilSensorValue soilSensorValue)
    {
        return soilSensorValueMapper.updateSoilSensorValue(soilSensorValue);
    }

    /**
     * 批量删除菜的环境数据
     * 
     * @param ids 需要删除的菜的环境数据主键
     * @return 结果
     */
    @Override
    public int deleteSoilSensorValueByIds(String[] ids)
    {
        return soilSensorValueMapper.deleteSoilSensorValueByIds(ids);
    }

    /**
     * 删除菜的环境数据信息
     * 
     * @param id 菜的环境数据主键
     * @return 结果
     */
    @Override
    public int deleteSoilSensorValueById(String id)
    {
        return soilSensorValueMapper.deleteSoilSensorValueById(id);
    }
}
