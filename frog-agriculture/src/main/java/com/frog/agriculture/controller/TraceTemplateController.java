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
import com.frog.agriculture.domain.TraceTemplate;
import com.frog.agriculture.service.ITraceTemplateService;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.common.core.page.TableDataInfo;

/**
 * 溯源模版Controller
 *
 * @author nealtsiao
 * @date 2024-04-14
 */
@RestController
@RequestMapping("/agriculture/traceTemplate")
public class TraceTemplateController extends BaseController
{
    @Autowired
    private ITraceTemplateService traceTemplateService;

    /**
     * 查询溯源模版列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(TraceTemplate traceTemplate)
    {
        startPage();
        List<TraceTemplate> list = traceTemplateService.selectTraceTemplateList(traceTemplate);
        return getDataTable(list);
    }

    /**
     * 导出溯源模版列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceTemplate:export')")
    @Log(title = "溯源模版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TraceTemplate traceTemplate)
    {
        List<TraceTemplate> list = traceTemplateService.selectTraceTemplateList(traceTemplate);
        ExcelUtil<TraceTemplate> util = new ExcelUtil<TraceTemplate>(TraceTemplate.class);
        util.exportExcel(response, list, "溯源模版数据");
    }

    /**
     * 获取溯源模版详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceTemplate:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId)
    {
        return AjaxResult.success(traceTemplateService.selectTraceTemplateByTemplateId(templateId));
    }

    /**
     * 新增溯源模版
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceTemplate:add')")
    @Log(title = "溯源模版", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TraceTemplate traceTemplate)
    {
        return toAjax(traceTemplateService.insertTraceTemplate(traceTemplate));
    }

    /**
     * 修改溯源模版
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceTemplate:edit')")
    @Log(title = "溯源模版", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TraceTemplate traceTemplate)
    {
        return toAjax(traceTemplateService.updateTraceTemplate(traceTemplate));
    }

    /**
     * 删除溯源模版
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceTemplate:remove')")
    @Log(title = "溯源模版", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(traceTemplateService.deleteTraceTemplateByTemplateIds(templateIds));
    }
}