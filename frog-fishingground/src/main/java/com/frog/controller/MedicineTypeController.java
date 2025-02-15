package com.frog.controller;

import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import com.frog.common.enums.BusinessType;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.domain.MedicineType;
import com.frog.service.MedicineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 药品类别Controller
 *
 * @author aj
 * @date 2025-02-15
 */
@RestController
@RequestMapping("/fishPasture/MedicineType")
public class MedicineTypeController extends BaseController
{
    @Autowired
    private MedicineTypeService medicineTypeService;

    /**
     * 查询药品类别列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineType:list')")
    @GetMapping("/list")
    public TableDataInfo list(MedicineType MedicineType)
    {
        startPage();
        List<MedicineType> list = medicineTypeService.selectMedicineTypeList(MedicineType);
        return getDataTable(list);
    }

    /**
     * 导出药品类别列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineType:export')")
    @Log(title = "药品类别", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MedicineType MedicineType)
    {
        List<MedicineType> list = medicineTypeService.selectMedicineTypeList(MedicineType);
        ExcelUtil<MedicineType> util = new ExcelUtil<MedicineType>(MedicineType.class);
        util.exportExcel(response, list, "药品类别数据");
    }

    /**
     * 获取药品类别详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineType:query')")
    @GetMapping(value = "/{MedicineTypeId}")
    public AjaxResult getInfo(@PathVariable("MedicineTypeId") Long MedicineTypeId)
    {
        return AjaxResult.success(medicineTypeService.selectMedicineTypeByMedicineTypeId(MedicineTypeId));
    }

    /**
     * 新增药品类别
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineType:add')")
    @Log(title = "药品类别", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MedicineType MedicineType)
    {
        return toAjax(medicineTypeService.insertMedicineType(MedicineType));
    }

    /**
     * 修改药品类别
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineType:edit')")
    @Log(title = "药品类别", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MedicineType MedicineType)
    {
        return toAjax(medicineTypeService.updateMedicineType(MedicineType));
    }

    /**
     * 删除药品类别
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineType:remove')")
    @Log(title = "药品类别", businessType = BusinessType.DELETE)
	@DeleteMapping("/{MedicineTypeIds}")
    public AjaxResult remove(@PathVariable Long[] MedicineTypeIds)
    {
        return toAjax(medicineTypeService.deleteMedicineTypeByMedicineTypeIds(MedicineTypeIds));
    }
}
