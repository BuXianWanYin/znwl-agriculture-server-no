package com.frog.agriculture.service;

import java.util.List;
import com.frog.agriculture.domain.SensorAlert;

/**
 * 传感器预警信息服务接口
 * 
 * @author buxianwanyin
 * @date 2025-02-23
 */
public interface ISensorAlertService {
    /**
     * 查询传感器预警信息
     * 
     * @param id 传感器预警信息主键
     * @return 传感器预警信息
     */
    public SensorAlert selectSensorAlertById(Long id);

    /**
     * 查询传感器预警信息列表
     * 
     * @param sensorAlert 传感器预警信息
     * @return 传感器预警信息集合
     */
    public List<SensorAlert> selectSensorAlertList(SensorAlert sensorAlert);

    /**
     * 新增传感器预警信息
     * 
     * @param sensorAlert 传感器预警信息
     * @return 结果
     */
    public int insertSensorAlert(SensorAlert sensorAlert);

    /**
     * 修改传感器预警信息
     * 
     * @param sensorAlert 传感器预警信息
     * @return 结果
     */
    public int updateSensorAlert(SensorAlert sensorAlert);

    /**
     * 批量删除传感器预警信息
     * 
     * @param ids 需要删除的传感器预警信息主键集合
     * @return 结果
     */
    public int deleteSensorAlertByIds(Long[] ids);

    /**
     * 删除传感器预警信息信息
     * 
     * @param id 传感器预警信息主键
     * @return 结果
     */
    public int deleteSensorAlertById(Long id);
    
    /**
     * 根据大棚ID查询预警信息
     * 
     * @param pastureId 大棚ID
     * @return 预警信息集合
     */
    public List<SensorAlert> selectAlertsByPastureId(String pastureId);
    
    /**
     * 查询未处理的预警信息
     * 
     * @return 未处理的预警信息集合
     */
    public List<SensorAlert> selectUnhandledAlerts();
    
    /**
     * 处理预警信息
     * 
     * @param id 预警ID
     * @param remark 处理备注
     * @return 结果
     */
    public int handleAlert(Long id, String remark);
} 