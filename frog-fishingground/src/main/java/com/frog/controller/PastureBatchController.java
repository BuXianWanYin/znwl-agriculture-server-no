package com.frog.controller;


import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.frog.service.PastureBatchService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.enums.BusinessType;
import com.frog.agriculture.domain.CropBatch;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.common.core.page.TableDataInfo;

/**
 * 鱼物批次Controller
 *
 * @author aj
 * @date 2024-2-13
 */
@RestController
@RequestMapping("/fishPasture/batch")
public class PastureBatchController  extends BaseController {

    @Autowired
    private PastureBatchService pastureBatchService;

    /**
     * 查询鱼物批次列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(CropBatch cropBatch) {
        startPage();
        List<CropBatch> list = pastureBatchService.selectCropBatchList(cropBatch);
        return getDataTable(list);
    }


    /**
     * 导出鱼物批次列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:export')")
    @Log(title = "鱼物批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CropBatch cropBatch) {
        List<CropBatch> list = pastureBatchService.selectCropBatchList(cropBatch);
        ExcelUtil<CropBatch> util = new ExcelUtil<CropBatch>(CropBatch.class);
        util.exportExcel(response, list, "鱼物批次数据");
    }

    /**
     * 获取鱼物批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:query')")
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable("batchId") Long batchId) {
        return AjaxResult.success(pastureBatchService.selectCropBatchByBatchId(batchId));
    }

    /**
     * 新增鱼物批次
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:add')")
    @Log(title = "鱼物批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CropBatch cropBatch) {
        return toAjax(pastureBatchService.insertCropBatch(cropBatch));
    }

    /**
     * 修改鱼物批次
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:edit')")
    @Log(title = "鱼物批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CropBatch cropBatch) {
        return toAjax(pastureBatchService.updateCropBatch(cropBatch));
    }

    /**
     * 删除鱼物批次
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:remove')")
    @Log(title = "鱼物批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds) {
        return toAjax(pastureBatchService.deleteCropBatchByBatchIds(batchIds));
    }
    /** ----- 手机端接口----- **/

    /**
     * 手机端鱼物批次列表
     */
    @GetMapping("/mobilelist")
    public TableDataInfo mobileList(CropBatch cropBatch) {
        startPage();
        List<CropBatch> list = pastureBatchService.selectCropBatchListToMobile(cropBatch);
        return getDataTable(list);
    }
}
