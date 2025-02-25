package com.frog.IaAgriculture.mapper;

import com.frog.IaAgriculture.vo.SoilSensorValueVo;
import java.util.List;

public interface SoilSensorValueVoMapper {
    /**
     * 根据查询条件获取土壤传感器数据列表 (支持分页)
     *
     * @param vo 查询条件封装
     * @return 土壤传感器数据
     */
    List<SoilSensorValueVo> selectSoilSensorValueList(SoilSensorValueVo vo);

    /**
     * 获取土壤传感器数据总数
     */
    int countSoilSensorValue(SoilSensorValueVo vo);
}