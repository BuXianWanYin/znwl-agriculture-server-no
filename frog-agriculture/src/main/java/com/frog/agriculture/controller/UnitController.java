package com.frog.agriculture.controller;

import java.util.HashMap;
import java.util.List;

import com.frog.agriculture.domain.CropBatch;
import com.frog.agriculture.domain.Land;
import com.frog.agriculture.model.UnitParams;
import com.frog.agriculture.iotDomain.AlertLog;
import com.frog.agriculture.iotDomain.Device;
import com.frog.agriculture.iotDomain.Scene;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.agriculture.service.IUnitService;
import com.frog.common.core.page.TableDataInfo;

/**
 * 种植单元Controller
 * 
 * @author nealtsiao
 * @date 2024-07-26
 */
@RestController
@RequestMapping("/agriculture/unit")
public class UnitController extends BaseController
{
    @Autowired
    private IUnitService unitService;

    /**
     * 配置种植单元
     */
    @PreAuthorize("@ss.hasPermi('agriculture:unit:cfg')")
    @PostMapping("/cfg")
    public AjaxResult cfg(@RequestBody UnitParams unitParams)
    {
        return toAjax(unitService.configUnit(unitParams));
    }

    @PreAuthorize("@ss.hasPermi('agriculture:unit:cfg')")
    @GetMapping("getUnit/{landId}")
    public AjaxResult getUnit(@PathVariable Long landId){
        return AjaxResult.success(unitService.getUnit(landId));
    }
    @GetMapping("selectDeviceList/{landId}")
    public TableDataInfo selectDeviceList(@PathVariable Long landId){
        startPage();
        List<Device> list = unitService.selectDeviceList(landId);
        return getDataTable(list);
    }

    @GetMapping("selectCameraList/{landId}")
    public TableDataInfo selectCameraList(@PathVariable Long landId){
        startPage();
        List<Device> list = unitService.selectCameraList(landId);
        return getDataTable(list);
    }

    @GetMapping("selectBatchList/{landId}")
    public TableDataInfo selectBatchList(@PathVariable Long landId){
        startPage();
        List<HashMap> list = unitService.selectBatchList(landId);
        return getDataTable(list);
    }
    @GetMapping("selectSceneList/{landId}")
    public TableDataInfo selectSceneList(@PathVariable Long landId){
        startPage();
        List<Scene> list = unitService.selectSceneList(landId);
        return getDataTable(list);
    }

    @GetMapping("selectAlertLogList")
    public TableDataInfo selectAlertLogList(Land land){
        startPage();
        List<AlertLog> list = unitService.selectAlertLogList(land.getLandId());
        return getDataTable(list);
    }
}
