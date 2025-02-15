package com.frog.controller;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.IaAgriculture.dto.IaPartitionFoodPageDTO;
import com.frog.IaAgriculture.model.IaFeeding;
import com.frog.IaAgriculture.model.IaPartitionFood;
import com.frog.IaAgriculture.service.IaPartitionFoodService;
import com.frog.IaAgriculture.vo.ResultVO;
import com.frog.common.core.controller.BaseController;
import com.frog.dto.FishPartitionFoodPageDTO;
import com.frog.model.FishPartitionFood;
import com.frog.service.impl.FishPartitionFoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@Api(tags = "鱼 捕捞(采摘)管理")
@RestController
@RequestMapping("/fishPasture/fishPartitionFood")
@Validated
public class FishPartitionFoodController {
    @Autowired
    private FishPartitionFoodService fishPartitionFoodService;

    @ApiOperation("添加")
    @PostMapping(value = "/create")
    public ResultVO<IaFeeding> create(@Validated @RequestBody FishPartitionFood ivPasture) {
        return this.fishPartitionFoodService.create(ivPasture);
    }

    //删除
    @ApiOperation("删除")
    @PostMapping(value = "/delete")
    public ResultVO delete(@ApiParam(required = true, name = "id", value = "施肥id")
                           @NotBlank(message = "施肥id为空") @RequestParam("id") String id) {
        return this.fishPartitionFoodService.delete(id);
    }

    //修改
    @ApiOperation("修改")
    @PostMapping(value = "/update")
    public ResultVO update(@Validated @RequestBody FishPartitionFood ivPasture) {
        return this.fishPartitionFoodService.update(ivPasture);
    }

    @ApiOperation("详情")
    @PostMapping(value = "/detail")
    public ResultVO detail(@ApiParam(required = true, name = "id", value = "施肥id")
                           @NotBlank(message = "施肥id为空") @RequestParam("id") String id) {
        return this.fishPartitionFoodService.detail(id);
    }

    @ApiOperation("分页列表")
    @PostMapping(value = "/page")
    public ResultVO page(@RequestBody FishPartitionFoodPageDTO baseDTO) {
        return this.fishPartitionFoodService.page(baseDTO);
    }
}
