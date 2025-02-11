package com.frog.IaAgriculture.service;

import cn.hutool.core.util.StrUtil; // Hutool 工具类
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; //  MyBatis-Plus 的 Lambda 查询包装器
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper; // MyBatis-Plus 的更新包装器
import com.baomidou.mybatisplus.core.metadata.OrderItem; //  MyBatis-Plus 的订单项
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; //  MyBatis-Plus 的分页工具
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; //  MyBatis-Plus 的服务实现类
import org.apache.commons.lang3.StringUtils; //  Apache Commons 的字符串工具类
import org.fisco.bcos.sdk.client.Client; //  FISCO BCOS 客户端
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse; // 交易响应 DTO
import org.springframework.beans.BeanUtils; //  Spring 的 Bean 复制工具
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.frog.IaAgriculture.dto.BaseDTO; // 基础 DTO
import com.frog.IaAgriculture.dto.ErrorCodeEnum; // 引入错误代码枚举
import com.frog.IaAgriculture.dto.IaPastureSensorValuePageDTO; // 牧场传感器值分页 DTO
import com.frog.IaAgriculture.domain.Device; // 设备实体类
import com.frog.IaAgriculture.domain.SensorValue; // 传感器值实体类
import com.frog.IaAgriculture.exception.ServerException; // 服务器异常类
import com.frog.IaAgriculture.mapper.DeviceMapper; // 设备映射器
import com.frog.IaAgriculture.mapper.IaPartitionMapper; // 分区映射器
import com.frog.IaAgriculture.mapper.IaPastureMapper; // 牧场映射器
import com.frog.IaAgriculture.mapper.SensorValueMapper; // 传感器值映射器
import com.frog.IaAgriculture.model.IaPasture; // 牧场模型
import vip.blockchain.agriculture.model.bo.GreenhouseModifyGreenhouseInfoInputBO; // 温室信息修改输入BO
import vip.blockchain.agriculture.model.bo.PlatformAddGreenhouseInfoInputBO; // 平台添加温室信息输入BO
import com.frog.IaAgriculture.model.entity.IaPartition; // 分区实体类
import vip.blockchain.agriculture.service.GreenhouseService; // 温室服务
import vip.blockchain.agriculture.service.PlatformService; // 平台服务
import vip.blockchain.agriculture.utils.*; // 工具类
import com.frog.IaAgriculture.vo.CommonContant; // 公共常量
import com.frog.IaAgriculture.vo.ResultVO; // 结果视图对象

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Service
public class IaPastureService extends ServiceImpl<IaPastureMapper, IaPasture> { // 定义 IaPastureService 类，继承自 ServiceImpl

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
    public ResultVO create(IaPasture iaPasture) { // 创建牧场方法
        iaPasture.setContractAddr(null); // 清除合同地址
        Device device = deviceMapper.selectById(iaPasture.getDeviceId()); // 根据设备 ID 查找设备
        if (Objects.isNull(device)) { // 如果设备不存在
            return ResultVO.failed("设备不存在"); // 返回失败结果
        }
        // 1. 将原有的设备绑定关系解除
        UpdateWrapper<Device> updateWrapper = new UpdateWrapper<Device>(); // 创建更新包装器
        updateWrapper.eq("id", device.getId()).set("pasture_id", null); // 设置条件和更新内容
        deviceMapper.update(updateWrapper); // 更新设备绑定关系

        IaPasture insertBean = new IaPasture(); // 创建新的 IaPasture 实体
        BeanUtils.copyProperties(iaPasture, insertBean); // 复制属性
        insertBean.setId(BaseUtil.getSnowflakeId()); // 设置 ID
        super.save(insertBean); // 保存牧场实例

        // 2. 绑定设备
        device.setPastureId(insertBean.getId()); // 将设备绑定到新牧场
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
            super.updateById(insertBean); // 更新牧场信息
        } catch (Exception e) { // 捕获异常
            e.printStackTrace(); // 打印异常
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器错误异常
        }
        return ResultVO.succeed(insertBean); // 返回成功结果
    }

    @Transactional(rollbackFor = Exception.class) // 开启事务，出现异常时回滚
    public ResultVO delete(String id) { // 删除牧场方法
        super.removeById(id); // 根据 ID 删除牧场
        return ResultVO.succeed(); // 返回成功结果
    }

    @Transactional(rollbackFor = Exception.class) // 开启事务，出现异常时回滚
    public ResultVO update(IaPasture iaPasture) { // 更新牧场方法
        Device device = deviceMapper.selectById(iaPasture.getDeviceId()); // 根据设备 ID 查找设备
        if (Objects.isNull(device)) { // 如果设备不存在
            return ResultVO.failed("设备不存在"); // 返回失败结果
        }
        iaPasture.setContractAddr(null); // 清除合同地址
        IaPasture ivPasture1 = this.baseMapper.selectById(iaPasture.getId()); // 查找要更新的牧场
        if (Objects.isNull(ivPasture1)) { // 如果牧场不存在
            return ResultVO.failed(); // 返回失败结果
        }
        // 1. 删除所有绑定当前牧场的设备
        UpdateWrapper<Device> updateWrapper = new UpdateWrapper<Device>(); // 创建更新包装器
        updateWrapper.eq("pasture_id", ivPasture1.getId()); // 设置条件
        updateWrapper.set("pasture_id", null); // 解除绑定
        deviceMapper.update(null, updateWrapper); // 更新设备绑定关系
        super.updateById(iaPasture); // 更新牧场信息

        // 2. 绑定设备
        device.setPastureId(iaPasture.getId()); // 绑定设备到新的牧场
        deviceMapper.updateById(device); // 更新设备信息

        try {
            // 创建温室服务对象
            GreenhouseService pastureService = new GreenhouseService(client, client.getCryptoSuite().getCryptoKeyPair(), ivPasture1.getContractAddr());

            // 创建温室信息修改输入对象
            GreenhouseModifyGreenhouseInfoInputBO input = new GreenhouseModifyGreenhouseInfoInputBO();
            input.set_greenhouseArea(iaPasture.getArea()); // 设置温室面积
            input.set_notes(iaPasture.getDescription()); // 设置备注
            input.set_greenhouseName(iaPasture.getName()); // 设置温室名称
            input.set_greenhousePosition(iaPasture.getAddress()); // 设置温室地址
            input.set_maxPartitionQuantity(BigInteger.valueOf(iaPasture.getBigBreedingQuantity())); // 设置最大分区数量

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

    public ResultVO detail(String id) { // 获取牧场详情方法
        IaPasture ivPasture = this.baseMapper.selectById(id); // 根据 ID 查找牧场
        List<Device> devices = this.deviceMapper.selectList(new LambdaQueryWrapper<Device>().eq(Device::getPastureId, ivPasture.getId())); // 查询绑定该牧场的设备
        ivPasture.setDevices(devices); // 设置设备列表
        return ResultVO.succeed(ivPasture); // 返回牧场详情
    }

    public ResultVO page(BaseDTO baseDTO) { // 分页获取牧场列表方法

        // 创建查询包装器
        LambdaQueryWrapper<IaPasture> qw = new LambdaQueryWrapper<>();
        if (StringUtils.isNoneBlank(baseDTO.getKeyword())) { // 如果有关键词
            qw.and(wrapper ->
                    wrapper.like(IaPasture::getName, baseDTO.getKeyword()) // 按照牧场名模糊查询
                            .or()
                            .like(IaPasture::getId, baseDTO.getKeyword()) // 或按照 ID 模糊查询
            );
        }

        // 创建分页对象
        Page<IaPasture> page = new Page<>(baseDTO.getCurrentPage(), baseDTO.getPageSize());
        Page<IaPasture> p = this.baseMapper.selectPage(page, qw); // 查询分页结果
        p.getRecords().forEach(bean -> { // 遍历每个记录
            Long count = iaPartitionMapper.selectCount( // 查询当前牧场的分区数量
                    new LambdaQueryWrapper<IaPartition>().eq(IaPartition::getPastureId, bean.getId()).eq(IaPartition::getProcessState, 0) // 条件过滤
            );
            bean.setBreedingQuantity(count); // 设置繁殖数量
            // 获取最新一条环境信息返回
            SensorValue lastDateOne = this.sensorValueMapper.getLastDateOne(bean.getId()); // 获取最新的传感器值
            if (Objects.nonNull(lastDateOne)) { // 如果存在传感器值
                bean.setTemperature(lastDateOne.getTemperature()); // 设置温度
                bean.setHumidity(lastDateOne.getHumidity()); // 设置湿度
                bean.setAirquality(lastDateOne.getAirquality()); // 设置空气质量
                bean.setPressure(lastDateOne.getPressure()); // 设置压力
            }
        });

        return ResultVO.succeed(p); // 返回结果
    }

    public ResultVO ivPastureSensorValuePage(IaPastureSensorValuePageDTO dto) { // 分页获取牧场传感器值方法
        LambdaQueryWrapper<SensorValue> qw = new LambdaQueryWrapper<>(); // 创建查询包装器
        qw.eq(SensorValue::getPastureId, dto.getPastureId()); // 设置牧场 ID 条件

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

    public ResultVO pastureList(String name) { // 获取牧场列表方法
        LambdaQueryWrapper<IaPasture> ivPasturesWrapper = new LambdaQueryWrapper<IaPasture>().select(IaPasture::getName, IaPasture::getId); // 创建查询包装器
        if (!StrUtil.isBlank(name)) { // 如果名称不为空
            ivPasturesWrapper.like(IaPasture::getName, name); // 按名称模糊查询
        }
        return ResultVO.succeed(baseMapper.selectList(ivPasturesWrapper)); // 返回结果
    }
}