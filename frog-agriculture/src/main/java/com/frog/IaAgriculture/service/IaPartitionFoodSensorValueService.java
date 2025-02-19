package com.frog.IaAgriculture.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frog.agriculture.domain.CropBatch;
import com.frog.agriculture.mapper.CropBatchMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Select;
import org.fisco.bcos.sdk.abi.wrapper.ABIObject;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.model.dto.CallResponse;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.frog.IaAgriculture.dto.EnvironmentPageDTO;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.dto.TraceabilityDTO;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.exception.ServerException;
import com.frog.IaAgriculture.mapper.*;
import com.frog.IaAgriculture.model.IaFeeding;
import com.frog.IaAgriculture.model.IaPartitionFood;
import com.frog.IaAgriculture.model.IaPartitionSensorValue;
import com.frog.IaAgriculture.model.IaPasture;
import vip.blockchain.agriculture.model.bo.PartitionsAddCollectorValueInputBO;
import vip.blockchain.agriculture.model.bo.PartitionsGetCollectorValueInputBO;
import vip.blockchain.agriculture.model.bo.PartitionsGetFertilizerInputBO;
import vip.blockchain.agriculture.model.bo.PartitionsGetFoodInputBO;
import com.frog.IaAgriculture.model.entity.IaPartition;
import vip.blockchain.agriculture.service.GreenhouseService;
import vip.blockchain.agriculture.service.PartitionsService;
import vip.blockchain.agriculture.utils.BaseUtil;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class IaPartitionFoodSensorValueService extends ServiceImpl<IaPartitionSensorValueMapper, IaPartitionSensorValue> {

    @Autowired
    private IaPartitionMapper iaPartitionMapper;
    @Autowired
    private Client client;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private IaPastureMapper iaPastureMapper;
    @Autowired
    private IaPartitionFoodMapper iaPartitionFoodMapper;
    @Autowired
    private IaFeedingMapper iaFeedingMapper;

    @Autowired
    private CropBatchMapper cropBatchMapper;


    public ResultVO get() {
        //之前的记录日期
        //List<String> BeforeDate = this.baseMapper.getBeforeDate();


        //this.sysIvLivestockSensorValueToBlockchain();
        return this.getTraceability("1845300162239533056");
        //return ResultVO.succeed(traceability);
    }

    //晚上8点
    @Scheduled(cron = "0 0 20 * * ?")
    public void sys() {
        this.sysIaPartitionFoodSensorValueToBlockchain();
    }

    //定时任务同步牧畜每天的信息
    @Transactional(rollbackFor = Exception.class)
    public void sysIaPartitionFoodSensorValueToBlockchain() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(date);
        //得到每个大棚一天的平均值
        List<IaPartitionSensorValue> ivLivestockSensorValues = baseMapper.get(formattedDate);
        ivLivestockSensorValues.forEach(map -> {

            String pastureId = map.getPastureId();
            List<Device> devices = this.deviceMapper.selectList(new LambdaQueryWrapper<Device>().eq(Device::getPastureId, pastureId));
            if (devices.isEmpty()) {
                return;
            }

            String deviceId = devices.get(0).getDeviceId();

            if (StringUtils.isNoneBlank(pastureId)) {
                //大棚每块分区
                List<IaPartition> iaPartitions = iaPartitionMapper.selectList(
                        new LambdaQueryWrapper<IaPartition>().eq(IaPartition::getPastureId, pastureId)
                                .eq(IaPartition::getProcessState, 0).eq(IaPartition::getStatus, 0)
                );
                //每个大棚每个分区每天的平均值
                List<IaPartitionSensorValue> collect = iaPartitions.stream().map(bean -> {
                    String id = bean.getId();
                    IaPartitionSensorValue insertBean = new IaPartitionSensorValue();
                    insertBean.setId(BaseUtil.getSnowflakeId());
                    insertBean.setIaPartitionId(id);
                    insertBean.setAirquality(map.getAirquality());
                    insertBean.setHumidity(map.getHumidity());
                    insertBean.setPressure(map.getPressure());
                    insertBean.setTemperature(map.getTemperature());
                    insertBean.setDate(date);
                    insertBean.setPastureId(map.getPastureId());
                    this.baseMapper.insert(insertBean);
                    insertBean.setContractAddr(bean.getContractAddr());
                    return insertBean;
                }).collect(Collectors.toList());

                collect.forEach(value -> {
                    PartitionsService cattleService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), value.getContractAddr());
                    PartitionsAddCollectorValueInputBO input = new PartitionsAddCollectorValueInputBO();
                    input.set_id(BigInteger.valueOf(Long.parseLong(value.getId())));
                    input.set_date(DateUtil.format(value.getDate(), "yyyy-MM-dd"));
                    input.set_airquality(value.getAirquality().toPlainString());
                    input.set_pressure(value.getPressure().toPlainString());
                    input.set_humidity(value.getHumidity().toPlainString());
                    input.set_temperature(value.getTemperature().toPlainString());
                    input.set_deviceId(deviceId);

                    try {
                        //同步区块链
                        TransactionResponse transactionResponse = cattleService.addCollectorValue(input);
                        if (Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) {

                        } else {
                            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("同步分区传感器信息异常");
                    }
                });
            }
        });
    }





    /**
     * 查询溯源信息
     *
     * @param iaPartitionFoodId 溯源码/溯源id
     * @return
     */
    public ResultVO<TraceabilityDTO> getTraceability(String iaPartitionFoodId) {

        TraceabilityDTO result = new TraceabilityDTO();
        IaPartitionFood ivLivestockSlaughter = this.iaPartitionFoodMapper.selectById(iaPartitionFoodId);
        if (Objects.isNull(ivLivestockSlaughter)) {
            return ResultVO.failed("食品溯源信息不存在");
        }
        String iaPartitionId = ivLivestockSlaughter.getIaPartitionId();
        CropBatch cropBatch = cropBatchMapper.selectCropBatchByBatchId(Long.valueOf(iaPartitionId));
        if (Objects.isNull(cropBatch)) {
            return ResultVO.failed("分区已被删除");
        }
        IaPasture iaPasture = this.iaPastureMapper.selectById(cropBatch.getLandId());
        if (Objects.isNull(iaPasture)) {
            return ResultVO.failed("大棚已被删除");
        }
        result.setContractAddr(iaPasture.getContractAddr());
        String contractAddrIaPasture = iaPasture.getContractAddr();
        GreenhouseService pastureService = new GreenhouseService(client, client.getCryptoSuite().getCryptoKeyPair(), contractAddrIaPasture);
        try {
            //1.大棚信息： /**
            //     * @dev 获取大棚信息
            //     * @return _greenhouseName 大棚名称
            //     * @return _greenhouseArea 大棚面积
            //     * @return _maxPartitionQuantity 最大分区数量
            //     * @return _greenhousePosition 大棚位置
            //     * @return _notes 备注
            //     */
            CallResponse info = pastureService.getGreenhouse();
            result.setIvPastureInfo(info.getReturnABIObject());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.failed("获取链大棚信息失败");
        }
        String contractAddrIaPartition = cropBatch.getContractAddress();
        PartitionsService cattleService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), contractAddrIaPartition);

        try {
            //2.分区的信息
            /**
             * @dev 获取分区信息
             * @return _id 分区id
             * @return _plantingVarieties 种植种类
             * @return _partitionsName 分区名称
             * @return _plantingName 种植名称
             * @return _offHarvest 是否收获
             * @return _plantingDate 种植日期
             * @return _ofGreenhouse 所属大棚
             * @return _harvestTimestamp 收获时间
             * @return _notes 备注
             * @return _process 备注
             */
            CallResponse info = cattleService.getPartitions();
            result.setIaPartitionInfo(info.getReturnABIObject());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.failed("获取链分区信息失败");
        }
        try {
            //3.商品信息
            PartitionsGetFoodInputBO input = new PartitionsGetFoodInputBO();
            input.set_id(iaPartitionFoodId);
            CallResponse goods = cattleService.getFood(input);
            result.setIaPartitionFoodSensorValueInfo(JSONUtil.parse(goods.getReturnABIObject()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.failed("获取链商品信息失败");
        }

//        //4.施肥信息
//        String id = String.valueOf(cropBatch.getBatchId());
//        List<IaPartitionFood> ivLivestockFeedings = iaPartitionFoodMapper.selectList(new LambdaQueryWrapper<IaPartitionFood>().eq(IaPartitionFood::getIaPartitionId, id));
        result.setMap(iaPartitionFoodMapper.calculateDailyAverages(iaPasture.getId()));
//        if (!ivLivestockFeedings.isEmpty()) {
//            PartitionsService partitionsService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), cropBatch.getContractAddress());
//            try {
//                CallResponse info = partitionsService.getFertilizers();
//                result.setIaPartitionFeedingInfo(JSONUtil.parse(info.getReturnABIObject()));
//            } catch (Exception e) {
//                e.printStackTrace();
//                return ResultVO.failed("获取链牲施肥信息失败");
//            }
//        }


        return ResultVO.succeed(result);
    }

    //获取施肥信息
    public ResultVO getFeedingPage(EnvironmentPageDTO dto) {
        IaPartitionFood ivLivestockSlaughter = this.iaPartitionFoodMapper.selectById(dto.getIaPartitionFoodId());
        if (Objects.isNull(ivLivestockSlaughter)) {
            return ResultVO.failed("食品溯源信息不存在");
        }
        String iaPartitionId = ivLivestockSlaughter.getIaPartitionId();
        CropBatch cropBatch = cropBatchMapper.selectCropBatchByBatchId(Long.valueOf(iaPartitionId));
        if (Objects.isNull(cropBatch)) {
            return ResultVO.failed("分区已被删除");
        }
        String id = String.valueOf(cropBatch.getLandId());

        LambdaQueryWrapper<IaFeeding> wq = new LambdaQueryWrapper<IaFeeding>();
        wq.eq(IaFeeding::getIaPartitionId, id);
        if (!StringUtils.isBlank(dto.getStartTime())) {
            wq.ge(IaFeeding::getDate, dto.getStartTime());
        }
        if (!StringUtils.isBlank(dto.getEndTime())) {
            wq.le(IaFeeding::getDate, dto.getEndTime());
        }
        Page<IaFeeding> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        Page<IaFeeding> feedingPage = iaFeedingMapper.selectPage(page, wq);
        List<BigInteger> collect = feedingPage.getRecords().stream()
                .map(IaFeeding::getId)
                .map(BigInteger::new)
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            PartitionsService partitionsService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), cropBatch.getContractAddress());
            try {
                PartitionsGetFertilizerInputBO input = new PartitionsGetFertilizerInputBO();
                input.set_ids(collect);
                CallResponse fertilizer = partitionsService.getFertilizer(input);
                List<ABIObject> returnABIObject = fertilizer.getReturnABIObject();
                if (Objects.nonNull(returnABIObject)) {
                    JSONArray objects = JSONUtil.parseArray(returnABIObject);
                    JSON parse = JSONUtil.parse(objects.get(0));
                    JSONArray listValues = (JSONArray)parse.getByPath("listValues");
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < listValues.size(); i++) {
                        list.add(listValues.get(i));
                    }
                    Page<Object> resultData = new Page<>();
                    BeanUtils.copyProperties(page, resultData);
                    resultData.setRecords(list);
                    return ResultVO.succeed(resultData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResultVO.failed("获取链牲施肥信息失败");
            }
        }
        return ResultVO.succeed(feedingPage);
    }

    //5.环境信息信息选项
    public ResultVO getEnvironmentPage(EnvironmentPageDTO dto) {
        IaPartitionFood iaPartitionFood = this.iaPartitionFoodMapper.selectById(dto.getIaPartitionFoodId());
        if (Objects.isNull(iaPartitionFood)) {
            return ResultVO.failed("溯源信息不存在");
        }
        String livestockId = dto.getIaPartitionFoodId();
        CropBatch cropBatch = cropBatchMapper.selectCropBatchByBatchId(Long.valueOf(iaPartitionFood.getIaPartitionId()));
        if (Objects.isNull(cropBatch)) {
            return ResultVO.failed("分区已被删除");
        }

        LambdaQueryWrapper<IaPartitionSensorValue> wq = new LambdaQueryWrapper<IaPartitionSensorValue>();
        wq.eq(IaPartitionSensorValue::getIaPartitionId, livestockId);

        if (!StringUtils.isBlank(dto.getStartTime())) {
            wq.ge(IaPartitionSensorValue::getDate, dto.getStartTime());
        }
        if (!StringUtils.isBlank(dto.getEndTime())) {
            wq.le(IaPartitionSensorValue::getDate, dto.getEndTime());
        }
        Page<IaPartitionSensorValue> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        Page<IaPartitionSensorValue> ivLivestockSensorValuePage = this.baseMapper.selectPage(page, wq);
        if (!ivLivestockSensorValuePage.getRecords().isEmpty()) {
            PartitionsService cattleService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), cropBatch.getContractAddress());
            List<BigInteger> collect = ivLivestockSensorValuePage.getRecords().stream().map(bean -> {
                return BigInteger.valueOf(Long.parseLong(bean.getId()));
            }).collect(Collectors.toList());

            try {
                PartitionsGetCollectorValueInputBO input = new PartitionsGetCollectorValueInputBO();
                input.set_ids(collect);
                CallResponse sensorValue = cattleService.getCollectorValue(input);
                List<ABIObject> returnABIObject = sensorValue.getReturnABIObject();
                if (Objects.nonNull(returnABIObject)) {
                    JSONArray objects = JSONUtil.parseArray(returnABIObject);
                    JSON parse = JSONUtil.parse(objects.get(0));
                    JSONArray listValues = (JSONArray)parse.getByPath("listValues");
                    List<Object> list = new ArrayList<>();
                    for (int i = 0; i < listValues.size(); i++) {
                        list.add(listValues.get(i));
                    }
                    Page<Object> resultData = new Page<>();
                    BeanUtils.copyProperties(page, resultData);
                    resultData.setRecords(list);
                    return ResultVO.succeed(resultData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResultVO.failed("获取链分区环境信息失败");
            }
        }

        return ResultVO.succeed(ivLivestockSensorValuePage);
    }
}
