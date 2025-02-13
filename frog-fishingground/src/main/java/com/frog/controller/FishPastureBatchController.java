package com.frog.controller;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.IaAgriculture.domain.SensorValue;
import com.frog.IaAgriculture.dto.BaseDTO;
import com.frog.IaAgriculture.model.IaPasture;
import com.frog.IaAgriculture.vo.ResultVO;
import com.frog.common.annotation.Anonymous;
import com.frog.common.core.controller.BaseController;
import com.frog.dto.FishPastureSensorValuePageDTO;
import com.frog.model.FishPasture;
import com.frog.service.FishPastureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@Api(tags = "鱼棚管理")
@RestController
@Anonymous
@RequestMapping("/fishPasture")
@Validated
public class FishPastureBatchController extends BaseController {

    @Autowired
    private FishPastureService fishPastureService;

    @ApiOperation("添加鱼棚")
    @PostMapping(value = "/create")
    public ResultVO<IaPasture> create(@Validated @RequestBody FishPasture fishPasture) {
        return this.fishPastureService.create(fishPasture);
    }

    @ApiOperation("删除鱼棚")
    @PostMapping(value = "/delete")
    public ResultVO delete(@ApiParam(required = true, name = "id", value = "鱼棚id") @NotBlank(message = "鱼棚id为空") @RequestParam("id") String id) {
        return this.fishPastureService.delete(id);
    }

    @ApiOperation("修改鱼棚")
    @PostMapping(value = "/update")
    public ResultVO update(@Validated @RequestBody FishPasture fishPasture) {
        return this.fishPastureService.update(fishPasture);
    }

    @ApiOperation("鱼棚详情")
    @PostMapping(value = "/detail")
    public ResultVO detail(@ApiParam(required = true, name = "id", value = "鱼棚id") @NotBlank(message = "鱼棚id为空") @RequestParam("id") String id) {
        return this.fishPastureService.detail(id);
    }

    @ApiOperation("鱼棚状态信息")
    @PostMapping(value = "/fishPastureSensorValuePage")
    public ResultVO<SensorValue> fishPastureSensorValuePage(@Validated @RequestBody FishPastureSensorValuePageDTO dto) {
        return this.fishPastureService.fishPastureSensorValuePage(dto);
    }
    @ApiOperation("全部状态信息")
    @PostMapping(value = "/selectSensorValuePage")
    public ResultVO<SensorValue> selectSensorValuePage(@Validated @RequestBody BaseDTO dto) {
        return this.fishPastureService.selectSensorValuePage(dto);
    }

    @ApiOperation("鱼棚分页列表")
    @PostMapping(value = "/page")
    public ResultVO page(@RequestBody BaseDTO baseDTO) {
        return this.fishPastureService.page(baseDTO);
    }



    @ApiOperation("查询所有鱼棚,只查询鱼棚名称和id，用来做下拉框，名称可模糊查询")
    @GetMapping(value = "/list")
    public ResultVO pastureList(@ApiParam(required = false, name = "name", value = "鱼棚名称") String name) {
        return fishPastureService.pastureList(name);
    }
}
