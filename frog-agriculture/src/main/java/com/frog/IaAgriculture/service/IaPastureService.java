package com.frog.IaAgriculture.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.frog.IaAgriculture.dto.BaseDTO;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.dto.IaPastureSensorValuePageDTO;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.domain.SensorValue;
import com.frog.IaAgriculture.exception.ServerException;
import com.frog.IaAgriculture.mapper.DeviceMapper;
import com.frog.IaAgriculture.mapper.IaPartitionMapper;
import com.frog.IaAgriculture.mapper.IaPastureMapper;
import com.frog.IaAgriculture.mapper.SensorValueMapper;
import com.frog.IaAgriculture.model.IaPasture;
import vip.blockchain.agriculture.model.bo.GreenhouseModifyGreenhouseInfoInputBO;
import vip.blockchain.agriculture.model.bo.PlatformAddGreenhouseInfoInputBO;
import com.frog.IaAgriculture.model.entity.IaPartition;
import vip.blockchain.agriculture.service.GreenhouseService;
import vip.blockchain.agriculture.service.PlatformService;
import vip.blockchain.agriculture.utils.*;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Service
public class IaPastureService extends ServiceImpl<IaPastureMapper, IaPasture> {


    @Autowired
    private PlatformService platformService;
    @Autowired
    private Client client;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private SensorValueMapper sensorValueMapper;
    @Autowired
    private IaPartitionMapper iaPartitionMapper;

    @Transactional(rollbackFor = Exception.class)
    public ResultVO create(IaPasture iaPasture) {
        iaPasture.setContractAddr(null);
        Device device = deviceMapper.selectById(iaPasture.getDeviceId());
        if (Objects.isNull(device)) {
            return ResultVO.failed("设备不存在");
        }
        //1.把原有的设备绑定关系解除
        UpdateWrapper<Device> updateWrapper = new UpdateWrapper<Device>();
        updateWrapper.eq("id", device.getId()).set("pasture_id", null);
        deviceMapper.update(updateWrapper);


        IaPasture insertBean = new IaPasture();
        BeanUtils.copyProperties(iaPasture, insertBean);
        insertBean.setId(BaseUtil.getSnowflakeId());
        super.save(insertBean);
        //2.绑定设备
        device.setPastureId(insertBean.getId());
        deviceMapper.updateById(device);
        try {
            PlatformAddGreenhouseInfoInputBO input = new PlatformAddGreenhouseInfoInputBO();
            input.set_greenhouseArea(insertBean.getArea());
            input.set_notes(insertBean.getDescription());
            input.set_greenhouseName(insertBean.getName());
            input.set_greenhousePosition(insertBean.getAddress());
            input.set_maxPartitionQuantity(BigInteger.valueOf(insertBean.getBigBreedingQuantity()));

            TransactionResponse transactionResponse = platformService.addGreenhouseInfo(input);
            if (Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) {
                String values = transactionResponse.getValues();
                if (StringUtils.isBlank(values)) {
                    throw new ServerException("合约地址不存在");
                }
                String contractAddr = BaseUtil.parseContractAddr(values);
                if (StringUtils.isBlank(contractAddr)) {
                    throw new ServerException(ErrorCodeEnum.CONTENT_PARSE_ADDR_ERROR);
                }
                insertBean.setContractAddr(contractAddr);
            } else {
                throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
            }
            super.updateById(insertBean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
        }
        return ResultVO.succeed(insertBean);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO delete(String id) {
        super.removeById(id);
        return ResultVO.succeed();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResultVO update(IaPasture iaPasture) {
        Device device = deviceMapper.selectById(iaPasture.getDeviceId());
        if (Objects.isNull(device)) {
            return ResultVO.failed("设备不存在");
        }
        iaPasture.setContractAddr(null);
        IaPasture ivPasture1 = this.baseMapper.selectById(iaPasture.getId());
        if (Objects.isNull(ivPasture1)) {
            return ResultVO.failed();
        }
        //1.删除所有绑定当前牧场的设备
        UpdateWrapper<Device> updateWrapper = new UpdateWrapper<Device>();
        updateWrapper.eq("pasture_id", ivPasture1.getId());
        updateWrapper.set("pasture_id", null);
        deviceMapper.update(null, updateWrapper);
        super.updateById(iaPasture);
        //2.绑定设备
        device.setPastureId(iaPasture.getId());
        deviceMapper.updateById(device);
        try {
            GreenhouseService pastureService = new GreenhouseService(client, client.getCryptoSuite().getCryptoKeyPair(), ivPasture1.getContractAddr());

            GreenhouseModifyGreenhouseInfoInputBO input = new GreenhouseModifyGreenhouseInfoInputBO();
            input.set_greenhouseArea(iaPasture.getArea());
            input.set_notes(iaPasture.getDescription());
            input.set_greenhouseName(iaPasture.getName());
            input.set_greenhousePosition(iaPasture.getAddress());
            input.set_maxPartitionQuantity(BigInteger.valueOf(iaPasture.getBigBreedingQuantity()));

            TransactionResponse transactionResponse = pastureService.modifyGreenhouseInfo(input);
            if (!Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) {
                throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
        }
        return ResultVO.succeed();
    }

    public ResultVO detail(String id) {
        IaPasture ivPasture = this.baseMapper.selectById(id);
        List<Device> devices = this.deviceMapper.selectList(new LambdaQueryWrapper<Device>().eq(Device::getPastureId, ivPasture.getId()));
        ivPasture.setDevices(devices);
        return ResultVO.succeed(ivPasture);
    }

    public ResultVO page(BaseDTO baseDTO) {

        LambdaQueryWrapper<IaPasture> qw = new LambdaQueryWrapper<>();
        if (StringUtils.isNoneBlank(baseDTO.getKeyword())) {
            qw.and(wrapper ->
                    wrapper.like(IaPasture::getName, baseDTO.getKeyword())
                            .or()
                            .like(IaPasture::getId, baseDTO.getKeyword())
            );
        }

        Page<IaPasture> page = new Page<>(baseDTO.getCurrentPage(), baseDTO.getPageSize());
        Page<IaPasture> p = this.baseMapper.selectPage(page, qw);
        p.getRecords().forEach(bean -> {
            Long count = iaPartitionMapper.selectCount(
                    new LambdaQueryWrapper<IaPartition>().eq(IaPartition::getPastureId, bean.getId()).eq(IaPartition::getProcessState, 0)
            );
            bean.setBreedingQuantity(count);
            //获取最新一条环境信息返回
            SensorValue lastDateOne = this.sensorValueMapper.getLastDateOne(bean.getId());
            if (Objects.nonNull(lastDateOne)) {
                bean.setTemperature(lastDateOne.getTemperature());
                bean.setHumidity(lastDateOne.getHumidity());
                bean.setAirquality(lastDateOne.getAirquality());
                bean.setPressure(lastDateOne.getPressure());
            }
        });

        return ResultVO.succeed(p);
    }
    public ResultVO ivPastureSensorValuePage(IaPastureSensorValuePageDTO dto) {

        LambdaQueryWrapper<SensorValue> qw = new LambdaQueryWrapper<>();
        qw.eq(SensorValue::getPastureId, dto.getPastureId());

        if (!StringUtils.isBlank(dto.getStartTime())) {
            qw.ge(SensorValue::getDateTime, dto.getStartTime());
        }
        if (!StringUtils.isBlank(dto.getEndTime())) {
            qw.le(SensorValue::getDateTime, dto.getEndTime());
        }
        Page<SensorValue> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        OrderItem orderItem = new OrderItem();
        orderItem.setColumn("date");
        orderItem.setAsc(false);
        page.addOrder(orderItem);
        Page<SensorValue> sensorValuePage = this.sensorValueMapper.selectPage(page, qw);
        return ResultVO.succeed(sensorValuePage);
    }
    public ResultVO selectSensorValuePage(BaseDTO dto) {

        // 创建分页对象并设置分页参数
        Page<SensorValue> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());

        // 设置排序规则，按 dateTime 字段倒序排序，确保获取最新的数据
        OrderItem orderItem = new OrderItem();
        orderItem.setColumn("date"); // 将排序字段设为 dateTime
        orderItem.setAsc(false); // 按降序排序
        page.addOrder(orderItem);

        // 查询所有数据，按分页返回
        Page<SensorValue> sensorValuePage = this.sensorValueMapper.selectPage(page, new LambdaQueryWrapper<>());

        // 返回结果
        return ResultVO.succeed(sensorValuePage);
    }


    public ResultVO pastureList(String name) {
        LambdaQueryWrapper<IaPasture> ivPasturesWrapper = new LambdaQueryWrapper<IaPasture>().select(IaPasture::getName, IaPasture::getId);
        if (!StrUtil.isBlank(name)) {
            ivPasturesWrapper.like(IaPasture::getName, name);
        }
        return ResultVO.succeed(baseMapper.selectList(ivPasturesWrapper));
    }
}
