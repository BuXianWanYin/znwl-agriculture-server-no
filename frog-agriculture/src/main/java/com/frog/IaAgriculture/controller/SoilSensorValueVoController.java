package com.frog.IaAgriculture.controller;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.IaAgriculture.mapper.SoilSensorValueVoMapper;
import com.frog.IaAgriculture.vo.SoilSensorValueVo;
import com.frog.agriculture.mapper.SoilSensorValueMapper;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/soilsensorvaluevo")
public class SoilSensorValueVoController extends BaseController {
    @Autowired
    private SoilSensorValueVoMapper soilSensorValueVoMapper;
    /**
     * 根据条件查询土壤传感器数据 (支持分页)
     *
     * @param vo 查询条件封装
     * @return 分页后的土壤传感器数据列表
     */
    @GetMapping("/values")
    public TableDataInfo getSoilSensorValues(SoilSensorValueVo vo) {
        startPage();
        List<SoilSensorValueVo> data = soilSensorValueVoMapper.selectSoilSensorValueList(vo);

        return getDataTable(data);
    }
}
