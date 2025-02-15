package com.frog.controller;

import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import com.frog.common.enums.BusinessType;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.domain.MedicineInfo;
import com.frog.service.MedicineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 药品信息Controller
 *
 * @author aj
 * @date 2025-02-15
 */
@RestController
@RequestMapping("/fishPasture/MedicineInfo")
public class MedicineInfoController extends BaseController
{
    @Autowired
    private MedicineInfoService medicineInfoService;

    /**
     * 查询药品信息列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(MedicineInfo MedicineInfo)
    {
        startPage();
        List<MedicineInfo> list = medicineInfoService.selectMedicineInfoList(MedicineInfo);
        return getDataTable(list);
    }

    /**
     * 导出药品信息列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineInfo:export')")
    @Log(title = "药品信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MedicineInfo MedicineInfo)
    {
        List<MedicineInfo> list = medicineInfoService.selectMedicineInfoList(MedicineInfo);
        ExcelUtil<MedicineInfo> util = new ExcelUtil<MedicineInfo>(MedicineInfo.class);
        util.exportExcel(response, list, "药品信息数据");
    }

    /**
     * 获取药品信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineInfo:query')")
    @GetMapping(value = "/{MedicineId}")
    public AjaxResult getInfo(@PathVariable("MedicineId") Long MedicineId)
    {
        return AjaxResult.success(medicineInfoService.selectMedicineInfoByMedicineId(MedicineId));
    }

    /**
     * 新增药品信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineInfo:add')")
    @Log(title = "药品信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MedicineInfo MedicineInfo)
    {
        return toAjax(medicineInfoService.insertMedicineInfo(MedicineInfo));
    }

    /**
     * 修改药品信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineInfo:edit')")
    @Log(title = "药品信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MedicineInfo MedicineInfo)
    {
        return toAjax(medicineInfoService.updateMedicineInfo(MedicineInfo));
    }

    /**
     * 删除药品信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:MedicineInfo:remove')")
    @Log(title = "药品信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{MedicineIds}")
    public AjaxResult remove(@PathVariable Long[] MedicineIds)
    {
        return toAjax(medicineInfoService.deleteMedicineInfoByMedicineIds(MedicineIds));
    }
}
