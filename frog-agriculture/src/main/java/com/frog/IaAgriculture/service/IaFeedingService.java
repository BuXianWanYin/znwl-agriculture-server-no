package com.frog.IaAgriculture.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.frog.IaAgriculture.exception.ServerException;
import com.frog.IaAgriculture.mapper.IaFeedingMapper;
import com.frog.IaAgriculture.mapper.IaPartitionMapper;
import com.frog.IaAgriculture.model.IaFeeding;
import vip.blockchain.agriculture.model.bo.PartitionsAddFertilizerInputBO;
import vip.blockchain.agriculture.model.bo.PartitionsModifyFertilizerInputBO;
import com.frog.IaAgriculture.model.entity.IaPartition;
import vip.blockchain.agriculture.service.PartitionsService;
import vip.blockchain.agriculture.utils.*;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

@Service
public class IaFeedingService extends ServiceImpl<IaFeedingMapper, IaFeeding> {

    @Autowired
    private Client client;
    @Autowired
    private IaPartitionMapper iaPartitionMapper;

    @Transactional(rollbackFor = Exception.class)
    public ResultVO create(IaFeeding iaFeeding) {

        IaPartition iaPartition = this.iaPartitionMapper.selectById(iaFeeding.getIaPartitionId());
        if (Objects.isNull(iaPartition)) {
            return ResultVO.failed("分区不存在");
        }
        //如果分区在加工和成熟了不能施肥
        if (iaPartition.getStatus() != 0 || iaPartition.getProcessState() != 0) {
            return ResultVO.failed("分区不能施肥");
        }

        IaFeeding insertBean = new IaFeeding();
        BeanUtils.copyProperties(iaFeeding, insertBean);
        insertBean.setId(BaseUtil.getSnowflakeId());
        super.save(insertBean);
        PartitionsService partitionsService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), iaPartition.getContractAddr());
        PartitionsAddFertilizerInputBO input = new PartitionsAddFertilizerInputBO();
        input.set_date(DateUtil.format(insertBean.getDate(), "yyyy-MM-dd HH:mm:ss"));
        input.set_id(BigInteger.valueOf(Long.parseLong(insertBean.getId())));
        input.set_notes(insertBean.getDescription());
        input.set_fertilizerNme(insertBean.getFoodName());
        input.set_fertilizerNumber(insertBean.getWeight().toEngineeringString());
        try {
            TransactionResponse transactionResponse = partitionsService.addFertilizer(input);
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
        super.removeById(id);
        return ResultVO.succeed();
    }


    @Transactional(rollbackFor = Exception.class)
    public ResultVO update(IaFeeding iaFeeding) {
        iaFeeding.setIaPartitionId(null);
        String id = iaFeeding.getId();
        IaFeeding feeding = super.getById(id);
        if (Objects.isNull(feeding)) {
            return ResultVO.failed();
        }
        super.updateById(iaFeeding);
        String iaPartitionId = feeding.getIaPartitionId();
        IaPartition iaPartition = this.iaPartitionMapper.selectById(iaPartitionId);

        //如果分区在加工和成熟了不能施肥
        if (iaPartition.getStatus() != 0 || iaPartition.getProcessState() != 0) {
            return ResultVO.failed("分区不能施肥");
        }
        try {

            PartitionsService partitionsService = new PartitionsService(client, client.getCryptoSuite().getCryptoKeyPair(), iaPartition.getContractAddr());

            PartitionsModifyFertilizerInputBO input = new PartitionsModifyFertilizerInputBO();
            input.set_date(DateUtil.format(iaFeeding.getDate(), "yyyy-MM-dd HH:mm:ss"));
            input.set_id(BigInteger.valueOf(Long.parseLong(iaFeeding.getId())));
            input.set_notes(iaFeeding.getDescription());
            input.set_fertilizerNme(iaFeeding.getFoodName());
            input.set_fertilizerNumber(iaFeeding.getWeight().toEngineeringString());

            TransactionResponse transactionResponse = partitionsService.modifyFertilizer(input);
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
        IaFeeding iaFeeding = this.baseMapper.selectById(id);

        return ResultVO.succeed(iaFeeding);
    }

    public ResultVO page(BaseDTO baseDTO) {
        QueryWrapper wrapper = new QueryWrapper<Map<String, Object>>();

        if (StringUtils.isNoneBlank(baseDTO.getKeyword())) {
            wrapper.like("ip.partition_name", baseDTO.getKeyword());
        }
        if (!StringUtils.isBlank(baseDTO.getStartTime())) {
            wrapper.ge("ia.date", baseDTO.getStartTime());
        }
        if (!StringUtils.isBlank(baseDTO.getEndTime())) {
            wrapper.le("ia.date", baseDTO.getEndTime());
        }
        Page<IaFeeding> page = new Page<>(baseDTO.getCurrentPage(), baseDTO.getPageSize());
        Page p = this.baseMapper.pageList(wrapper, page);

        return ResultVO.succeed(p);
    }

}
