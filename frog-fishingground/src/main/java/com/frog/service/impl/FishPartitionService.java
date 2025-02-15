package com.frog.service.impl;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import cn.hutool.core.collection.Partition;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.dto.IvLivestockDTO;
import com.frog.IaAgriculture.dto.IvLivestockOutDTO;
import com.frog.IaAgriculture.mapper.IaPartitionMapper;
import com.frog.IaAgriculture.mapper.IaPastureMapper;
import com.frog.IaAgriculture.model.IaPasture;
import com.frog.IaAgriculture.model.entity.IaPartition;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;
import com.frog.mapper.FishPartitionMapper;
import com.frog.mapper.FishPastureMapper;
import com.frog.model.FishPasture;
import com.frog.model.entity.FishPartition;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.blockchain.agriculture.model.bo.*;
import vip.blockchain.agriculture.service.GreenhouseService;
import vip.blockchain.agriculture.service.PartitionsService;
import vip.blockchain.agriculture.service.PlatformService;
import vip.blockchain.agriculture.utils.BaseUtil;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FishPartitionService extends ServiceImpl<FishPartitionMapper, FishPartition> {
    @Resource
    PlatformService platformService;  //上链

    @Resource
    FishPastureMapper fishPastureMapper; //鱼棚mapper

    @Resource
    Client client;

    /**
     * 添加分区
     * @param iaPartition 分区对象
     * @return ResultVO 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO addPartition(FishPartition iaPartition) {
        FishPasture iaPasture = fishPastureMapper.selectById(iaPartition.getPastureId());
        String psContractAddr = iaPasture.getContractAddr();
        if (iaPasture == null || StrUtil.isBlank(psContractAddr)) {
            return ResultVO.failed("大棚不存在");
        }
        String snowflakeId = BaseUtil.getSnowflakeId();

        Date now = new Date();
        PlatformAddPartitionsInputBO partitionsInputBO = new PlatformAddPartitionsInputBO();
        partitionsInputBO.set_id(new BigInteger(snowflakeId));
        partitionsInputBO.set_partitionsName(iaPartition.getPartitionName());
        partitionsInputBO.set_notes(iaPartition.getRemark() == null ? " " : iaPartition.getRemark());
        partitionsInputBO.set_plantingName(iaPartition.getPartitionName());
        partitionsInputBO.set_plantingDate(DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
        partitionsInputBO.set_plantingVarieties(iaPartition.getVariety());
        partitionsInputBO.set_ofGreenhouse(iaPasture.getContractAddr());

        try {
            TransactionResponse transactionResponse = platformService.addPartitions(partitionsInputBO);
            if (transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                String contractAddressArray = transactionResponse.getValues();
                JSONArray jsonArray = JSONUtil.parseArray(contractAddressArray);
                String contractAddress = jsonArray.getStr(0);
                iaPartition.setContractAddr(contractAddress);
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        iaPartition.setId(snowflakeId);
        iaPartition.setPlantDate(now);
        baseMapper.insert(iaPartition);
        return ResultVO.succeed("添加成功");
    }

    /**
     * 删除分区
     * @param id 分区ID
     * @return ResultVO 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO deletePartition(String id) {
        FishPartition iaPartition = baseMapper.selectById(id);
        if (iaPartition == null) {
            return ResultVO.failed("不存在该分区");
        }
        FishPasture iaPasture = fishPastureMapper.selectById(iaPartition.getPastureId());
        String psContractAddr = iaPasture.getContractAddr();
        if (iaPasture == null || StrUtil.isBlank(psContractAddr)) {
            return ResultVO.failed("大棚不存在");
        }
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        GreenhouseService greenhouseService = new GreenhouseService(client, cryptoKeyPair, psContractAddr);
        GreenhouseRemovePartitionsInputBO p = new GreenhouseRemovePartitionsInputBO();
        p.set_partitions(iaPartition.getContractAddr());
        try {
            TransactionResponse transactionResponse = greenhouseService.removePartitions(p);
            if (!transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * 更新分区信息
     * @param iaPartition 分区对象
     * @return ResultVO 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updatePartition(FishPartition iaPartition) {
        if (StrUtil.isBlank(iaPartition.getId())) {
            return ResultVO.failed("id不能为空");
        }
        FishPartition iaPartitionFromDB = baseMapper.selectById(iaPartition.getId());
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        // 分区地址
        String cattleContract = iaPartitionFromDB.getContractAddr();
        PartitionsService partitionsService = new PartitionsService(client, cryptoKeyPair, cattleContract);
        PartitionsModifyPartitionsInfoInputBO cmc = new PartitionsModifyPartitionsInfoInputBO();
        cmc.set_partitionsName(iaPartition.getPartitionName());
        cmc.set_plantingDate(DateUtil.format(iaPartition.getPlantDate(), "yyyy-MM-dd HH:mm:ss"));
        cmc.set_plantingVarieties(iaPartition.getVariety());
        cmc.set_plantingName(iaPartition.getPlantName());
        cmc.set_notes(StrUtil.isBlank(iaPartition.getRemark()) ? " " : iaPartition.getRemark());

        FishPasture ivPasture = fishPastureMapper.selectById(iaPartition.getPastureId());
        // 大棚地址
        String psContractAddr = ivPasture.getContractAddr();
        PartitionsModifyOfGreenhouseInputBO pmog = new PartitionsModifyOfGreenhouseInputBO();
        pmog.set_newOfGreenhouse(psContractAddr);

        try {
            TransactionResponse transactionResponse = partitionsService.modifyPartitionsInfo(cmc);
            TransactionResponse transactionResponse1 = partitionsService.modifyOfGreenhouse(pmog);
            if (!transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE) ||
                    !transactionResponse1.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        baseMapper.updateById(iaPartition);
        return ResultVO.succeed();
    }

    /**
     * 分页获取分区列表
     * @param pastureName 牧场名称
     * @param id 分区ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return ResultVO 返回操作结果
     */
    public ResultVO partitionList(String pastureName, String id, Integer page, Integer pageSize) {
        Page<IvLivestockDTO> pageNum = new Page<>(page, pageSize);
        Page<IvLivestockDTO> resultPage = baseMapper.selectPartitionWithPagination(pageNum, pastureName, id);
        return ResultVO.succeed(resultPage);
    }

    /**
     * 收获分区
     * @param ids 分区ID列表
     * @return ResultVO 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO harvestPartition(List<String> ids) {
        LambdaUpdateWrapper<FishPartition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FishPartition::getId, ids)
                .set(FishPartition::getStatus, CommonContant.LIVESTOCK_STATE_OUT_FENCE).set(FishPartition::getHarvestDate, new Date());
        boolean updateResult = baseMapper.update(null, updateWrapper) > 0;

        List<FishPartition> ivLivestocks = baseMapper.selectBatchIds(ids);
        // 出栏上链操作
        harvestToChain(ivLivestocks);
        return ResultVO.succeed();
    }

    /**
     * 上链操作
     * @param ivLivestocks 分区列表
     */
    public void harvestToChain(List<FishPartition> ivLivestocks) {
        ArrayList<String> listContract = new ArrayList<>();
        for (FishPartition ivLivestock : ivLivestocks) {
            listContract.add(ivLivestock.getContractAddr());
        }
        // 上链
        PlatformOffHarvestInputBO pohi = new PlatformOffHarvestInputBO();
        pohi.set_partitionsss(listContract);
        try {
            TransactionResponse ot = platformService.offHarvest(pohi);
            if (!ot.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 收获整个大棚
     * @param pastureId 大棚ID
     * @return ResultVO 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO harvestPasture(String pastureId) {
        List<FishPartition> ivLivestocks = baseMapper.selectList(new LambdaQueryWrapper<FishPartition>()
                .eq(FishPartition::getPastureId, pastureId).eq(FishPartition::getStatus, CommonContant.LIVESTOCK_STATE_IN_FENCE));

        LambdaUpdateWrapper<FishPartition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FishPartition::getPastureId, pastureId).eq(FishPartition::getStatus, CommonContant.LIVESTOCK_STATE_IN_FENCE)
                .set(FishPartition::getStatus, CommonContant.LIVESTOCK_STATE_OUT_FENCE).set(FishPartition::getHarvestDate, new Date());
        baseMapper.update(null, updateWrapper);

        // 出栏上链操作
        harvestToChain(ivLivestocks);
        return ResultVO.succeed();
    }

    /**
     * 完成处理流程
     * @param id 分区ID
     * @return ResultVO 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO finishProcess(String id) {
        FishPartition ivLivestock = baseMapper.selectById(id);
        if (ivLivestock == null) {
            return ResultVO.failed(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (ivLivestock.getStatus() != CommonContant.LIVESTOCK_STATE_OUT_FENCE) {
            return ResultVO.failed(ErrorCodeEnum.PARTITION_NOT_HARVEST);
        }

        if (ivLivestock.getProcessState() != CommonContant.LIVESTOCK_STATE_KILLING) {
            return ResultVO.failed(ErrorCodeEnum.LIVESTOCK_NOT_PROCESSING);
        }

        LambdaUpdateWrapper<FishPartition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FishPartition::getId, id)
                .set(FishPartition::getProcessState, CommonContant.LIVESTOCK_STATE_KILLED).set(FishPartition::getProcessDate, new Date());
        baseMapper.update(null, updateWrapper);

        // 上链
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        // 牛地址
        String cattleContract = ivLivestock.getContractAddr();
        PartitionsService partitionsService = new PartitionsService(client, cryptoKeyPair, cattleContract);
        try {
            TransactionResponse complete = partitionsService.completeProcessing();
            if (!complete.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultVO.succeed();
    }

    /**
     * 获取收获列表
     * @param pastureName 牧场名称
     * @param id 分区ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return ResultVO 返回操作结果
     */
    public ResultVO harvestList(String pastureName, String id, Integer page, Integer pageSize) {
        Page<IvLivestockOutDTO> pageNum = new Page<>(page, pageSize);
        Page<IvLivestockOutDTO> resultPage = baseMapper.selectLivestockOut(pageNum, pastureName, id);
        return ResultVO.succeed(resultPage);
    }

    /**
     * 获取所有分区信息
     * @return ResultVO 返回分区列表
     */
    public ResultVO getList() {
        List<FishPartition> iaPartitions = this.baseMapper.selectList(new LambdaQueryWrapper<FishPartition>().eq(FishPartition::getStatus, 0).eq(FishPartition::getProcessState, 0));
        return ResultVO.succeed(iaPartitions);
    }

    /**
     * 处理出栏的牧场
     * @param ids 牧场ID列表
     * @return ResultVO 返回操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO outPastures(List<String> ids) {
        List<FishPartition> ivLivestocks = baseMapper
                .selectList(new LambdaQueryWrapper<FishPartition>().in(FishPartition::getPastureId, ids)
                        .eq(FishPartition::getStatus, CommonContant.LIVESTOCK_STATE_IN_FENCE));

        LambdaUpdateWrapper<FishPartition> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(FishPartition::getPastureId, ids).eq(FishPartition::getStatus, CommonContant.LIVESTOCK_STATE_IN_FENCE)
                .set(FishPartition::getStatus, CommonContant.LIVESTOCK_STATE_OUT_FENCE).set(FishPartition::getHarvestDate, new Date());

        int updatedRows = baseMapper.update(null, updateWrapper);

        // 出栏上链操作
        harvestToChain(ivLivestocks);

        if (updatedRows > 0) {
            return ResultVO.succeed("成熟成功");
        } else {
            return ResultVO.failed("成熟失败");
        }
    }

}
