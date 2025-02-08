package com.frog.agriculture.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.enums.BusinessType;
import com.frog.agriculture.domain.TraceCode;
import com.frog.agriculture.service.ITraceCodeService;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.common.core.page.TableDataInfo;

/**
 * 溯源码Controller
 * 
 * @author nealtsiao
 * @date 2023-08-04
 */
@RestController
@RequestMapping("/agriculture/code")
public class TraceCodeController extends BaseController
{
    @Autowired
    private ITraceCodeService traceCodeService;

    /**
     * 查询溯源码列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:code:list')")
    @GetMapping("/list")
    public TableDataInfo list(TraceCode traceCode)
    {
        startPage();
        List<TraceCode> list = traceCodeService.selectTraceCodeList(traceCode);
        return getDataTable(list);
    }

    /**
     * 查询异常溯源码列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:code:alertList')")
    @GetMapping("/alertList")
    public TableDataInfo alertList(TraceCode traceCode)
    {
        startPage();
        List<TraceCode> list = traceCodeService.selectAlertTraceCodeList(traceCode);
        return getDataTable(list);
    }

    /**
     * 导出溯源码列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:code:export')")
    @Log(title = "溯源码", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TraceCode traceCode)
    {
        List<TraceCode> list = traceCodeService.selectTraceCodeList(traceCode);
        ExcelUtil<TraceCode> util = new ExcelUtil<TraceCode>(TraceCode.class);
        util.exportExcel(response, list, "溯源码数据");
    }

    @PreAuthorize("@ss.hasPermi('agriculture:code:download')")
    @Log(title = "溯源码", businessType = BusinessType.OTHER)
    @PostMapping("/download")
    public void download(HttpServletResponse response, TraceCode traceCode) throws IOException {
        // 设置Content-Type
        response.setContentType("application/zip");
        // 设置Content-Disposition，文件名可以自定义，这里假设我们命名为"images.zip"
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"images.zip\"");
        traceCodeService.downloadTraceCodeImagesZip(response,traceCode);
    }

    @PreAuthorize("@ss.hasPermi('agriculture:code:generateCode')")
    @Log(title = "生成溯源码", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody Map<String,Long> map){

        traceCodeService.batchInsertTraceCode(map.get("versionId"),map.get("codeNum"));
        return AjaxResult.success();
    }
    /**
     * 获取溯源码详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:code:query')")
    @GetMapping(value = "/{codeId}")
    public AjaxResult getInfo(@PathVariable("codeId") Long codeId)
    {
        return AjaxResult.success(traceCodeService.selectTraceCodeByCodeId(codeId));
    }

    /**
     * 新增溯源码
     */
    @PreAuthorize("@ss.hasPermi('agriculture:code:add')")
    @Log(title = "溯源码", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TraceCode traceCode)
    {
        return toAjax(traceCodeService.insertTraceCode(traceCode));
    }

    /**
     * 修改溯源码
     */
    @PreAuthorize("@ss.hasPermi('agriculture:code:edit')")
    @Log(title = "溯源码", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TraceCode traceCode)
    {
        return toAjax(traceCodeService.updateTraceCode(traceCode));
    }

    /**
     * 删除溯源码
     */
    @PreAuthorize("@ss.hasPermi('agriculture:code:remove')")
    @Log(title = "溯源码", businessType = BusinessType.DELETE)
	@DeleteMapping("/{codeIds}")
    public AjaxResult remove(@PathVariable Long[] codeIds)
    {
        return toAjax(traceCodeService.deleteTraceCodeByCodeIds(codeIds));
    }
}
