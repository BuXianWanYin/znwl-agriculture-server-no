package com.frog.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.frog.domain.FishBatchTask;
import com.frog.service.FishBatchTaskService;
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
 */
@RestController
@RequestMapping("/fishPasture/fishbatchTask")
public class FishBatchTaskController extends BaseController
{
    @Autowired
    private FishBatchTaskService fishBatchTaskService;

    /**
     * 查询批次任务列表
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:batchTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(FishBatchTask fishBatchTask)
    {
        startPage();
        List<FishBatchTask> list = fishBatchTaskService.selectBatchTaskList(fishBatchTask);
        return getDataTable(list);
    }

    /**
     * 导出批次任务列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batchTask:export')")
    @Log(title = "批次任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FishBatchTask fishBatchTask)
    {
        List<FishBatchTask> list = fishBatchTaskService.selectBatchTaskList(fishBatchTask);
        ExcelUtil<FishBatchTask> util = new ExcelUtil<FishBatchTask>(FishBatchTask.class);
        util.exportExcel(response, list, "批次任务数据");
    }

    /**
     * 获取批次任务详细信息
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:batchTask:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId)
    {
        return AjaxResult.success(fishBatchTaskService.selectBatchTaskByTaskId(taskId));
    }

    /**
     * 新增批次任务
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batchTask:add')")
    @Log(title = "批次任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FishBatchTask fishBatchTask)
    {
        return toAjax(fishBatchTaskService.insertBatchTask(fishBatchTask));
    }

    /**
     * 修改批次任务
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batchTask:edit')")
    @Log(title = "批次任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FishBatchTask fishBatchTask)
    {
        return toAjax(fishBatchTaskService.updateBatchTask(fishBatchTask));
    }

    /**
     * 删除批次任务
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batchTask:remove')")
    @Log(title = "批次任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(fishBatchTaskService.deleteBatchTaskByTaskIds(taskIds));
    }

    /** 手机端接口 */
    /**
     * 查询批次任务列表
     */
    @GetMapping("/mobilelist")
    public TableDataInfo mobileList(FishBatchTask fishBatchTask)
    {
        startPage();
        List<FishBatchTask> list = fishBatchTaskService.selectBatchTaskListToMobile(fishBatchTask);
        return getDataTable(list);
    }
}
