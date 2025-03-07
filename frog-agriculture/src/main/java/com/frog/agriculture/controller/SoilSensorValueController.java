package com.frog.agriculture.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.frog.common.annotation.Anonymous;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.enums.BusinessType;
import com.frog.agriculture.domain.SoilSensorValue;
import com.frog.agriculture.service.ISoilSensorValueService;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.common.core.page.TableDataInfo;

/**
 * 土壤环境数据Controller
 * 
 * @author nealtsiao
 * @date 2025-02-23
 */

@RestController
@RequestMapping("/agriculture/value")
public class SoilSensorValueController extends BaseController
{
    @Autowired
    private ISoilSensorValueService soilSensorValueService;

    /**
     * 查询土壤环境数据列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SoilSensorValue soilSensorValue)
    {
        startPage();
        List<SoilSensorValue> list = soilSensorValueService.selectSoilSensorValueList(soilSensorValue);
        return getDataTable(list);
    }

    /**
     * 导出土壤环境数据列表
     */
    @Log(title = "土壤环境数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SoilSensorValue soilSensorValue)
    {
        List<SoilSensorValue> list = soilSensorValueService.selectSoilSensorValueList(soilSensorValue);
        ExcelUtil<SoilSensorValue> util = new ExcelUtil<SoilSensorValue>(SoilSensorValue.class);
        util.exportExcel(response, list, "土壤环境数据");
    }

    /**
     * 获取土壤环境数据详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(soilSensorValueService.selectSoilSensorValueById(id));
    }
    /**
     * 获取大棚土壤环境数据详细信息
     */
    @GetMapping("/pasture/{pastureId}")
    public List<SoilSensorValue> getSoilSensorValuesByPastureId(@PathVariable String pastureId) {
        return soilSensorValueService.getSoilSensorValuesByPastureId(pastureId);
    }
    /**
     * 新增土壤环境数据
     */
    @Log(title = "土壤环境数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SoilSensorValue soilSensorValue)
    {
        return toAjax(soilSensorValueService.insertSoilSensorValue(soilSensorValue));
    }

    /**
     * 修改土壤环境数据
     */
    @Log(title = "土壤环境数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SoilSensorValue soilSensorValue)
    {
        return toAjax(soilSensorValueService.updateSoilSensorValue(soilSensorValue));
    }

    /**
     * 删除土壤环境数据
     */
    @Log(title = "土壤环境数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(soilSensorValueService.deleteSoilSensorValueByIds(ids));
    }

    /**
     * 根据批次ID和日期范围查询土壤环境数据
     */
    @GetMapping("/range")
    public TableDataInfo getSoilSensorValuesByBatchIdAndDateRange(
            @RequestParam Long batchId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        startPage();
        List<SoilSensorValue> list = soilSensorValueService.selectSoilSensorValuesByBatchIdAndDateRange(batchId, startDate, endDate);
        return getDataTable(list);
    }

}
