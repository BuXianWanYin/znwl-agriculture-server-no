package com.frog.IaAgriculture.controller;


import com.frog.common.annotation.Anonymous;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.frog.IaAgriculture.dto.BaseDTO;
import com.frog.IaAgriculture.model.IaFeeding;
import com.frog.IaAgriculture.service.IaFeedingService;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.validation.constraints.NotBlank;

@Anonymous
@Api(tags = "施肥管理")
@RestController
@RequestMapping("/iaFeeding")
@Validated
public class IaFeedingController {

    @Autowired
    private IaFeedingService iaFeedingService;

    @ApiOperation("添加")
    @PostMapping(value = "/create")
    public ResultVO<IaFeeding> create(@Validated @RequestBody IaFeeding ivPasture) {
        return this.iaFeedingService.create(ivPasture);
    }

    //删除
    @ApiOperation("删除")
    @PostMapping(value = "/delete")
    public ResultVO delete(@ApiParam(required = true, name = "id", value = "施肥id")
                           @NotBlank(message = "施肥id为空") @RequestParam("id") String id) {
        return this.iaFeedingService.delete(id);
    }

    //修改
    @ApiOperation("修改")
    @PostMapping(value = "/update")
    public ResultVO update(@Validated @RequestBody IaFeeding ivPasture) {
        return this.iaFeedingService.update(ivPasture);
    }

    @ApiOperation("详情")
    @PostMapping(value = "/detail")
    public ResultVO detail(@ApiParam(required = true, name = "id", value = "施肥id")
                           @NotBlank(message = "施肥id为空") @RequestParam("id") String id) {
        return this.iaFeedingService.detail(id);
    }

    @ApiOperation("分页列表")
    @PostMapping(value = "/page")
    public ResultVO page(@RequestBody BaseDTO baseDTO) {
        return this.iaFeedingService.page(baseDTO);
    }

}
