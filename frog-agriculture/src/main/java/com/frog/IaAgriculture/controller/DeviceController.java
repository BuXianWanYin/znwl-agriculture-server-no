package com.frog.IaAgriculture.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frog.common.annotation.Anonymous;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.service.DeviceService;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.annotation.Resource;

/**
 * @ Description   :  设备管理
 * @ Author        :
 * @ CreateDate    :  2024/10/11 20:51
 * @ Version       :  1.0
 */
@Anonymous
@RestController
@RequestMapping("/device")
@Api(tags = "设备管理")
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    @PostMapping(value = "/add")
    @ApiOperation("添加设备")
    public ResultVO addDevice(@RequestBody Device device) {
        if (device == null || !StrUtil.isAllNotBlank(device.getDeviceId(),device.getDeviceName())) {
            return ResultVO.failed(ErrorCodeEnum.PARAMETER_ERROR);
        }
        return deviceService.addDevice(device);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation("删除设备")
    public ResultVO deleteDevice(@ApiParam(value = "id", required = true) @RequestParam String id) {
        if (StrUtil.isBlank(id)) {
            return ResultVO.failed(ErrorCodeEnum.PARAMETER_ERROR);
        }
        return deviceService.delete(id);
    }

    @GetMapping(value = "/list")
    @ApiOperation("设备列表")
    public ResultVO deviceList(@ApiParam(value = "id", required = false)String id,
                               @ApiParam(value = "设备状态", required = false)Integer state,
                               @ApiParam(value = "当前页数", required = false)Integer page,
                               @ApiParam(value = "总页数", required = false)Integer pageSize) {
        if (page == null) {
            page = CommonContant.DEFAULT_CURRENT_PAGE;
        }
        if (pageSize == null) {
            pageSize = CommonContant.DEFAULT_PAGE_SIZE;
        }
        return deviceService.deviceList(id,state,page,pageSize);
    }

    @ApiIgnore
    @PutMapping(value = "/update")
    @ApiOperation("更新设备")
    public ResultVO deviceUpdate(@ApiParam(value = "id", required = true) @RequestParam String id,
                                 @ApiParam(value = "设备名称", required = false) @RequestParam(required = false) String deviceName ,
                                 @ApiParam(value = "备注", required = false) @RequestParam(required = false) String remark) {

        if (StrUtil.isBlank(id)) {
            return ResultVO.failed(ErrorCodeEnum.PARAMETER_ERROR);
        }
        return deviceService.deviceUpdate(id,deviceName,remark);
    }

    @GetMapping(value = "/listAll")
    @ApiOperation("所有设备，大棚添加修改下拉使用")
    public ResultVO deviceList() {
        return ResultVO.succeed(deviceService.list(new LambdaQueryWrapper<Device>().select(Device::getId, Device::getDeviceName)));
    }
}
