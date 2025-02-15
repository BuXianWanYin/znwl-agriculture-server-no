package com.frog.controller;

import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import com.frog.common.enums.BusinessType;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.domain.CostBait;
import com.frog.service.CostBaitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 饵料用量Controller
 *
 * @author aj
 * @date 2024-02-15
 */
@RestController
@RequestMapping("/fishPasture/costBait")
public class CostBaitController extends BaseController
{
    @Autowired
    private CostBaitService costBaitService;

    /**
     * 查询饵料用量列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costBait:list')")
    @GetMapping("/list")
    public TableDataInfo list(CostBait costBait)
    {
        startPage();
        List<CostBait> list = costBaitService.selectCostBaitList(costBait);
        return getDataTable(list);
    }

    /**
     * 导出饵料用量列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costBait:export')")
    @Log(title = "饵料用量", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CostBait costBait)
    {
        List<CostBait> list = costBaitService.selectCostBaitList(costBait);
        ExcelUtil<CostBait> util = new ExcelUtil<CostBait>(CostBait.class);
        util.exportExcel(response, list, "饵料用量数据");
    }

    /**
     * 获取饵料用量详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costBait:query')")
    @GetMapping(value = "/{costId}")
    public AjaxResult getInfo(@PathVariable("costId") Long costId)
    {
        return AjaxResult.success(costBaitService.selectCostBaitByCostId(costId));
    }

    /**
     * 新增饵料用量
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costBait:add')")
    @Log(title = "饵料用量", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CostBait costBait)
    {
        return toAjax(costBaitService.insertCostBait(costBait));
    }

    /**
     * 修改饵料用量
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costBait:edit')")
    @Log(title = "饵料用量", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CostBait costBait)
    {
        return toAjax(costBaitService.updateCostBait(costBait));
    }

    /**
     * 删除饵料用量
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costBait:remove')")
    @Log(title = "饵料用量", businessType = BusinessType.DELETE)
	@DeleteMapping("/{costIds}")
    public AjaxResult remove(@PathVariable Long[] costIds)
    {
        return toAjax(costBaitService.deleteCostBaitByCostIds(costIds));
    }

    /** 手机端接口 **/

    /**
     * 手机端任务详情界面，按照饵料名称分组统计用量
     * @param taskId
     * @return
     */
    @GetMapping(value = "selectBaitGroupByBaitName/{taskId}")
    public AjaxResult selectBaitGroupByBaitName(@PathVariable("taskId") Long taskId)
    {
        return AjaxResult.success(costBaitService.selectBaitGroupByBaitName(taskId));
    }
}
