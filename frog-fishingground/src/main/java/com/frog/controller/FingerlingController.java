package com.frog.controller;

import com.frog.domain.Fingerling;
import com.frog.service.IFingerlingService;
import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import com.frog.common.enums.BusinessType;
import com.frog.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 种质Controller
 *
 * @author nealtsiao
 * @date 2023-05-13
 */
@RestController
@RequestMapping("/fishPasture/fingerling")
public class FingerlingController extends BaseController
{
    @Autowired
    private IFingerlingService fingerlingService;

    /**
     * 查询种质列表
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:fingerling:list')")
    @GetMapping("/list")
    public TableDataInfo list(Fingerling fingerling)
    {
        startPage();
        List<Fingerling> list = fingerlingService.selectFingerlingList(fingerling);
        return getDataTable(list);
    }

    /**
     * 导出种质列表
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:fingerling:export')")
    @Log(title = "种质", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Fingerling fingerling)
    {
        List<Fingerling> list = fingerlingService.selectFingerlingList(fingerling);
        ExcelUtil<Fingerling> util = new ExcelUtil<Fingerling>(Fingerling.class);
        util.exportExcel(response, list, "种质数据");
    }

    /**
     * 获取种质详细信息
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:fingerling:query')")
    @GetMapping(value = "/{fingerlingId}")
    public AjaxResult getInfo(@PathVariable("fingerlingId") Long fingerlingId)
    {
        return AjaxResult.success(fingerlingService.selectFingerlingByFingerlingId(fingerlingId));
    }

    /**
     * 新增种质
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:fingerling:add')")
    @Log(title = "种质", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Fingerling fingerling)
    {
        return toAjax(fingerlingService.insertFingerling(fingerling));
    }

    /**
     * 修改种质
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:fingerling:edit')")
    @Log(title = "种质", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Fingerling fingerling)
    {
        return toAjax(fingerlingService.updateFingerling(fingerling));
    }

    /**
     * 删除种质
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:fingerling:remove')")
    @Log(title = "种质", businessType = BusinessType.DELETE)
	@DeleteMapping("/{fingerlingIds}")
    public AjaxResult remove(@PathVariable Long[] fingerlingIds)
    {
        return toAjax(fingerlingService.deleteFingerlingByFingerlingIds(fingerlingIds));
    }
}
