package com.frog.IaAgriculture.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.frog.IaAgriculture.dto.IaPartitionFoodPageDTO;
import com.frog.IaAgriculture.model.IaFeeding;
import com.frog.IaAgriculture.model.IaPartitionFood;
import com.frog.IaAgriculture.service.IaPartitionFoodService;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.validation.constraints.NotBlank;


@Api(tags = "加工管理抽屉管理")
@RestController
@RequestMapping("/iaPartitionFood")
@Validated
public class IaPartitionFoodController {

    @Autowired
    private IaPartitionFoodService iaPartitionFoodService;

    @ApiOperation("添加")
    @PostMapping(value = "/create")
    public ResultVO<IaFeeding> create(@Validated @RequestBody IaPartitionFood ivPasture) {
        return this.iaPartitionFoodService.create(ivPasture);
    }

    //删除
    @ApiOperation("删除")
    @PostMapping(value = "/delete")
    public ResultVO delete(@ApiParam(required = true, name = "id", value = "施肥id")
                           @NotBlank(message = "施肥id为空") @RequestParam("id") String id) {
        return this.iaPartitionFoodService.delete(id);
    }

    //修改
    @ApiOperation("修改")
    @PostMapping(value = "/update")
    public ResultVO update(@Validated @RequestBody IaPartitionFood ivPasture) {
        return this.iaPartitionFoodService.update(ivPasture);
    }

    @ApiOperation("详情")
    @PostMapping(value = "/detail")
    public ResultVO detail(@ApiParam(required = true, name = "id", value = "施肥id")
                           @NotBlank(message = "施肥id为空") @RequestParam("id") String id) {
        return this.iaPartitionFoodService.detail(id);
    }

    @ApiOperation("分页列表")
    @PostMapping(value = "/page")
    public ResultVO page(@RequestBody IaPartitionFoodPageDTO baseDTO) {
        return this.iaPartitionFoodService.page(baseDTO);
    }

}
