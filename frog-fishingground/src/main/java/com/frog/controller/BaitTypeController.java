package com.frog.controller;

import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import com.frog.common.enums.BusinessType;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.domain.BaitType;
import com.frog.service.BaitTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 饵料类别Controller
 *
 * @author aj
 * @date 2025-02-15
 */
@RestController
@RequestMapping("/fishPasture/BaitType")
public class BaitTypeController extends BaseController
{
    @Autowired
    private BaitTypeService baitTypeService;

    /**
     * 查询饵料类别列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitType:list')")
    @GetMapping("/list")
    public TableDataInfo list(BaitType BaitType)
    {
        startPage();
        List<BaitType> list = baitTypeService.selectBaitTypeList(BaitType);
        return getDataTable(list);
    }

    /**
     * 导出饵料类别列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitType:export')")
    @Log(title = "饵料类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BaitType BaitType)
    {
        List<BaitType> list = baitTypeService.selectBaitTypeList(BaitType);
        ExcelUtil<BaitType> util = new ExcelUtil<BaitType>(BaitType.class);
        util.exportExcel(response, list, "饵料类别数据");
    }

    /**
     * 获取饵料类别详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitType:query')")
    @GetMapping(value = "/{BaitTypeId}")
    public AjaxResult getInfo(@PathVariable("BaitTypeId") Long BaitTypeId)
    {
        return AjaxResult.success(baitTypeService.selectBaitTypeByBaitTypeId(BaitTypeId));
    }

    /**
     * 新增饵料类别
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitType:add')")
    @Log(title = "饵料类别", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BaitType BaitType)
    {
        return toAjax(baitTypeService.insertBaitType(BaitType));
    }

    /**
     * 修改饵料类别
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitType:edit')")
    @Log(title = "饵料类别", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BaitType BaitType)
    {
        return toAjax(baitTypeService.updateBaitType(BaitType));
    }

    /**
     * 删除饵料类别
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitType:remove')")
    @Log(title = "饵料类别", businessType = BusinessType.DELETE)
	@DeleteMapping("/{BaitTypeIds}")
    public AjaxResult remove(@PathVariable Long[] BaitTypeIds)
    {
        return toAjax(baitTypeService.deleteBaitTypeByBaitTypeIds(BaitTypeIds));
    }
}
