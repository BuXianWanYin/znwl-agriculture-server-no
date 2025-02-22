package com.frog.service.impl;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import cn.hutool.core.collection.Partition;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frog.IaAgriculture.config.BarcodeConfig;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.dto.IaPartitionFoodPageDTO;
import com.frog.IaAgriculture.exception.ServerException;
import com.frog.IaAgriculture.model.IaPartitionFood;
import com.frog.IaAgriculture.model.entity.IaPartition;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;
import com.frog.domain.PastureBatch;
import com.frog.dto.FishPartitionFoodPageDTO;
import com.frog.mapper.FishPartitionFoodMapper;
import com.frog.mapper.FishPartitionMapper;
import com.frog.mapper.PastureBatchMapper;
import com.frog.model.FishPartitionFood;
import com.frog.model.entity.FishPartition;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.blockchain.agriculture.model.bo.PartitionsAddFoodInputBO;
import vip.blockchain.agriculture.model.bo.PartitionsModifyFoodInputBO;
import vip.blockchain.agriculture.model.bo.PartitionsRemoverFoodInputBO;
import vip.blockchain.agriculture.service.PartitionsService;
import vip.blockchain.agriculture.utils.BarcodeUtil;
import vip.blockchain.agriculture.utils.BaseUtil;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

//采摘Service
@Service
public class FishPartitionFoodService extends ServiceImpl<FishPartitionFoodMapper, FishPartitionFood> {

    @Autowired
    private Client client;
    @Autowired
    private FishPartitionMapper fishPartitionMapper;
    @Autowired
    private BarcodeConfig barcodeConfig;
    @Autowired
    private PastureBatchMapper pastureBatchMapper;   //CropBatchMapper cropBatchMapper;


    @Transactional(rollbackFor = Exception.class)
    public ResultVO create(FishPartitionFood iaPartitionFood) {

        PastureBatch cropBatch= pastureBatchMapper.selectPastureBatchByBatchId(Long.valueOf(iaPartitionFood.getFishPartitionId()));
        FishPartition iaPartition = this.fishPartitionMapper.selectById(iaPartitionFood.getFishPartitionId());
        if (Objects.isNull(cropBatch)) {
            return ResultVO.failed("分区不存在");
        }
        if (cropBatch.getStatus().equals( "0")) {
            return ResultVO.failed("未成熟");
        }

        FishPartitionFood insertBean = new FishPartitionFood();
        insertBean.setFishPartitionId(cropBatch.getLandId().toString());
        BeanUtils.copyProperties(iaPartitionFood, insertBean);
        insertBean.setId(BaseUtil.getSnowflakeId());
        super.save(insertBean);
        PartitionsService partitionsService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), cropBatch.getContractAddress());
        PartitionsAddFoodInputBO input = new PartitionsAddFoodInputBO();
        input.set_foodName(insertBean.getName());
        input.set_id(insertBean.getId());
        input.set_notes(insertBean.getDescription());
        input.set_weight(insertBean.getWeight().toEngineeringString());
        input.set_quality(BigInteger.valueOf(insertBean.getStatus()));

        try {
            TransactionResponse transactionResponse = partitionsService.addFood(input);
            if (Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) {

            } else {
                throw new ServerException(transactionResponse.getReceiptMessages());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
        }
        return ResultVO.succeed(insertBean);
    }

    //删除
    @Transactional(rollbackFor = Exception.class)
    public ResultVO delete(String id) {
        FishPartitionFood partitionFood = super.getById(id);
        if (Objects.isNull(partitionFood)) {
            return ResultVO.failed();
        }
        FishPartition iaPartition = this.fishPartitionMapper.selectById(partitionFood.getFishPartitionId());
        super.removeById(id);
        try {

            PartitionsService partitionsService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), iaPartition.getContractAddr());

            PartitionsRemoverFoodInputBO input = new PartitionsRemoverFoodInputBO();
            input.set_id(partitionFood.getId());

            TransactionResponse transactionResponse = partitionsService.removerFood(input);
            if (Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) {

            } else {
                throw new ServerException(transactionResponse.getReceiptMessages());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
        }
        return ResultVO.succeed();
    }


    @Transactional(rollbackFor = Exception.class)
    public ResultVO update(FishPartitionFood iaPartitionFood) {
        iaPartitionFood.setFishPartitionId(null);
        String id = iaPartitionFood.getId();
        FishPartitionFood feeding = super.getById(id);
        if (Objects.isNull(feeding)) {
            return ResultVO.failed();
        }
        String iaPartitionId = feeding.getFishPartitionId();
        FishPartition iaPartition = this.fishPartitionMapper.selectById(iaPartitionId);

        super.updateById(iaPartitionFood);

        try {

            PartitionsService partitionsService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), iaPartition.getContractAddr());

            PartitionsModifyFoodInputBO input = new PartitionsModifyFoodInputBO();
            input.set_foodName(iaPartitionFood.getName());
            input.set_id(iaPartitionFood.getId());
            input.set_weight(iaPartitionFood.getWeight().toEngineeringString());
            input.set_notes(iaPartitionFood.getDescription());
            input.set_quality(BigInteger.valueOf(iaPartitionFood.getStatus()));

            TransactionResponse transactionResponse = partitionsService.modifyFood(input);
            if (Objects.equals(transactionResponse.getReceiptMessages(), CommonContant.SUCCESS_MESSAGE)) {

            } else {
                throw new ServerException(transactionResponse.getReceiptMessages());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR);
        }

        return ResultVO.succeed();
    }

    public ResultVO detail(String id) {
        FishPartitionFood iaFeeding = this.baseMapper.selectById(id);

        return ResultVO.succeed(iaFeeding);
    }

    public ResultVO page(FishPartitionFoodPageDTO baseDTO) {
        QueryWrapper wrapper = new QueryWrapper<Map<String, Object>>();

        if (StringUtils.isNoneBlank(baseDTO.getKeyword())) {
            wrapper.like("name", baseDTO.getKeyword());
        }
        wrapper.eq("fish_partition_id", baseDTO.getPartitionId());

        Page<FishPartitionFood> page = new Page<>(baseDTO.getCurrentPage(), baseDTO.getPageSize());
        Page<FishPartitionFood> p = this.baseMapper.selectPage(page, wrapper);

        //生成前端访问页面的条形二维码
        p.getRecords().forEach(bean -> {
            try {
                bean.setBarcode(BarcodeUtil.getQRCode(barcodeConfig.getPath() + bean.getId()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return ResultVO.succeed(p);
    }


}