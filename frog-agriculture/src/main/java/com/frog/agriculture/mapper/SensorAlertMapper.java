package com.frog.agriculture.mapper;

import java.util.List;
import com.frog.agriculture.domain.SensorAlert;
import org.apache.ibatis.annotations.Param;

/**
 * 传感器预警信息数据层
 * 
 * @author buxianwanyin
 * @date 2025-02-23
 */
public interface SensorAlertMapper {
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
     * 删除传感器预警信息
     * 
     * @param id 传感器预警信息主键
     * @return 结果
     */
    public int deleteSensorAlertById(Long id);

    /**
     * 批量删除传感器预警信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSensorAlertByIds(Long[] ids);
    
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
     * 根据大棚/鱼棚类型查询预警信息列表
     * 
     * @param pastureType 大棚/鱼棚类型（0：大棚，1：鱼棚）
     * @return 预警信息列表
     */
    public List<SensorAlert> selectSensorAlertsByPastureType(String pastureType);

    /**
     * 查询最近处理过的预警
     * @param alert 查询条件
     * @param minutes 时间范围（分钟）
     * @return 最近处理过的预警列表
     */
    List<SensorAlert> selectRecentProcessedAlerts(@Param("alert") SensorAlert alert, @Param("minutes") int minutes);
} 