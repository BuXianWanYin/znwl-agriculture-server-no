package com.frog.controller;

import com.frog.domain.CostMedicine;
import com.frog.service.ICostMedicineService;
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
 * 农资用量Controller
 *
 * @author xuweidong
 * @date 2023-05-24
 */
@RestController
@RequestMapping("/fishPasture/costMedicine")
public class CostMedicineController extends BaseController
{
    @Autowired
    private ICostMedicineService costMedicineService;

    /**
     * 查询农资用量列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costMedicine:list')")
    @GetMapping("/list")
    public TableDataInfo list(CostMedicine costMedicine)
    {
        startPage();
        List<CostMedicine> list = costMedicineService.selectCostMedicineList(costMedicine);
        return getDataTable(list);
    }

    /**
     * 导出农资用量列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costMedicine:export')")
    @Log(title = "农资用量", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CostMedicine costMedicine)
    {
        List<CostMedicine> list = costMedicineService.selectCostMedicineList(costMedicine);
        ExcelUtil<CostMedicine> util = new ExcelUtil<CostMedicine>(CostMedicine.class);
        util.exportExcel(response, list, "农资用量数据");
    }

    /**
     * 获取农资用量详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costMedicine:query')")
    @GetMapping(value = "/{costId}")
    public AjaxResult getInfo(@PathVariable("costId") Long costId)
    {
        return AjaxResult.success(costMedicineService.selectCostMedicineByCostId(costId));
    }

    /**
     * 新增农资用量
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costMedicine:add')")
    @Log(title = "农资用量", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CostMedicine costMedicine)
    {
        return toAjax(costMedicineService.insertCostMedicine(costMedicine));
    }

    /**
     * 修改农资用量
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costMedicine:edit')")
    @Log(title = "农资用量", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CostMedicine costMedicine)
    {
        return toAjax(costMedicineService.updateCostMedicine(costMedicine));
    }

    /**
     * 删除农资用量
     */
    @PreAuthorize("@ss.hasPermi('agriculture:costMedicine:remove')")
    @Log(title = "农资用量", businessType = BusinessType.DELETE)
	@DeleteMapping("/{costIds}")
    public AjaxResult remove(@PathVariable Long[] costIds)
    {
        return toAjax(costMedicineService.deleteCostMedicineByCostIds(costIds));
    }

    /** 手机端接口 **/

    /**
     * 手机端任务详情界面，按照农资名称分组统计用量
     * @param taskId
     * @return
     */
    @GetMapping(value = "selectMedicineGroupByMedicineName/{taskId}")
    public AjaxResult selectMedicineGroupByMedicineName(@PathVariable("taskId") Long taskId)
    {
        return AjaxResult.success(costMedicineService.selectMedicineGroupByMedicineName(taskId));
    }
}
