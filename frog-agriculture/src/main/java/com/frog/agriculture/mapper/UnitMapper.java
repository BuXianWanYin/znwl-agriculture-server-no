package com.frog.agriculture.mapper;

import java.util.HashMap;
import java.util.List;

import com.frog.agriculture.domain.CropBatch;
import com.frog.agriculture.domain.Unit;
import com.frog.agriculture.iotDomain.AlertLog;
import com.frog.agriculture.iotDomain.Device;
import com.frog.agriculture.iotDomain.Scene;

/**
 * 种植单元Mapper接口
 * 
 * @author nealtsiao
 * @date 2024-07-26
 */
public interface UnitMapper 
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

    public List<Device> selectDeviceList(Long landId);
    public List<Device> selectCameraList(Long landId);
    public List<HashMap> selectBatchList(Long landId);
    public List<Scene> selectSceneList(Long landId);
    public List<AlertLog> selectAlertLogList(Long landId);
    /**
     * 新增种植单元
     * 
     * @param unit 种植单元
     * @return 结果
     */
    public int insertUnit(Unit unit);

    /**
     * 删除种植单元
     *
     * @param landId 种植单元主键
     * @return 结果
     */
    public int deleteUnitByLandId(Long landId);
}
