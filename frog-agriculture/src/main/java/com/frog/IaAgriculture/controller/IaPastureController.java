package com.frog.IaAgriculture.controller;


import com.frog.agriculture.domain.CostEmployee;
import com.frog.common.annotation.Anonymous;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.frog.IaAgriculture.dto.BaseDTO;
import com.frog.IaAgriculture.dto.IaPastureSensorValuePageDTO;
import com.frog.IaAgriculture.domain.SensorValue;
import com.frog.IaAgriculture.model.IaPasture;
import com.frog.IaAgriculture.service.IaPastureService;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Api(tags = "大棚管理")
@RestController
@Anonymous
@RequestMapping("/iaPasture")
@Validated
public class IaPastureController extends BaseController {

    @Autowired
    private IaPastureService iaPastureService;


    @ApiOperation("添加大棚")
    @PostMapping(value = "/create")
    public ResultVO<IaPasture> create(@Validated @RequestBody IaPasture ivPasture) {
        return this.iaPastureService.create(ivPasture);
    }

    @ApiOperation("删除大棚")
    @PostMapping(value = "/delete")
    public ResultVO delete(@ApiParam(required = true, name = "id", value = "大棚id") @NotBlank(message = "大棚id为空") @RequestParam("id") String id) {
        return this.iaPastureService.delete(id);
    }

    @ApiOperation("修改大棚")
    @PostMapping(value = "/update")
    public ResultVO update(@Validated @RequestBody IaPasture ivPasture) {
        return this.iaPastureService.update(ivPasture);
    }

    @ApiOperation("大棚详情")
    @PostMapping(value = "/detail")
    public ResultVO detail(@ApiParam(required = true, name = "id", value = "大棚id") @NotBlank(message = "大棚id为空") @RequestParam("id") String id) {
        return this.iaPastureService.detail(id);
    }

    @ApiOperation("大棚状态信息")
    @PostMapping(value = "/ivPastureSensorValuePage")
    public ResultVO<SensorValue> ivPastureSensorValuePage(@Validated @RequestBody IaPastureSensorValuePageDTO dto) {
        return this.iaPastureService.ivPastureSensorValuePage(dto);
    }
    @ApiOperation("全部状态信息")
    @PostMapping(value = "/selectSensorValuePage")
    public ResultVO<SensorValue> selectSensorValuePage(@Validated @RequestBody BaseDTO dto) {
        return this.iaPastureService.selectSensorValuePage(dto);
    }

    @ApiOperation("大棚分页列表")
    @PostMapping(value = "/page")
    public ResultVO page(@RequestBody BaseDTO baseDTO) {
        return this.iaPastureService.page(baseDTO);
    }


    //    @PreAuthorize("@ss.hasPermi('agriculture:costEmployee:list')")
//    @GetMapping("/list")
//    public TableDataInfo list(IaPasture iaPasture)
//    {
//        startPage();
//        List<IaPasture> list = iaPastureService.pastureList(iaPasture.getName());
//        return getDataTable(list);
//    }
    @ApiOperation("查询所有大棚,只查询大棚名称和id，用来做下拉框，名称可模糊查询")
    @GetMapping(value = "/list")
    public ResultVO pastureList(@ApiParam(required = false, name = "name", value = "大棚名称") String name) {
        return iaPastureService.pastureList(name);
    }
}
