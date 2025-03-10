package com.frog.agriculture.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.enums.BusinessType;
import com.frog.agriculture.domain.FishWaterQuality;
import com.frog.agriculture.service.IFishWaterQualityService;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.common.core.page.TableDataInfo;

/**
 * 水质数据Controller
 * 
 * @author nealtsiao
 * @date 2025-02-23
 */
@RestController
@RequestMapping("/agriculture/quality")
public class FishWaterQualityController extends BaseController
{
    @Autowired
    private IFishWaterQualityService fishWaterQualityService;

    /**
     * 查询水质数据列表
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:quality:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishWaterQuality fishWaterQuality)
    {
        startPage();
        List<FishWaterQuality> list = fishWaterQualityService.selectFishWaterQualityList(fishWaterQuality);
        return getDataTable(list);
    }

    /**
     * 导出水质数据列表
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:quality:export')")
    @Log(title = "水质数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FishWaterQuality fishWaterQuality)
    {
        List<FishWaterQuality> list = fishWaterQualityService.selectFishWaterQualityList(fishWaterQuality);
        ExcelUtil<FishWaterQuality> util = new ExcelUtil<FishWaterQuality>(FishWaterQuality.class);
        util.exportExcel(response, list, "水质数据数据");
    }

    /**
     * 获取水质数据详细信息
     */
 //   @PreAuthorize("@ss.hasPermi('agriculture:quality:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(fishWaterQualityService.selectFishWaterQualityById(id));
    }

    /**
     * 新增水质数据
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:quality:add')")
    @Log(title = "水质数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishWaterQuality fishWaterQuality)
    {
        return toAjax(fishWaterQualityService.insertFishWaterQuality(fishWaterQuality));
    }

    /**
     * 修改水质数据
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:quality:edit')")
    @Log(title = "水质数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishWaterQuality fishWaterQuality)
    {
        return toAjax(fishWaterQualityService.updateFishWaterQuality(fishWaterQuality));
    }

    /**
     * 删除水质数据
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:quality:remove')")
    @Log(title = "水质数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(fishWaterQualityService.deleteFishWaterQualityByIds(ids));
    }

    /**
     * 根据批次ID和日期范围查询水质数据
     *
     * @param batchId 批次ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 水质数据列表
     */
    @GetMapping("/range")
    public List<FishWaterQuality> getFishWaterQualityByBatchIdAndDateRange(
            @RequestParam Long batchId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return fishWaterQualityService.selectFishWaterQualityByBatchIdAndDateRange(batchId, startDate, endDate);
    }

    /**
     * 查询水质数据详细列表 最新日期排序
     */
    @GetMapping("/detailList")
    public TableDataInfo detailList(FishWaterQuality fishWaterQuality)
    {
        startPage();
        List<FishWaterQuality> list = fishWaterQualityService.selectFishWaterQualityDetailList(fishWaterQuality);
        return getDataTable(list);
    }

}
