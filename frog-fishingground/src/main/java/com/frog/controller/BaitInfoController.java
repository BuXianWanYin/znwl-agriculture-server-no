package com.frog.controller;

import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import com.frog.common.enums.BusinessType;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.domain.BaitInfo;
import com.frog.service.BaitInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 饵料信息Controller
 *
 * @author aj
 * @date 2025-02-15
 */
@RestController
@RequestMapping("/fishPasture/BaitInfo")
public class BaitInfoController extends BaseController
{
    @Autowired
    private BaitInfoService baitInfoService;

    /**
     * 查询饵料信息列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(BaitInfo BaitInfo)
    {
        startPage();
        List<BaitInfo> list = baitInfoService.selectBaitInfoList(BaitInfo);
        return getDataTable(list);
    }

    /**
     * 导出饵料信息列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitInfo:export')")
    @Log(title = "饵料信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BaitInfo BaitInfo)
    {
        List<BaitInfo> list = baitInfoService.selectBaitInfoList(BaitInfo);
        ExcelUtil<BaitInfo> util = new ExcelUtil<BaitInfo>(BaitInfo.class);
        util.exportExcel(response, list, "饵料信息数据");
    }

    /**
     * 获取饵料信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitInfo:query')")
    @GetMapping(value = "/{BaitId}")
    public AjaxResult getInfo(@PathVariable("BaitId") Long BaitId)
    {
        return AjaxResult.success(baitInfoService.selectBaitInfoByBaitId(BaitId));
    }

    /**
     * 新增饵料信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitInfo:add')")
    @Log(title = "饵料信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BaitInfo BaitInfo)
    {
        return toAjax(baitInfoService.insertBaitInfo(BaitInfo));
    }

    /**
     * 修改饵料信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitInfo:edit')")
    @Log(title = "饵料信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BaitInfo BaitInfo)
    {
        return toAjax(baitInfoService.updateBaitInfo(BaitInfo));
    }

    /**
     * 删除饵料信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:BaitInfo:remove')")
    @Log(title = "饵料信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{BaitIds}")
    public AjaxResult remove(@PathVariable Long[] BaitIds)
    {
        return toAjax(baitInfoService.deleteBaitInfoByBaitIds(BaitIds));
    }
}
