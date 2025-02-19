package com.frog.controller;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import com.frog.common.utils.StringUtils;
import com.frog.domain.PastureBatch;
import com.frog.service.FishShedPartitionblockService;
import com.frog.service.PastureBatchService;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.transaction.model.exception.TransactionBaseException;
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

import static java.lang.Math.abs;

/**
 * 鱼物批次Controller
 *
 * @author aj
 * @date 2024-2-13
 */
@RestController
@RequestMapping("/fishPasture/batch")
public class PastureBatchController extends BaseController {

    @Autowired
    private PastureBatchService pastureBatchService;

//    @Autowired
    /**
     * 查询鱼物批次列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(PastureBatch pastureBatch) {
        startPage();
        List<PastureBatch> list = pastureBatchService.selectPastureBatchList(pastureBatch);
        return getDataTable(list);
    }


    /**
     * 导出鱼物批次列表
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:export')")
    @Log(title = "鱼物批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PastureBatch pastureBatch) {
        List<PastureBatch> list = pastureBatchService.selectPastureBatchList(pastureBatch);
        ExcelUtil<PastureBatch> util = new ExcelUtil<PastureBatch>(PastureBatch.class);
        util.exportExcel(response, list, "鱼物批次数据");
    }

    /**
     * 获取鱼物批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:query')")
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable("batchId") Long batchId) {
        return AjaxResult.success(pastureBatchService.selectPastureBatchByBatchId(batchId));
    }

    /**
     * 新增鱼物批次
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:add')")
    @Log(title = "鱼物批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PastureBatch pastureBatch) throws ABICodecException, TransactionBaseException, IOException,RuntimeException {

        return toAjax(pastureBatchService.insertPastureBatch(pastureBatch));
    }

    /**
     * 修改鱼物批次
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:edit')")
    @Log(title = "鱼物批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PastureBatch pastureBatch) {
        return toAjax(pastureBatchService.updatePastureBatch(pastureBatch));
    }

    /**
     * 删除鱼物批次
     */
    @PreAuthorize("@ss.hasPermi('agriculture:batch:remove')")
    @Log(title = "鱼物批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{batchId}")
    public AjaxResult remove(@PathVariable Long batchId) {

        return toAjax(pastureBatchService.deletePastureBatchByBatchIds(batchId));
    }

    /**
     * 手机端鱼物批次列表
     */
    @GetMapping("/mobilelist")
    public TableDataInfo mobileList(PastureBatch pastureBatch) {
        startPage();
        List<PastureBatch> list = pastureBatchService.selectPastureBatchListToMobile(pastureBatch);
        return getDataTable(list);
    }
}
