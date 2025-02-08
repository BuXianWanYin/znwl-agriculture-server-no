package com.frog.agriculture.controller;

import com.frog.agriculture.domain.TraceRecord;
import com.frog.agriculture.service.ITraceCodeService;
import com.frog.agriculture.service.ITraceRecordService;
import com.frog.agriculture.service.ITraceTemplateService;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 溯源页面控制器,免登录
 * @author neal
 * @date 2024/4/29  09:09
 */
@RestController
@RequestMapping("/agriculture/traceWebSite")
public class TraceWebSiteController extends BaseController {
    @Autowired
    private ITraceRecordService traceRecordService;
    @Autowired
    private ITraceTemplateService traceTemplateService;
    @Autowired
    private ITraceCodeService traceCodeService;

    /**
     * 新增溯源记录接口
     * @param traceRecord
     * @return
     */
    @PostMapping("/addRecord")
    public AjaxResult addRecord(@RequestBody TraceRecord traceRecord)
    {
        return toAjax(traceRecordService.insertTraceRecord(traceRecord));
    }
    /**
     * 获取溯源码详情
     * @param templateId
     * @return
     */
    @GetMapping(value = "/getTraceTemplateByTemplateId/{templateId}")
    public AjaxResult getTraceTemplateByTemplateId(@PathVariable("templateId") Long templateId)
    {
        return AjaxResult.success(traceTemplateService.selectTraceTemplateByTemplateId(templateId));
    }

    /**
     * 根据溯源码获取溯源页面模版
     * @param traceCode
     * @return
     */
    @GetMapping("/getTraceTemplateByTraceCode/{traceCode}")
    public AjaxResult getTraceTemplateByTraceCode(@PathVariable("traceCode") String traceCode){
        return AjaxResult.success(traceTemplateService.getTraceTemplateByTraceCode(traceCode));
    }
    /**
     * 根据traceCode获取溯源码信息
     * @param traceCode
     * @return
     */
    @GetMapping(value = "/getCodeByTraceCode/{traceCode}")
    public AjaxResult getCodeByTraceCode(@PathVariable("traceCode") String traceCode)
    {
        return AjaxResult.success(traceCodeService.selectTraceCodeByTraceCode(traceCode));
    }

    @GetMapping(value="/getRecordList")
    public TableDataInfo getRecordList(TraceRecord traceRecord){
        List<TraceRecord> traceRecords = traceRecordService.selectTraceRecordList(traceRecord);
        return getDataTable(traceRecords);
    }

}
