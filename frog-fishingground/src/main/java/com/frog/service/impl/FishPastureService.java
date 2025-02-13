package com.frog.service.impl;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.domain.SensorValue;
import com.frog.IaAgriculture.dto.BaseDTO;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.exception.ServerException;
import com.frog.IaAgriculture.mapper.DeviceMapper;
import com.frog.IaAgriculture.mapper.IaPartitionMapper;
import com.frog.IaAgriculture.mapper.SensorValueMapper;
import com.frog.IaAgriculture.model.entity.IaPartition;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;
import com.frog.dto.FishPastureSensorValuePageDTO;
import com.frog.mapper.FishPastureMapper;
import com.frog.model.FishPasture;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.blockchain.agriculture.model.bo.GreenhouseModifyGreenhouseInfoInputBO;
import vip.blockchain.agriculture.model.bo.PlatformAddGreenhouseInfoInputBO;
import vip.blockchain.agriculture.service.GreenhouseService;
import vip.blockchain.agriculture.service.PlatformService;
import vip.blockchain.agriculture.utils.BaseUtil;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Service
public class FishPastureService extends ServiceImpl<FishPastureMapper, FishPasture> {

    @Autowired // 自动装配平台服务
    private PlatformService platformService;
    @Autowired // 自动装配 FISCO BCOS 客户端
    private Client client;
    @Autowired // 自动装配设备映射器
    private DeviceMapper deviceMapper;
    @Autowired // 自动装配传感器值映射器
    private SensorValueMapper sensorValueMapper;
    @Autowired // 自动装配分区映射器
    private IaPartitionMapper iaPartitionMapper;

    @Transactional(rollbackFor = Exception.class) // 开启事务，出现异常时回滚
    public ResultVO create(FishPasture fishPasture) { // 创建鱼棚方法
        fishPasture.setContractAddr(null); // 清除合同地址
        Device device = deviceMapper.selectById(fishPasture.getDeviceId()); // 根据设备 ID 查找设备
        if (Objects.isNull(device)) { // 如果设备不存在
            return ResultVO.failed("设备不存在"); // 返回失败结果
        }
        // 1. 将原有的设备绑定关系解除
        UpdateWrapper<Device> updateWrapper = new UpdateWrapper<Device>(); // 创建更新包装器
        updateWrapper.eq("id", device.getId()).set("pasture_id", null); // 设置条件和更新内容
        deviceMapper.update(updateWrapper); // 更新设备绑定关系

        FishPasture insertBean = new FishPasture(); // 创建新的 FishPasture 实体
        BeanUtils.copyProperties(fishPasture, insertBean); // 复制属性
        insertBean.setId(BaseUtil.getSnowflakeId()); // 设置 ID
        super.save(insertBean); // 保存鱼场实例

        // 2. 绑定设备
        device.setPastureId(insertBean.getId()); // 将设备绑定到新鱼棚
        deviceMapper.updateById(device); // 更新设备

        try {
            // 创建温室信息输入对象
            PlatformAddGreenhouseInfoInputBO input = new PlatformAddGreenhouseInfoInputBO();
            input.set_greenhouseArea(insertBean.getArea()); // 设置温室面积
            input.set_notes(insertBean.getDescription()); // 设置备注
            input.set_greenhouseName(insertBean.getName()); // 设置温室名称
            input.set_greenhousePosition(insertBean.getAddress()); // 设置温室地址
            input.set_maxPartitionQuantity(BigInteger.valueOf(insertBean.getBigBreedingQuantity())); // 设置最大分区数量

            // 调用平台服务添加温室信息
            TransactionResponse transactionResponse = platformService.addGreenhouseInfo(input);
            if (Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) {
                // 如果响应成功
                String values = transactionResponse.getValues();
                if (StringUtils.isBlank(values)) { // 如果合同地址为空
                    throw new ServerException("合约地址不存在"); // 抛出服务器异常
                }
                String contractAddr = BaseUtil.parseContractAddr(values); // 解析合同地址
                if (StringUtils.isBlank(contractAddr)) { // 如果解析后的地址为空
                    throw new ServerException(ErrorCodeEnum.CONTENT_PARSE_ADDR_ERROR); // 抛出解析错误异常
                }
                insertBean.setContractAddr(contractAddr); // 设置合同地址
            } else {
                throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器错误异常
            }
            super.updateById(insertBean); // 更新鱼棚信息
        } catch (Exception e) { // 捕获异常
            e.printStackTrace(); // 打印异常
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器错误异常
        }
        return ResultVO.succeed(insertBean); // 返回成功结果
    }

    @Transactional(rollbackFor = Exception.class) // 开启事务，出现异常时回滚
    public ResultVO delete(String id) { // 删除鱼棚方法
        super.removeById(id); // 根据 ID 删除鱼棚
        return ResultVO.succeed(); // 返回成功结果
    }

    @Transactional(rollbackFor = Exception.class) // 开启事务，出现异常时回滚
    public ResultVO update(FishPasture fishPasture) { // 更新鱼棚方法
        Device device = deviceMapper.selectById(fishPasture.getDeviceId()); // 根据设备 ID 查找设备
        if (Objects.isNull(device)) { // 如果设备不存在
            return ResultVO.failed("设备不存在"); // 返回失败结果
        }
        fishPasture.setContractAddr(null); // 清除合同地址
        FishPasture ivPasture1 = this.baseMapper.selectById(fishPasture.getId()); // 查找要更新的鱼棚
        if (Objects.isNull(ivPasture1)) { // 如果鱼棚不存在
            return ResultVO.failed(); // 返回失败结果
        }
        // 1. 删除所有绑定当前鱼棚的设备
        UpdateWrapper<Device> updateWrapper = new UpdateWrapper<Device>(); // 创建更新包装器
        updateWrapper.eq("pasture_id", ivPasture1.getId()); // 设置条件
        updateWrapper.set("pasture_id", null); // 解除绑定
        deviceMapper.update(null, updateWrapper); // 更新设备绑定关系
        super.updateById(fishPasture); // 更新鱼棚信息

        // 2. 绑定设备
        device.setPastureId(fishPasture.getId()); // 绑定设备到新的鱼棚
        deviceMapper.updateById(device); // 更新设备信息

        try {
            // 创建温室服务对象
            GreenhouseService pastureService = new GreenhouseService(client, client.getCryptoSuite().getCryptoKeyPair(), ivPasture1.getContractAddr());

            // 创建温室信息修改输入对象
            GreenhouseModifyGreenhouseInfoInputBO input = new GreenhouseModifyGreenhouseInfoInputBO();
            input.set_greenhouseArea(fishPasture.getArea()); // 设置温室面积
            input.set_notes(fishPasture.getDescription()); // 设置备注
            input.set_greenhouseName(fishPasture.getName()); // 设置温室名称
            input.set_greenhousePosition(fishPasture.getAddress()); // 设置温室地址
            input.set_maxPartitionQuantity(BigInteger.valueOf(fishPasture.getBigBreedingQuantity())); // 设置最大分区数量

            // 调用温室服务修改温室信息
            TransactionResponse transactionResponse = pastureService.modifyGreenhouseInfo(input);
            if (!Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) {
                // 如果响应不是成功
                throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器错误异常
            }
        } catch (Exception e) { // 捕获异常
            e.printStackTrace(); // 打印异常
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器错误异常
        }
        return ResultVO.succeed(); // 返回成功结果
    }

    public ResultVO detail(String id) { // 获取鱼棚详情方法
        FishPasture ivPasture = this.baseMapper.selectById(id); // 根据 ID 查找鱼棚
        List<Device> devices = this.deviceMapper.selectList(new LambdaQueryWrapper<Device>().eq(Device::getPastureId, ivPasture.getId())); // 查询绑定该鱼棚的设备
        ivPasture.setDevices(devices); // 设置设备列表
        return ResultVO.succeed(ivPasture); // 返回鱼棚详情
    }

    public ResultVO page(BaseDTO baseDTO) { // 分页获取鱼棚列表方法

        // 创建查询包装器
        LambdaQueryWrapper<FishPasture> qw = new LambdaQueryWrapper<>();
        if (StringUtils.isNoneBlank(baseDTO.getKeyword())) { // 如果有关键词
            qw.and(wrapper ->
                    wrapper.like(FishPasture::getName, baseDTO.getKeyword()) // 按照鱼棚名模糊查询
                            .or()
                            .like(FishPasture::getId, baseDTO.getKeyword()) // 或按照 ID 模糊查询
            );
        }

        // 创建分页对象
        Page<FishPasture> page = new Page<>(baseDTO.getCurrentPage(), baseDTO.getPageSize());
        Page<FishPasture> p = this.baseMapper.selectPage(page, qw); // 查询分页结果
        p.getRecords().forEach(bean -> { // 遍历每个记录
            Long count = iaPartitionMapper.selectCount( // 查询当前鱼棚的分区数量
                    new LambdaQueryWrapper<IaPartition>().eq(IaPartition::getPastureId, bean.getId()).eq(IaPartition::getProcessState, 0) // 条件过滤
            );
            bean.setBreedingQuantity(count); // 设置繁殖数量
            // 获取最新一条环境信息返回
            SensorValue lastDateOne = this.sensorValueMapper.getLastDateOne(bean.getId()); // 获取最新的传感器值
            if (Objects.nonNull(lastDateOne)) { // 如果存在传感器值
                bean.setPhValue(String.valueOf(lastDateOne.getTemperature())); // 设置ph
                bean.setDissolvedOxygen(String.valueOf(lastDateOne.getHumidity())); // 设置含氧量
                bean.setNitriteNitrogen(String.valueOf(lastDateOne.getAirquality())); // 设置亚硝酸盐
            //    bean.setPressure(lastDateOne.getPressure()); // 设置压力
            }
        });

        return ResultVO.succeed(p); // 返回结果
    }

    public ResultVO fishPastureSensorValuePage(FishPastureSensorValuePageDTO dto) { // 分页获取鱼棚传感器值方法
        LambdaQueryWrapper<SensorValue> qw = new LambdaQueryWrapper<>(); // 创建查询包装器
        qw.eq(SensorValue::getPastureId, dto.getPastureId()); // 设置鱼棚 ID 条件

        if (!StringUtils.isBlank(dto.getStartTime())) { // 如果起始时间不为空
            qw.ge(SensorValue::getDateTime, dto.getStartTime()); // 设置开始时间条件
        }
        if (!StringUtils.isBlank(dto.getEndTime())) { // 如果结束时间不为空
            qw.le(SensorValue::getDateTime, dto.getEndTime()); // 设置结束时间条件
        }

        // 创建分页对象
        Page<SensorValue> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        OrderItem orderItem = new OrderItem(); // 创建排序对象
        orderItem.setColumn("date"); // 设置排序字段为 date
        orderItem.setAsc(false); // 按降序排序
        page.addOrder(orderItem); // 添加排序规则

        // 查询传感器值的分页结果
        Page<SensorValue> sensorValuePage = this.sensorValueMapper.selectPage(page, qw);
        return ResultVO.succeed(sensorValuePage); // 返回结果
    }

    public ResultVO selectSensorValuePage(BaseDTO dto) { // 获取传感器值分页方法

        // 创建分页对象并设置分页参数
        Page<SensorValue> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());

        // 设置排序规则，按 dateTime 字段倒序排序，确保获取最新的数据
        OrderItem orderItem = new OrderItem();
        orderItem.setColumn("date"); // 将排序字段设为 date
        orderItem.setAsc(false); // 按降序排序
        page.addOrder(orderItem); // 添加排序规则

        // 查询所有数据，按分页返回
        Page<SensorValue> sensorValuePage = this.sensorValueMapper.selectPage(page, new LambdaQueryWrapper<>());

        // 返回结果
        return ResultVO.succeed(sensorValuePage); // 返回结果
    }
    //**
    public ResultVO pastureList(String name) { // 获取鱼棚列表方法
        LambdaQueryWrapper<FishPasture> fishPasturesWrapper = new LambdaQueryWrapper<FishPasture>().select(FishPasture::getName, FishPasture::getId); // 创建查询包装器
        if (!StrUtil.isBlank(name)) { // 如果名称不为空
            fishPasturesWrapper.like(FishPasture::getName, name); // 按名称模糊查询
        }
        return ResultVO.succeed(baseMapper.selectList(fishPasturesWrapper)); // 返回结果
    }
}
