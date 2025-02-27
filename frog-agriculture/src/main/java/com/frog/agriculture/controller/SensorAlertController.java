package com.frog.agriculture.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.frog.agriculture.domain.SensorAlert;
import com.frog.agriculture.service.ISensorAlertService;
import com.frog.common.core.domain.AjaxResult;

/**
 * 传感器预警信息控制器
 * 
 * @author buxianwanyin
 * @date 2025-02-23
 */
@RestController
@RequestMapping("/agriculture/alert")
public class SensorAlertController {
    @Autowired
    private ISensorAlertService sensorAlertService;

    /**
     * 查询传感器预警信息列表
     */
    @GetMapping("/list")
    public AjaxResult list(SensorAlert sensorAlert) {
        List<SensorAlert> list = sensorAlertService.selectSensorAlertList(sensorAlert);
        return AjaxResult.success(list);
    }

    /**
     * 获取传感器预警信息详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(sensorAlertService.selectSensorAlertById(id));
    }

    /**
     * 新增传感器预警信息
     */
    @PostMapping
    public AjaxResult add(@RequestBody SensorAlert sensorAlert) {
        return AjaxResult.success(sensorAlertService.insertSensorAlert(sensorAlert));
    }

    /**
     * 修改传感器预警信息
     */
    @PutMapping
    public AjaxResult edit(@RequestBody SensorAlert sensorAlert) {
        return AjaxResult.success(sensorAlertService.updateSensorAlert(sensorAlert));
    }

    /**
     * 删除传感器预警信息
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return AjaxResult.success(sensorAlertService.deleteSensorAlertByIds(ids));
    }
    
    /**
     * 根据大棚ID查询预警信息
     */
    @GetMapping("/pasture/{pastureId}")
    public AjaxResult getAlertsByPastureId(@PathVariable("pastureId") String pastureId) {
        List<SensorAlert> list = sensorAlertService.selectAlertsByPastureId(pastureId);
        return AjaxResult.success(list);
    }
    
    /**
     * 查询未处理的预警信息
     */
    @GetMapping("/unhandled")
    public AjaxResult getUnhandledAlerts() {
        List<SensorAlert> list = sensorAlertService.selectUnhandledAlerts();
        return AjaxResult.success(list);
    }
    
    /**
     * 处理预警信息
     */
    @PutMapping("/handle/{id}")
    public AjaxResult handleAlert(@PathVariable("id") Long id, @RequestParam("remark") String remark) {
        return AjaxResult.success(sensorAlertService.handleAlert(id, remark));
    }
} 