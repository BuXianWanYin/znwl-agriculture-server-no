package com.frog.agriculture.service.impl;

import java.util.HashMap;
import java.util.List;

import com.frog.agriculture.domain.CropBatch;
import com.frog.agriculture.model.UnitParams;
import com.frog.agriculture.iotDomain.AlertLog;
import com.frog.agriculture.iotDomain.Device;
import com.frog.agriculture.iotDomain.Scene;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.UnitMapper;
import com.frog.agriculture.domain.Unit;
import com.frog.agriculture.service.IUnitService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 种植单元Service业务层处理
 * 
 * @author nealtsiao
 * @date 2024-07-26
 */
@Service
public class UnitServiceImpl implements IUnitService
{
    @Autowired
    private UnitMapper unitMapper;

    /**
     * 查询种植单元
     * 
     * @param id 种植单元主键
     * @return 种植单元
     */
    @Override
    public Unit selectUnitById(Long id)
    {
        return unitMapper.selectUnitById(id);
    }

    /**
     * 查询种植单元列表
     * 
     * @param unit 种植单元
     * @return 种植单元
     */
    @Override
    public List<Unit> selectUnitList(Unit unit)
    {
        return unitMapper.selectUnitList(unit);
    }

    /**
     * 新增种植单元
     * 
     * @param unit 种植单元
     * @return 结果
     */
    @Override
    public int insertUnit(Unit unit)
    {
        return unitMapper.insertUnit(unit);
    }

    /**
     * 配置种植单元
     * @param unitParams
     * @return
     */
    @Override
    @Transactional
    public int configUnit(UnitParams unitParams){
        //1设备 2监控 3种植批次 4自动化场
        try {
            /** 简化逻辑，现将此地块的数据全部删除 */
            unitMapper.deleteUnitByLandId(unitParams.getLandId());
            for(Long deviceId : unitParams.getDeviceIds()){
                Unit unit = new Unit();
                unit.setLandId(unitParams.getLandId());
                unit.setType(1);
                unit.setElementId(deviceId);
                unitMapper.insertUnit(unit);
            }
            for(Long cameraId : unitParams.getCameraIds()){
                Unit unit = new Unit();
                unit.setLandId(unitParams.getLandId());
                unit.setType(2);
                unit.setElementId(cameraId);
                unitMapper.insertUnit(unit);
            }
            for(Long batchId : unitParams.getBatchIds()){
                Unit unit = new Unit();
                unit.setLandId(unitParams.getLandId());
                unit.setType(3);
                unit.setElementId(batchId);
                unitMapper.insertUnit(unit);
            }
            for(Long sceneId : unitParams.getSceneIds()){
                Unit unit = new Unit();
                unit.setLandId(unitParams.getLandId());
                unit.setType(4);
                unit.setElementId(sceneId);
                unitMapper.insertUnit(unit);
            }
            return 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     *获取种植单元
     * @param landId
     * @return
     */
    public UnitParams getUnit(Long landId){
        Unit unit = new Unit();
        unit.setLandId(landId);
        List<Unit> units = unitMapper.selectUnitList(unit);
        UnitParams unitParams = new UnitParams();
        unitParams.setLandId(landId);
        for(Unit u : units){
            //1设备 2监控 3种植批次 4自动化场
            if(u.getType()==1){
                unitParams.getDeviceIds().add(u.getElementId());
            }
            if(u.getType()==2){
                unitParams.getCameraIds().add(u.getElementId());
            }
            if(u.getType()==3){
                unitParams.getBatchIds().add(u.getElementId());
            }
            if(u.getType()==4){
                unitParams.getSceneIds().add(u.getElementId());
            }
        }
        return unitParams;
    }

    @Override
    public List<Device> selectDeviceList(Long landId) {
        return unitMapper.selectDeviceList(landId);
    }

    @Override
    public List<Device> selectCameraList(Long landId) {
        return unitMapper.selectCameraList(landId);
    }

    @Override
    public List<HashMap> selectBatchList(Long landId) {
        return unitMapper.selectBatchList(landId) ;
    }

    @Override
    public List<Scene> selectSceneList(Long landId) {
        return unitMapper.selectSceneList(landId);
    }

    @Override
    public List<AlertLog> selectAlertLogList(Long landId) {
        return unitMapper.selectAlertLogList(landId);
    }
}
