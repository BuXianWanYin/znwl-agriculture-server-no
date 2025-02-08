package com.frog.agriculture.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.frog.agriculture.domain.WarningInfo;
import com.frog.agriculture.service.IWarningInfoService;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.common.core.page.TableDataInfo;

/**
 * 预警信息Controller
 * 
 * @author hekui
 * @date 2024-10-20
 */
@RestController
@RequestMapping("/agriculture/info")
public class WarningInfoController extends BaseController
{
    @Autowired
    private IWarningInfoService warningInfoService;

    /**
     * 查询预警信息列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarningInfo warningInfo)
    {
        startPage();
        List<WarningInfo> list = warningInfoService.selectWarningInfoList(warningInfo);
        return getDataTable(list);
    }

    /**
     * 导出预警信息列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:info:export')")
    @Log(title = "预警信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarningInfo warningInfo)
    {
        List<WarningInfo> list = warningInfoService.selectWarningInfoList(warningInfo);
        ExcelUtil<WarningInfo> util = new ExcelUtil<WarningInfo>(WarningInfo.class);
        util.exportExcel(response, list, "预警信息数据");
    }

    /**
     * 获取预警信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(warningInfoService.selectWarningInfoById(id));
    }

    /**
     * 新增预警信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:info:add')")
    @Log(title = "预警信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WarningInfo warningInfo)
    {
        return toAjax(warningInfoService.insertWarningInfo(warningInfo));
    }

    /**
     * 修改预警信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:info:edit')")
    @Log(title = "预警信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WarningInfo warningInfo)
    {
        return toAjax(warningInfoService.updateWarningInfo(warningInfo));
    }

    /**
     * 删除预警信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:info:remove')")
    @Log(title = "预警信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(warningInfoService.deleteWarningInfoByIds(ids));
    }
}
