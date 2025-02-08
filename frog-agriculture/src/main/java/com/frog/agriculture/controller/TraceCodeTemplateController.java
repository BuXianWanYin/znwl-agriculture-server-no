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
import com.frog.agriculture.domain.TraceCodeTemplate;
import com.frog.agriculture.service.ITraceCodeTemplateService;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.common.core.page.TableDataInfo;

/**
 * 溯源码模版Controller
 * 
 * @author nealtsiao
 * @date 2024-04-02
 */
@RestController
@RequestMapping("/agriculture/traceCodeTemplate")
public class TraceCodeTemplateController extends BaseController
{
    @Autowired
    private ITraceCodeTemplateService traceCodeTemplateService;

    /**
     * 查询溯源码模版列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceCodeTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(TraceCodeTemplate traceCodeTemplate)
    {
        startPage();
        List<TraceCodeTemplate> list = traceCodeTemplateService.selectTraceCodeTemplateList(traceCodeTemplate);
        return getDataTable(list);
    }

    /**
     * 导出溯源码模版列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceCodeTemplate:export')")
    @Log(title = "溯源码模版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TraceCodeTemplate traceCodeTemplate)
    {
        List<TraceCodeTemplate> list = traceCodeTemplateService.selectTraceCodeTemplateList(traceCodeTemplate);
        ExcelUtil<TraceCodeTemplate> util = new ExcelUtil<TraceCodeTemplate>(TraceCodeTemplate.class);
        util.exportExcel(response, list, "溯源码模版数据");
    }

    /**
     * 获取溯源码模版详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceCodeTemplate:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId)
    {
        return AjaxResult.success(traceCodeTemplateService.selectTraceCodeTemplateByTemplateId(templateId));
    }

    /**
     * 新增溯源码模版
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceCodeTemplate:add')")
    @Log(title = "溯源码模版", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TraceCodeTemplate traceCodeTemplate)
    {
        return toAjax(traceCodeTemplateService.insertTraceCodeTemplate(traceCodeTemplate));
    }

    /**
     * 修改溯源码模版
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceCodeTemplate:edit')")
    @Log(title = "溯源码模版", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TraceCodeTemplate traceCodeTemplate)
    {
        return toAjax(traceCodeTemplateService.updateTraceCodeTemplate(traceCodeTemplate));
    }

    /**
     * 删除溯源码模版
     */
    @PreAuthorize("@ss.hasPermi('agriculture:traceCodeTemplate:remove')")
    @Log(title = "溯源码模版", businessType = BusinessType.DELETE)
	@DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(traceCodeTemplateService.deleteTraceCodeTemplateByTemplateIds(templateIds));
    }
}
