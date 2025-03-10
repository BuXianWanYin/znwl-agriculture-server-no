package com.frog.IaAgriculture.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frog.common.utils.StringUtils;
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
public class DeviceService extends ServiceImpl<DeviceMapper, Device> { // DeviceService继承ServiceImpl，实现设备Service逻辑

    @Resource // 注入PlatformService类的Bean对象
    PlatformService platformService; // 用于平台交互的服务

    @Resource
    DeviceMapper deviceMapper; // 定义设备映射器，用于数据库操作

    @Transactional(rollbackFor = Exception.class) // 开启事务管理，出现异常时回滚事务
    public ResultVO addDevice(Device device) { // 添加设备方法，接收Device对象作为参数

        // 存在性校验
        if (baseMapper.exists(new LambdaQueryWrapper<Device>().eq(Device::getDeviceId, device.getDeviceId()))) {
            return ResultVO.failed(ErrorCodeEnum.DATA_ALREADY_EXIST);
        }

        // 新增设备类型判断逻辑
        if (isFishPastureDevice(device)) { // 鱼棚设备
            device.setPastureId(null);     // 清空大棚相关字段
            device.setBatchId(null);//清空大棚批次字段



        } else if (isVegetablePastureDevice(device)) { // 蔬菜大棚设备
            device.setFishPastureId(null);       // 清空鱼棚相关字段
            device.setFishPastureBatchId(null);
        }
        PlatformAddCollectorInputBO pa = new PlatformAddCollectorInputBO(); // 新建平台添加采集器输入对象
        pa.set_collectorId(device.getDeviceId()); // 设置采集器ID为设备的deviceId
        try { // 开始尝试调用平台服务添加采集器
            TransactionResponse transactionResponse = platformService.addCollector(pa); // 调用平台服务，添加采集器并获取交易响应
            device.setId(BaseUtil.getSnowflakeId()); // 使用雪花算法生成一个唯一ID，并设置给设备对象
            device.setDate(new Date()); // 设置当前日期时间到设备对象
            device.setAddress(transactionResponse.getTransactionReceipt().getFrom()); // 从交易回执中获取发送方地址，并设置到设备对象

            baseMapper.insert(device); // 将设备对象插入数据库中
            // 检查交易回执信息是否与预期相同，不符合则抛出异常
            if (!transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) { // 如果交易回执消息不等于预期成功信息
                throw new RuntimeException(); // 抛出运行时异常以触发事务回滚
            }
        } catch (Exception e) { // 捕获异常情况
            throw new RuntimeException(e); // 抛出运行时异常，并携带捕获到的异常信息
        }

        return ResultVO.succeed(); // 操作成功时返回成功响应
    }


    // 判断是否为鱼棚设备的私有方法
    private boolean isFishPastureDevice(Device device) {
        return StringUtils.isNotBlank(device.getFishPastureId());
    }

    // 判断是否为蔬菜大棚设备的私有方法
    private boolean isVegetablePastureDevice(Device device) {
        return StringUtils.isNotBlank(device.getPastureId());
    }

    @Transactional(rollbackFor = Exception.class) // 开启事务管理，出现异常时回滚事务
    public ResultVO delete(String id) { // 删除设备方法，接收设备ID作为参数
        Device device = baseMapper.selectById(id); // 根据设备ID从数据库中查询设备对象
        baseMapper.deleteById(id); // 根据设备ID从数据库中删除设备对象

        PlatformRemoveCollectorInputBO pb = new PlatformRemoveCollectorInputBO(); // 新建平台移除采集器输入对象
        pb.set_collectorId(device.getDeviceId()); // 设置采集器ID为设备的deviceId
        try { // 尝试调用平台服务移除采集器
            TransactionResponse transactionResponse = platformService.removeCollector(pb); // 调用平台服务，移除采集器并获取交易响应
            // 检查交易回执信息是否与预期相同，不符合则抛出异常
            if (!transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) { // 如果交易回执消息不等于预期成功信息
                throw new RuntimeException(); // 抛出运行时异常以触发事务回滚
            }
        } catch (Exception e) { // 捕获异常情况
            throw new RuntimeException(e); // 抛出运行时异常，并携带捕获到的异常信息
        }
        return ResultVO.succeed(); // 操作成功时返回成功响应
    }

    public ResultVO deviceList(String id, Integer state, Integer page, Integer pageSize) { // 查询设备列表方法，接收设备ID、状态、页码及每页数量作为参数
        Page<Device> pageInfo = new Page<>(page, pageSize); // 新建分页对象，指定当前页和页大小
        LambdaQueryWrapper<Device> queryWrapper = Wrappers.lambdaQuery(); // 新建Lambda查询条件包装器

        // 如果设备ID不为空，则添加ID等于查询条件
        if (StrUtil.isNotBlank(id)) { // 判断设备ID是否不为空白
            queryWrapper.eq(Device::getId, id); // 设置查询条件：设备 ID 相等
        }
        // 如果状态不为null，则添加状态等于查询条件
        if (state != null) { // 判断设备状态是否不为null
            queryWrapper.eq(Device::getStatus, state); // 设置查询条件：设备状态相等
        }
        Page<Device> resultPage = baseMapper.selectPage(pageInfo, queryWrapper); // 根据分页对象和查询条件从数据库中查询设备列表
        return ResultVO.succeed(resultPage); // 返回查询到的分页设备数据，并封装成成功结果
    }

    public ResultVO deviceUpdate(String id, String deviceName, String remark) { // 更新设备方法，接收设备ID、名称与备注作为参数

        LambdaUpdateWrapper<Device> updateWrapper = new LambdaUpdateWrapper<>(); // 新建Lambda更新条件包装器
        updateWrapper.eq(Device::getId, id); // 设置更新条件，设备ID相等
        // 如果设备名称非空且非空字符串，则设置设备名称更新
        if (deviceName != null && !deviceName.isEmpty()) { // 判断设备名称是否不为null且不为空字符串
            updateWrapper.set(Device::getDeviceName, deviceName); // 设置更新字段：设备名称
        }
        // 如果备注非空且非空字符串，则设置备注更新
        if (remark != null && !remark.isEmpty()) { // 判断备注是否不为null且不为空字符串
            updateWrapper.set(Device::getRemark, remark); // 设置更新字段：备注
        }
        int updateCount = baseMapper.update(null, updateWrapper); // 根据更新条件包装器更新设备记录，并返回更新数量
        if (updateCount > 0) { // 如果更新数量大于0，则表示更新成功
            return ResultVO.succeed("更新成功"); // 返回成功响应，并提示更新成功
        } else { // 如果更新数量不大于0，可能是因为找不到对应ID
            return ResultVO.failed("更新失败，ID不存在"); // 返回失败响应，并提示更新失败
        }
    }

    public ResultVO deviceUpdate(Device device) {
        if (StrUtil.isBlank(device.getId())) {
            return ResultVO.failed(ErrorCodeEnum.PARAMETER_ERROR);
        }

        // 直接根据id更新整个实体
        int updateCount = baseMapper.updateById(device);

        return updateCount > 0
                ? ResultVO.succeed("更新成功")
                : ResultVO.failed("更新失败，ID不存在");
    }


}