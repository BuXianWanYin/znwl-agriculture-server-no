package com.frog.agriculture.service;

import java.util.HashMap;
import java.util.List;

import com.frog.agriculture.domain.CropBatch;
import com.frog.agriculture.domain.Unit;
import com.frog.agriculture.model.UnitParams;
import com.frog.agriculture.iotDomain.AlertLog;
import com.frog.agriculture.iotDomain.Device;
import com.frog.agriculture.iotDomain.Scene;

/**
 * 种植单元Service接口
 * 
 * @author nealtsiao
 * @date 2024-07-26
 */
public interface IUnitService 
{
    /**
     * 查询种植单元
     * 
     * @param id 种植单元主键
     * @return 种植单元
     */
    public Unit selectUnitById(Long id);

    /**
     * 查询种植单元列表
     * 
     * @param unit 种植单元
     * @return 种植单元集合
     */
    public List<Unit> selectUnitList(Unit unit);

    /**
     * 新增种植单元
     * 
     * @param unit 种植单元
     * @return 结果
     */
    public int insertUnit(Unit unit);

    /**
     * 配置种植单元
     * @param unitParams
     * @return
     */
    public int configUnit(UnitParams unitParams);

    /**
     *获取种植单元
     * @param landId
     * @return
     */
    public UnitParams getUnit(Long landId);

    /**
     * 获取单元IOT设备
     * @param landId
     * @return
     */
    public List<Device> selectDeviceList(Long landId);

    /**
     * 获取单元监控设备
     * @param landId
     * @return
     */
    public List<Device> selectCameraList(Long landId);

    /**
     * 获取单元种植批次
     * @param landId
     * @return
     */
    public List<HashMap> selectBatchList(Long landId);

    /**
     * 获取单元场景
     * @param landId
     * @return
     */
    public List<Scene> selectSceneList(Long landId);

    /**
     * 获取告警日志
     * @param landId
     * @return
     */
    public List<AlertLog> selectAlertLogList(Long landId);
}
