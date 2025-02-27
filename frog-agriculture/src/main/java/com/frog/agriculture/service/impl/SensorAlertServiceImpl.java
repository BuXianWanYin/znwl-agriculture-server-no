package com.frog.agriculture.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.SensorAlertMapper;
import com.frog.agriculture.domain.SensorAlert;
import com.frog.agriculture.service.ISensorAlertService;

/**
 * 传感器预警信息服务实现类
 * 
 * @author buxianwanyin
 * @date 2025-02-23
 */
@Service
public class SensorAlertServiceImpl implements ISensorAlertService {
    @Autowired
    private SensorAlertMapper sensorAlertMapper;

    /**
     * 查询传感器预警信息
     * 
     * @param id 传感器预警信息主键
     * @return 传感器预警信息
     */
    @Override
    public SensorAlert selectSensorAlertById(Long id) {
        return sensorAlertMapper.selectSensorAlertById(id);
    }

    /**
     * 查询传感器预警信息列表
     * 
     * @param sensorAlert 传感器预警信息
     * @return 传感器预警信息
     */
    @Override
    public List<SensorAlert> selectSensorAlertList(SensorAlert sensorAlert) {
        return sensorAlertMapper.selectSensorAlertList(sensorAlert);
    }

    /**
     * 新增传感器预警信息
     * 
     * @param sensorAlert 传感器预警信息
     * @return 结果
     */
    @Override
    public int insertSensorAlert(SensorAlert sensorAlert) {
        return sensorAlertMapper.insertSensorAlert(sensorAlert);
    }

    /**
     * 修改传感器预警信息
     * 
     * @param sensorAlert 传感器预警信息
     * @return 结果
     */
    @Override
    public int updateSensorAlert(SensorAlert sensorAlert) {
        return sensorAlertMapper.updateSensorAlert(sensorAlert);
    }

    /**
     * 批量删除传感器预警信息
     * 
     * @param ids 需要删除的传感器预警信息主键
     * @return 结果
     */
    @Override
    public int deleteSensorAlertByIds(Long[] ids) {
        return sensorAlertMapper.deleteSensorAlertByIds(ids);
    }

    /**
     * 删除传感器预警信息信息
     * 
     * @param id 传感器预警信息主键
     * @return 结果
     */
    @Override
    public int deleteSensorAlertById(Long id) {
        return sensorAlertMapper.deleteSensorAlertById(id);
    }
    
    /**
     * 根据大棚ID查询预警信息
     * 
     * @param pastureId 大棚ID
     * @return 预警信息集合
     */
    @Override
    public List<SensorAlert> selectAlertsByPastureId(String pastureId) {
        return sensorAlertMapper.selectAlertsByPastureId(pastureId);
    }
    
    /**
     * 查询未处理的预警信息
     * 
     * @return 未处理的预警信息集合
     */
    @Override
    public List<SensorAlert> selectUnhandledAlerts() {
        return sensorAlertMapper.selectUnhandledAlerts();
    }
    
    /**
     * 处理预警信息
     * 
     * @param id 预警ID
     * @param remark 处理备注
     * @return 结果
     */
    @Override
    public int handleAlert(Long id, String remark) {
        SensorAlert alert = new SensorAlert();
        alert.setId(id);
        alert.setStatus("1"); // 设置为已处理
        alert.setRemark(remark);
        return sensorAlertMapper.updateSensorAlert(alert);
    }
} 