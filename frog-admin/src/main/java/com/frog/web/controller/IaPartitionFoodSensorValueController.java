package com.frog.web.controller;


import com.frog.IaAgriculture.domain.AgricultureTraceRecord;
import com.frog.IaAgriculture.service.AgricultureTraceRecordService;
import com.frog.common.annotation.Anonymous;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.frog.IaAgriculture.dto.EnvironmentPageDTO;
import com.frog.IaAgriculture.dto.TraceabilityDTO;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.validation.constraints.NotBlank;

@Anonymous
@Api(tags = "溯源信息")
@RestController
@RequestMapping("/iaPartitionFoodSensorValue")
@Validated
public class IaPartitionFoodSensorValueController {

    @Autowired
    private IaPartitionFoodSensorValueService iaPartitionFoodSensorValueService;

    @Autowired
    private AgricultureTraceRecordService agricultureTraceRecordService;


    @GetMapping("/get")
    public ResultVO get() {
        return this.iaPartitionFoodSensorValueService.get();
    }

    @ApiOperation("获取溯源环境信息")
    @PostMapping("/getEnvironmentPage")
    public ResultVO getEnvironmentPage(@Validated @RequestBody EnvironmentPageDTO dto) {
        return this.iaPartitionFoodSensorValueService.getEnvironmentPage(dto);
    }
    @ApiOperation("获取溯源施肥信息")
    @PostMapping("/getFeedingPage")
    public ResultVO getFeedingPage(@Validated @RequestBody EnvironmentPageDTO dto) {
        return this.iaPartitionFoodSensorValueService.getFeedingPage(dto);
    }

    @ApiOperation("获取溯源基础信息")
    @GetMapping("/getTraceability")
    public ResultVO<TraceabilityDTO> getTraceability(@ApiParam(required = true, name = "id", value = "食品溯源id")
                                                     @NotBlank(message = "食品溯源id为空") @RequestParam("id") String id) {
        return this.iaPartitionFoodSensorValueService.getTraceability(id);
    }

    @ApiOperation("手动同步")
    @GetMapping("/sysIaPartitionFoodSensorValueToBlockchain")
    public ResultVO<TraceabilityDTO> sysIaPartitionFoodSensorValueToBlockchain() {
        this.iaPartitionFoodSensorValueService.sysIaPartitionFoodSensorValueToBlockchain();
        return ResultVO.succeed();
    }

    @ApiOperation("记录查询溯源")
    @PostMapping("/addAgricultureTraceRecord")
    public ResultVO addAgricultureTraceRecord(@RequestBody AgricultureTraceRecord agricultureTraceRecord) {
        this.agricultureTraceRecordService.addAgricultureTraceRecord(agricultureTraceRecord);
        return ResultVO.succeed();
    }

    @ApiOperation("查询记录溯源")
    @GetMapping("/getAgricultureTraceRecord")
    public ResultVO getAgricultureTraceRecord() {
        return ResultVO.succeed(this.agricultureTraceRecordService.getAgricultureTraceRecord());
    }
}
