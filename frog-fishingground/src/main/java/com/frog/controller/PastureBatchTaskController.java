package com.frog.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.frog.domain.PastureBatchTask;
import com.frog.service.IPastureBatchTaskService;
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
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.common.core.page.TableDataInfo;

/**
 * 批次任务Controller
 *
 * @author xuweidong
 * @date 2023-05-24
 */
@RestController
@RequestMapping("/fishPasture/pastureBatchTask")
public class PastureBatchTaskController extends BaseController
{
    @Autowired
    private IPastureBatchTaskService pastureBatchTaskService;

    /**
     * 查询批次任务列表
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:batchTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(PastureBatchTask batchTask)
    {
        startPage();
        List<PastureBatchTask> list = pastureBatchTaskService.selectPastureBatchTaskList(batchTask);
        return getDataTable(list);
    }

    /**
     * 导出批次任务列表
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:batchTask:export')")
    @Log(title = "批次任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PastureBatchTask batchTask)
    {
        List<PastureBatchTask> list = pastureBatchTaskService.selectPastureBatchTaskList(batchTask);
        ExcelUtil<PastureBatchTask> util = new ExcelUtil<PastureBatchTask>(PastureBatchTask.class);
        util.exportExcel(response, list, "批次任务数据");
    }

    /**
     * 获取批次任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:batchTask:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return AjaxResult.success(pastureBatchTaskService.selectPastureBatchTaskByTaskId(taskId));
    }

    /**
     * 新增批次任务
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:batchTask:add')")
    @Log(title = "批次任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PastureBatchTask batchTask)
    {
        return toAjax(pastureBatchTaskService.insertPastureBatchTask(batchTask));
    }

    /**
     * 修改批次任务
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:batchTask:edit')")
    @Log(title = "批次任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PastureBatchTask batchTask)
    {
        return toAjax(pastureBatchTaskService.updatePastureBatchTask(batchTask));
    }

    /**
     * 删除批次任务
     */
    @PreAuthorize("@ss.hasPermi('fishPasture:batchTask:remove')")
    @Log(title = "批次任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(pastureBatchTaskService.deletePastureBatchTaskByTaskIds(taskIds));
    }

    /** 手机端接口 */
    /**
     * 查询批次任务列表
     */
    @GetMapping("/mobilelist")
    public TableDataInfo mobileList(PastureBatchTask batchTask)
    {
        startPage();
        List<PastureBatchTask> list = pastureBatchTaskService.selectPastureBatchTaskListToMobile(batchTask);
        return getDataTable(list);
    }
}
