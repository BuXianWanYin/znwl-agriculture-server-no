package com.frog.IaAgriculture.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.mapper.DeviceMapper;
import vip.blockchain.agriculture.model.bo.PlatformAddCollectorInputBO;
import vip.blockchain.agriculture.model.bo.PlatformRemoveCollectorInputBO;
import vip.blockchain.agriculture.service.PlatformService;
import vip.blockchain.agriculture.utils.BaseUtil;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 设备service
 */
@Service
public class DeviceService extends ServiceImpl<DeviceMapper, Device> {

    @Resource
    PlatformService platformService;
    @Resource
    DeviceMapper deviceMapper;

    @Transactional(rollbackFor = Exception.class)
    public ResultVO addDevice(Device device) {

        if (baseMapper.exists(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, device.getDeviceId()))) {
            return ResultVO.failed(ErrorCodeEnum.DATA_ALREADY_EXIST);
        }

        PlatformAddCollectorInputBO pa = new PlatformAddCollectorInputBO();
        pa.set_collectorId(device.getDeviceId());
        try {
            TransactionResponse transactionResponse = platformService.addCollector(pa);
            device.setId(BaseUtil.getSnowflakeId());
            device.setDate(new Date());
            device.setAddress(transactionResponse.getTransactionReceipt().getFrom());
            baseMapper.insert(device);
            if (!transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResultVO.succeed();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO delete(String id) {
        Device device = baseMapper.selectById(id);
        baseMapper.deleteById(id);

        PlatformRemoveCollectorInputBO pb  =  new PlatformRemoveCollectorInputBO();
        pb.set_collectorId(device.getDeviceId());
        try {
            TransactionResponse transactionResponse = platformService.removeCollector(pb);
            if (!transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultVO.succeed();
    }

    public ResultVO deviceList(String id, Integer state, Integer page, Integer pageSize) {
        Page<Device> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Device> queryWrapper = Wrappers.lambdaQuery();

        if (StrUtil.isNotBlank(id)) {
            queryWrapper.eq(Device::getId, id);
        }
        if (state != null) {
            queryWrapper.eq(Device::getStatus, state);
        }
        Page<Device> resultPage = baseMapper.selectPage(pageInfo, queryWrapper);
        return ResultVO.succeed(resultPage);
    }

    public ResultVO deviceUpdate(String id, String deviceName, String remark) {

        LambdaUpdateWrapper<Device> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Device::getId, id);
        if (deviceName != null && !deviceName.isEmpty()) {
            updateWrapper.set(Device::getDeviceName, deviceName);
        }
        if (remark != null && !remark.isEmpty()) {
            updateWrapper.set(Device::getRemark, remark);
        }
        int updateCount = baseMapper.update(null, updateWrapper);
        if (updateCount > 0) {
            return ResultVO.succeed("更新成功");
        } else {
            return ResultVO.failed("更新失败，ID不存在");
        }
    }

    public void updateDevice(Device device) {
        int result = deviceMapper.updateDevice(device);
        if (result == 0) {
            throw new RuntimeException("更新失败：未找到对应的设备 ID");
        }
        System.out.println("设备更新成功");
    }
}
