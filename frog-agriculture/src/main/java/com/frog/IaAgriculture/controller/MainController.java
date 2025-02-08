package com.frog.IaAgriculture.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.service.MainService;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.annotation.Resource;

/**
 * @ Description   :  首页展示统计控制器
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/13 11:43
 * @ Version       :  1.0
 */
@RestController
@RequestMapping("/main")
@Api(tags = "首页展示相关接口")
public class MainController {

    @Resource
    MainService mainService;

    @GetMapping(value = "/quantity")
    @ApiOperation(value = "获取数量统计", notes = "返回大棚数量pastureNum、未成熟数量rawNum、成熟数量ripeNum、设备数量的统计devNum")
    public ResultVO allQuantityList() {
        return mainService.allQuantityList();
    }

    @GetMapping(value = "/varietyStatistics")
    @ApiOperation(value = "总类饼图", notes = "返回的是LIST的variety种类名称，count 数量")
    public ResultVO varietyStatistics() {
        return mainService.varietyStatistics();
    }

    @GetMapping(value = "/monthlyRipe")
    @ApiOperation(value = "月成熟折线图", notes = "返回的是LIST的month月份，count 数量")
    public ResultVO monthlyOutFence(@ApiParam(value = "年份", required = true) Integer year) {
        if (year == null) {
            return ResultVO.failed(ErrorCodeEnum.PARAMETER_ERROR);
        }
        return mainService.monthlyOutFence(year);
    }

    @GetMapping(value = "/monthlyProcessCount")
    @ApiOperation(value = "月加工量", notes = "返回的是LIST的month月份，count 数量")
    public ResultVO monthlyProcessCount(@ApiParam(value = "年份", required = true) Integer year) {
        if (year == null) {
            return ResultVO.failed(ErrorCodeEnum.PARAMETER_ERROR);
        }
        return mainService.monthlyProcessCount(year);
    }

}
