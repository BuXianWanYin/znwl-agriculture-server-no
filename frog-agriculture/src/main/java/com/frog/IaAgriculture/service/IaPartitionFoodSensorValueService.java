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

    @Autowired //  IaPartitionMapper 对象
    private IaPartitionMapper iaPartitionMapper; // 用于操作分区数据

    @Autowired //  Client 对象
    private Client client; // 用于与区块链进行交互

    @Autowired //  DeviceMapper 对象
    private DeviceMapper deviceMapper; // 用于操作设备数据

    @Autowired //  IaPastureMapper 对象
    private IaPastureMapper iaPastureMapper; // 用于操作大棚数据

    @Autowired //  IaPartitionFoodMapper 对象
    private IaPartitionFoodMapper iaPartitionFoodMapper; // 用于操作食品溯源数据

    @Autowired //  IaFeedingMapper 对象
    private IaFeedingMapper iaFeedingMapper; // 用于操作施肥数据

    @Autowired //  CropBatchMapper 对象
    private CropBatchMapper cropBatchMapper; // 用于操作作物批次数据

    public ResultVO get() { // 回溯源信息包装对象
        return this.getTraceability("1845300162239533056"); // 通过溯源码调用 getTraceability 方法获取信息并返回
    }

    // 晚上8点定时任务
    @Scheduled(cron = "0 0 20 * * ?") // 定时任务注解：每天20点执行
    public void sys() { // 定义系统定时任务方法
        this.sysIaPartitionFoodSensorValueToBlockchain(); // 调用同步分区传感器数据到区块链的方法
    }

    // 定时任务同步牧畜每天的信息
    @Transactional(rollbackFor = Exception.class) // 事务，遇异常回滚
    public void sysIaPartitionFoodSensorValueToBlockchain() { // 定义将传感器数据同步到区块链的方法
        Date date = new Date(); // 获取当前日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 创建日期格式化对象
        String formattedDate = dateFormat.format(date); // 格式化当前日期为字符串
        // 得到每个大棚一天的平均值
        List<IaPartitionSensorValue> ivLivestockSensorValues = baseMapper.get(formattedDate); // 从数据库查询当天每个大棚的平均传感器值
        ivLivestockSensorValues.forEach(map -> { // 对每个记录进行遍历处理
            String pastureId = map.getPastureId(); // 获取该记录所对应的大棚ID
            List<Device> devices = this.deviceMapper.selectList(new LambdaQueryWrapper<Device>().eq(Device::getPastureId, pastureId)); // 根据大棚ID查询设备列表
            if (devices.isEmpty()) { // 如果设备列表为空，则不处理
                return; // 直接返回
            }

            String deviceId = devices.get(0).getDeviceId(); // 获取第一个设备的设备ID

            if (StringUtils.isNoneBlank(pastureId)) { // 检查大棚ID是否不为空
                // 大棚每块分区
                List<IaPartition> iaPartitions = iaPartitionMapper.selectList( // 查询指定大棚下所有分区
                        new LambdaQueryWrapper<IaPartition>().eq(IaPartition::getPastureId, pastureId)
                                .eq(IaPartition::getProcessState, 0).eq(IaPartition::getStatus, 0)
                );
                // 每个大棚每个分区每天的平均值
                List<IaPartitionSensorValue> collect = iaPartitions.stream().map(bean -> { // 遍历每个分区
                    String id = bean.getId(); // 获取分区ID
                    IaPartitionSensorValue insertBean = new IaPartitionSensorValue(); // 创建新的传感器值对象
                    insertBean.setId(BaseUtil.getSnowflakeId()); // 生成唯一ID并设置
                    insertBean.setIaPartitionId(id); // 设置分区ID
                    insertBean.setAirquality(map.getAirquality()); // 复制空气质量数据
                    insertBean.setHumidity(map.getHumidity()); // 复制湿度数据
                    insertBean.setPressure(map.getPressure()); // 复制压力数据
                    insertBean.setTemperature(map.getTemperature()); // 复制温度数据
                    insertBean.setDate(date); // 设置当前日期
                    insertBean.setPastureId(map.getPastureId()); // 设置大棚ID
                    this.baseMapper.insert(insertBean); // 将记录插入数据库
                    insertBean.setContractAddr(bean.getContractAddr()); // 设置对应区块链合约地址
                    return insertBean; // 返回构建好的传感器值记录
                }).collect(Collectors.toList()); // 收集所有记录到列表中

                collect.forEach(value -> { // 遍历生成的记录列表
                    // 根据合约地址创建分区服务对象
                    PartitionsService cattleService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), value.getContractAddr());
                    PartitionsAddCollectorValueInputBO input = new PartitionsAddCollectorValueInputBO(); // 创建添加收集器数值的输入对象
                    input.set_id(BigInteger.valueOf(Long.parseLong(value.getId()))); // 设置并转换记录ID
                    input.set_date(DateUtil.format(value.getDate(), "yyyy-MM-dd")); // 格式化日期并设置
                    input.set_airquality(value.getAirquality().toPlainString()); // 设置空气质量（转为字符串）
                    input.set_pressure(value.getPressure().toPlainString()); // 设置压力（转为字符串）
                    input.set_humidity(value.getHumidity().toPlainString()); // 设置湿度（转为字符串）
                    input.set_temperature(value.getTemperature().toPlainString()); // 设置温度（转为字符串）
                    input.set_deviceId(deviceId); // 设置设备ID

                    try { // 尝试调用区块链接口同步数据
                        // 同步区块链
                        TransactionResponse transactionResponse = cattleService.addCollectorValue(input); // 调用区块链服务接口
                        if (Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) { // 如果返回成功消息
                            // 可在此添加成功处理逻辑
                        } else { // 如果返回消息不成功
                            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器异常
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
     * @return 返回查询到的溯源信息结果对象
     */
    public ResultVO<TraceabilityDTO> getTraceability(String iaPartitionFoodId) { // 传入溯源id
        TraceabilityDTO result = new TraceabilityDTO(); // 创建溯源数据传输对象
        IaPartitionFood ivLivestockSlaughter = this.iaPartitionFoodMapper.selectById(iaPartitionFoodId); // 根据溯源码查询食品溯源记录
        if (Objects.isNull(ivLivestockSlaughter)) { // 如果记录不存在
            return ResultVO.failed("食品溯源信息不存在"); // 返回错误结果
        }
        String iaPartitionId = ivLivestockSlaughter.getIaPartitionId(); // 获取对应分区ID
        CropBatch cropBatch = cropBatchMapper.selectCropBatchByBatchId(Long.valueOf(iaPartitionId)); // 根据分区ID查询作物批次信息
        if (Objects.isNull(cropBatch)) { // 如果批次信息不存在
            return ResultVO.failed("分区已被删除"); // 返回错误结果
        }
        IaPasture iaPasture = this.iaPastureMapper.selectById(cropBatch.getLandId()); // 查询对应大棚信息
        if (Objects.isNull(iaPasture)) { // 如果大棚信息不存在
            return ResultVO.failed("大棚已被删除"); // 返回错误结果
        }
        result.setContractAddr(iaPasture.getContractAddr()); // 将大棚合约地址设置到返回对象中
        String contractAddrIaPasture = iaPasture.getContractAddr(); // 获取大棚合约地址
        GreenhouseService pastureService = new GreenhouseService(client, client.getCryptoSuite().getCryptoKeyPair(), contractAddrIaPasture); // 创建大棚服务对象，用于获取链上大棚信息
        try { // 尝试调用大棚链上信息接口
            // 1.大棚信息： 获取大棚基本信息
            /*
             * @dev 获取大棚信息
             * @return _greenhouseName 大棚名称
             * @return _greenhouseArea 大棚面积
             * @return _maxPartitionQuantity 最大分区数量
             * @return _greenhousePosition 大棚位置
             * @return _notes 备注
             */
            CallResponse info = pastureService.getGreenhouse(); // 调用大棚服务获取大棚信息
            result.setIvPastureInfo(info.getReturnABIObject()); // 将链上返回的大棚信息设置到数据对象中
        } catch (Exception e) { // 捕获异常
            e.printStackTrace(); // 输出详细异常信息
            return ResultVO.failed("获取链大棚信息失败"); // 返回错误结果
        }
        String contractAddrIaPartition = cropBatch.getContractAddress(); // 获取分区合约地址
        // 创建分区服务对象，用于链上交互
        PartitionsService cattleService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), contractAddrIaPartition);

        try { // 尝试获取链上分区信息
            // 2.分区的信息： 获取分区详细信息方法说明
            /*
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
            CallResponse info = cattleService.getPartitions(); // 调用分区服务获取详细分区信息
            result.setIaPartitionInfo(info.getReturnABIObject()); // 将分区信息设置到数据对象中
        } catch (Exception e) { // 捕获异常
            e.printStackTrace(); // 输出异常信息
            return ResultVO.failed("获取链分区信息失败"); // 返回错误结果
        }
        try { // 尝试获取链上商品信息
            // 3.商品信息
            PartitionsGetFoodInputBO input = new PartitionsGetFoodInputBO(); // 创建商品信息查询的输入对象
            input.set_id(iaPartitionFoodId); // 设置溯源码到输入对象
            CallResponse goods = cattleService.getFood(input); // 调用链上接口获取商品信息
            result.setIaPartitionFoodSensorValueInfo(JSONUtil.parse(goods.getReturnABIObject())); // 将返回的商品信息解析并设置到数据对象中
        } catch (Exception e) { // 捕获异常
            e.printStackTrace(); // 输出异常堆栈
            return ResultVO.failed("获取链商品信息失败"); // 返回错误结果
        }
        result.setMap(iaPartitionFoodMapper.calculateDailyAverages(iaPasture.getId())); // 设置每日平均数据信息映射
        return ResultVO.succeed(result); // 返回成功的溯源信息结果
    }

    // 获取施肥信息页面数据
    public ResultVO getFeedingPage(EnvironmentPageDTO dto) { // 获取施肥信息分页数据的方法
        IaPartitionFood ivLivestockSlaughter = this.iaPartitionFoodMapper.selectById(dto.getIaPartitionFoodId()); // 根据溯源码获取食品溯源记录
        if (Objects.isNull(ivLivestockSlaughter)) { // 如果记录为空
            return ResultVO.failed("食品溯源信息不存在"); // 返回失败提示信息
        }
        String iaPartitionId = ivLivestockSlaughter.getIaPartitionId(); // 获取分区ID
        CropBatch cropBatch = cropBatchMapper.selectCropBatchByBatchId(Long.valueOf(iaPartitionId)); // 查询作物批次信息
        if (Objects.isNull(cropBatch)) { // 如果作物批次为空
            return ResultVO.failed("分区已被删除"); // 返回错误信息，提示分区已删除
        }
        String id = String.valueOf(cropBatch.getLandId()); // 获取大棚ID字符串

        LambdaQueryWrapper<IaFeeding> wq = new LambdaQueryWrapper<IaFeeding>(); // 创建查询包装器对象
        wq.eq(IaFeeding::getIaPartitionId, id); // 添加查询条件：分区ID必须匹配
        if (!StringUtils.isBlank(dto.getStartTime())) { // 如果查询条件中指定开始时间
            wq.ge(IaFeeding::getDate, dto.getStartTime()); // 添加大于等于开始时间的查询条件
        }
        if (!StringUtils.isBlank(dto.getEndTime())) { // 如果查询条件中指定结束时间
            wq.le(IaFeeding::getDate, dto.getEndTime()); // 添加小于等于结束时间的查询条件
        }
        Page<IaFeeding> page = new Page<>(dto.getCurrentPage(), dto.getPageSize()); // 创建分页对象，设置当前页与每页数量
        Page<IaFeeding> feedingPage = iaFeedingMapper.selectPage(page, wq); // 通过查询条件分页获取施肥信息记录
        List<BigInteger> collect = feedingPage.getRecords().stream() // 对记录列表进行流处理
                .map(IaFeeding::getId) // 获取每条记录的ID
                .map(BigInteger::new) // 将ID转换为 BigInteger 类型
                .collect(Collectors.toList()); // 收集转换后的ID到列表中
        if (!collect.isEmpty()) { // 如果 ID 列表不为空
            PartitionsService partitionsService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), cropBatch.getContractAddress()); // 根据合约地址创建分区服务对象
            try { // 尝试获取链上施肥信息
                PartitionsGetFertilizerInputBO input = new PartitionsGetFertilizerInputBO(); // 创建施肥信息查询输入对象
                input.set_ids(collect); // 设置查询ID列表
                CallResponse fertilizer = partitionsService.getFertilizer(input); // 调用分区服务获取施肥信息
                List<ABIObject> returnABIObject = fertilizer.getReturnABIObject(); // 获取返回的 ABI 对象列表
                if (Objects.nonNull(returnABIObject)) { // 如果返回对象不为空
                    JSONArray objects = JSONUtil.parseArray(returnABIObject); // 将返回对象转换为 JSONArray
                    JSON parse = JSONUtil.parse(objects.get(0)); // 解析第一个对象为 JSON
                    JSONArray listValues = (JSONArray) parse.getByPath("listValues"); // 获取键为 "listValues" 的数据
                    List<Object> list = new ArrayList<>(); // 创建新的列表用于存储数据
                    for (int i = 0; i < listValues.size(); i++) { // 遍历 listValues 数组
                        list.add(listValues.get(i)); // 将每个数据项添加至列表
                    }
                    Page<Object> resultData = new Page<>(); // 创建新的分页结果对象
                    BeanUtils.copyProperties(page, resultData); // 复制分页信息
                    resultData.setRecords(list); // 设置转换后的数据列表
                    return ResultVO.succeed(resultData); // 返回封装成功结果的分页对象
                }
            } catch (Exception e) { // 捕获异常
                e.printStackTrace(); // 输出异常详细信息
                return ResultVO.failed("获取链牲施肥信息失败"); // 返回失败结果
            }
        }
        return ResultVO.succeed(feedingPage); // 返回查询到的施肥信息分页结果
    }

    // 环境信息页面选项
    public ResultVO getEnvironmentPage(EnvironmentPageDTO dto) { // 定义获取环境信息分页数据的方法
        IaPartitionFood iaPartitionFood = this.iaPartitionFoodMapper.selectById(dto.getIaPartitionFoodId()); // 根据溯源码查询食品溯源记录
        if (Objects.isNull(iaPartitionFood)) { // 如果记录不存在
            return ResultVO.failed("溯源信息不存在"); // 返回错误结果
        }
        String livestockId = dto.getIaPartitionFoodId(); // 获取溯源码
        CropBatch cropBatch = cropBatchMapper.selectCropBatchByBatchId(Long.valueOf(iaPartitionFood.getIaPartitionId())); // 根据分区ID查询作物批次信息
        if (Objects.isNull(cropBatch)) { // 如果作物批次不存在
            return ResultVO.failed("分区已被删除"); // 返回错误信息
        }

        LambdaQueryWrapper<IaPartitionSensorValue> wq = new LambdaQueryWrapper<IaPartitionSensorValue>(); // 创建传感器值查询包装器
        wq.eq(IaPartitionSensorValue::getIaPartitionId, livestockId); // 添加条件：分区ID匹配

        if (!StringUtils.isBlank(dto.getStartTime())) { // 如果指定了开始时间
            wq.ge(IaPartitionSensorValue::getDate, dto.getStartTime()); // 添加大于等于开始时间的查询条件
        }
        if (!StringUtils.isBlank(dto.getEndTime())) { // 如果指定了结束时间
            wq.le(IaPartitionSensorValue::getDate, dto.getEndTime()); // 添加小于等于结束时间的查询条件
        }
        Page<IaPartitionSensorValue> page = new Page<>(dto.getCurrentPage(), dto.getPageSize()); // 创建分页对象
        Page<IaPartitionSensorValue> ivLivestockSensorValuePage = this.baseMapper.selectPage(page, wq); // 分页查询传感器数据
        if (!ivLivestockSensorValuePage.getRecords().isEmpty()) { // 如果查询结果不为空
            PartitionsService cattleService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), cropBatch.getContractAddress()); // 根据合约地址创建分区服务对象
            List<BigInteger> collect = ivLivestockSensorValuePage.getRecords().stream().map(bean -> { // 遍历传感器数据记录
                return BigInteger.valueOf(Long.parseLong(bean.getId())); // 将每条记录的ID转换为 BigInteger 类型
            }).collect(Collectors.toList()); // 收集所有转换后的ID

            try { // 尝试调用链上接口获取传感器数据
                PartitionsGetCollectorValueInputBO input = new PartitionsGetCollectorValueInputBO(); // 创建查询收集器值输入对象
                input.set_ids(collect); // 设置 ID 列表
                CallResponse sensorValue = cattleService.getCollectorValue(input); // 调用分区服务获取传感器信息
                List<ABIObject> returnABIObject = sensorValue.getReturnABIObject(); // 获取返回的 ABI 对象
                if (Objects.nonNull(returnABIObject)) { // 如果返回对象不为空
                    JSONArray objects = JSONUtil.parseArray(returnABIObject); // 解析返回对象为 JSONArray
                    JSON parse = JSONUtil.parse(objects.get(0)); // 解析第一个对象
                    JSONArray listValues = (JSONArray) parse.getByPath("listValues"); // 获取 "listValues" 数组
                    List<Object> list = new ArrayList<>(); // 创建列表存储环境数据
                    for (int i = 0; i < listValues.size(); i++) { // 遍历数组
                        list.add(listValues.get(i)); // 添加每个数据项到列表
                    }
                    Page<Object> resultData = new Page<>(); // 创建分页结果对象
                    BeanUtils.copyProperties(page, resultData); // 复制分页信息
                    resultData.setRecords(list); // 设置返回数据记录
                    return ResultVO.succeed(resultData); // 返回成功结果
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResultVO.failed("获取链分区环境信息失败");
            }
        }

        return ResultVO.succeed(ivLivestockSensorValuePage); // 返回传感器数据分页结果
    }
}