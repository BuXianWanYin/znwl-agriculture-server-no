package com.frog.controller;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.common.annotation.Log;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import com.frog.common.enums.BusinessType;
import com.frog.common.utils.poi.ExcelUtil;
import com.frog.domain.Species;
import com.frog.service.IspeciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/fishPasture/species")
public class SpeciesController  extends BaseController {

    @Autowired
    private IspeciesService ispeciesService;

    /**
     * 查询鱼种列表
     */
 //   @PreAuthorize("@ss.hasPermi('agriculture:germplasm:list')")
    @GetMapping("/list")
    public TableDataInfo list(Species species)
    {
        startPage();
        List<Species> list = ispeciesService.selectSpeciesList(species);
        return getDataTable(list);
    }

    /**
     * 导出鱼种列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:germplasm:export')")
    @Log(title = "鱼种", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Species species)
    {
        List<Species> list = ispeciesService.selectSpeciesList(species);
        ExcelUtil<Species> util = new ExcelUtil<Species>(Species.class);
        util.exportExcel(response, list, "鱼种数据");
    }

    /**
     * 获取鱼种详细信息
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:germplasm:query')")
    @GetMapping(value = "/{speciesId}")
    public AjaxResult getInfo(@PathVariable("speciesId") Long speciesId)
    {
        return AjaxResult.success(ispeciesService.selectSpeciesBySpeciesId(speciesId));
    }

    /**
     * 新增鱼种
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:germplasm:add')")
    @Log(title = "鱼种", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Species species)
    {
        return toAjax(ispeciesService.insertSpecies(species));
    }

    /**
     * 修改鱼种
     */
  //  @PreAuthorize("@ss.hasPermi('agriculture:germplasm:edit')")
    @Log(title = "鱼种", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Species species)
    {
        return toAjax(ispeciesService.updateSpecies(species));
    }

    /**
     * 删除鱼种
     */
   // @PreAuthorize("@ss.hasPermi('agriculture:germplasm:remove')")
    @Log(title = "鱼种", businessType = BusinessType.DELETE)
    @DeleteMapping("/{speciesIds}")
    public AjaxResult remove(@PathVariable Long[] speciesIds)
    {
        return toAjax(ispeciesService.deleteSpeciesBySpeciesIds(speciesIds));
    }
}
