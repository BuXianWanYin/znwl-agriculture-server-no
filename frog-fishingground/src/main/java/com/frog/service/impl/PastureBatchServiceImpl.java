package com.frog.service.impl;


import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.frog.IaAgriculture.mapper.IaPastureMapper;
import com.frog.IaAgriculture.model.IaPasture;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;
import com.frog.agriculture.domain.BatchTask;
import com.frog.agriculture.domain.StandardJob;
import com.frog.agriculture.domain.TaskLog;
import com.frog.agriculture.mapper.BatchTaskMapper;
import com.frog.agriculture.mapper.TaskLogMapper;
import com.frog.agriculture.service.IGermplasmService;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.mapper.PastureBatchMapper;
import com.frog.service.PastureBatchService;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.StandardJobMapper;
import com.frog.agriculture.domain.CropBatch;
import org.springframework.transaction.annotation.Transactional;
import vip.blockchain.agriculture.model.bo.PlatformAddPartitionsInputBO;
import vip.blockchain.agriculture.service.PlatformService;
import vip.blockchain.agriculture.utils.BaseUtil;

import javax.annotation.Resource;

/**
 * 鱼物批次Service业务层处理
 *
 * @author aj
 * @date 2024-2-13
 */
@Service
public class PastureBatchServiceImpl implements PastureBatchService {
    @Autowired
    private PastureBatchMapper pastureBatchMapper;
    @Autowired
    private StandardJobMapper standardJobMapper;
    @Autowired
    private BatchTaskMapper batchTaskMapper;
    @Autowired
    private TaskLogMapper taskLogMapper;

    @Autowired
    private IGermplasmService germplasmService;

    @Resource
    IaPastureMapper iaPastureMapper;

    @Resource
    PlatformService platformService;

    /**
     * 查询鱼物批次
     *
     * @param batchId 鱼物批次主键
     * @return 鱼物批次
     */
    @Override
    public CropBatch selectCropBatchByBatchId(Long batchId) {
        return pastureBatchMapper.selectCropBatchByBatchId(batchId);
    }

    /**
     * 查询鱼物批次列表
     *
     * @param cropBatch 鱼物批次
     * @return 鱼物批次
     */
    @Override
    public List<CropBatch> selectCropBatchList(CropBatch cropBatch) {
        Long userId = SecurityUtils.getUserId();
        if (!SecurityUtils.isAdmin(userId)) {
            cropBatch.setBatchHead(SecurityUtils.getUserId());
        }
        return pastureBatchMapper.selectCropBatchList(cropBatch);
    }

    /**
     * 新增鱼物批次
     *
     * @param cropBatch 鱼物批次
     * @return 结果
     */
    @Override
    @Transactional
    public int insertCropBatch(CropBatch cropBatch) {


        // 根据土地ID查询相应的IaPasture对象
        IaPasture iaPasture = iaPastureMapper.selectById(cropBatch.getLandId().toString());
        // 获取对应的合约地址
        String psContractAddr = iaPasture.getContractAddr();
        // 如果IaPasture对象为空或者合约地址为空，返回0表示失败
        if (iaPasture == null || StrUtil.isBlank(psContractAddr)) {
            return 0;
        }
        // 生成一个新的唯一ID作为批次ID
        String snowflakeId = BaseUtil.getSnowflakeId();
        long batchId = Long.parseLong(snowflakeId);  // 将String转为long类型
        // 设置鱼物批次的ID
        cropBatch.setBatchId(batchId);
        // 设置创建时间为当前时间
        cropBatch.setCreateTime(DateUtils.getNowDate());
        // 设置创建者为当前用户的ID
        cropBatch.setCreateBy(SecurityUtils.getUserId().toString());

        // 上链操作
        Date now = new Date();  // 获取当前时间
        PlatformAddPartitionsInputBO partitionsInputBO = new PlatformAddPartitionsInputBO();
        // 设置上链所需参数
        partitionsInputBO.set_id(new BigInteger(String.valueOf(cropBatch.getBatchId())));
        partitionsInputBO.set_partitionsName(cropBatch.getBatchName());
        partitionsInputBO.set_notes(cropBatch.getRemark() == null ? " " : cropBatch.getRemark());
        partitionsInputBO.set_plantingName(germplasmService.selectGermplasmByGermplasmId(cropBatch.getGermplasmId()).getCropName());
        partitionsInputBO.set_plantingDate(DateUtil.format(now, "yyyy-MM-dd HH:mm:ss"));
        partitionsInputBO.set_plantingVarieties(cropBatch.getVariety() == null ? "种类为空" : cropBatch.getVariety());
        partitionsInputBO.set_ofGreenhouse(psContractAddr);  // 设置温室的合约地址

        try {
            // 调用平台服务进行上链
            TransactionResponse transactionResponse = platformService.addPartitions(partitionsInputBO);
            // 检查上链结果，如果成功则获取合约地址
            if (transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                String contractAddressArray = transactionResponse.getValues();
                JSONArray jsonArray = JSONUtil.parseArray(contractAddressArray);
                String contractAddress = jsonArray.getStr(0);  // 获取第一个合约地址
                // 设置鱼物批次的合约地址
                cropBatch.setContractAddress(contractAddress);
            } else {
                throw new RuntimeException();  // 如果上链失败，抛出异常
            }
        } catch (Exception e) {
            throw new RuntimeException(e);  // 捕获异常并抛出
        }

        // 结束上链操作，插入鱼物批次到数据库
        int i = pastureBatchMapper.insertCropBatch(cropBatch);
        // 创建查询条件，查询标准作业列表
        StandardJob queryPar = new StandardJob();
        queryPar.setGermplasmId(cropBatch.getGermplasmId());
        // 查询标准作业列表
        List<StandardJob> sjList = standardJobMapper.selectStandardJobList(queryPar);
        // 遍历标准作业列表，生成批次任务
        for (StandardJob sj : sjList) {
            BatchTask bt = new BatchTask();
            // 设置批次任务的相关信息
            bt.setBatchId(cropBatch.getBatchId());
            bt.setTaskHead(cropBatch.getBatchHead());
            bt.setTaskName(sj.getJobName());

            // 计算周期单位，如果单位为"0"，则周期为1，否则为7
            int mult = sj.getCycleUnit().equals("0") ? 1 : 7;
            try {
                // 设置计划开始时间和计划完成时间
                bt.setPlanStart(DateUtils.plusDay((int) (sj.getJobStart() * mult), cropBatch.getStartTime()));
                bt.setPlanFinish(DateUtils.plusDay((int) (sj.getJobFinish() * mult), cropBatch.getStartTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);  // 捕获解析异常并抛出
            }
            // 插入批次任务到数据库
            batchTaskMapper.insertBatchTask(bt);
            // 创建任务日志
            TaskLog tl = new TaskLog();
            tl.setTaskId(bt.getTaskId());
            tl.setOperName(SecurityUtils.getUsername());  // 操作用户名
            tl.setOperId(SecurityUtils.getUserId());  // 操作用户ID
            tl.setOperDes("创建任务");  // 操作描述
            tl.setCreateTime(DateUtils.getNowDate());  // 创建时间为当前时间
            // 插入任务日志到数据库
            taskLogMapper.insertTaskLog(tl);
        }

        // 返回插入鱼物批次的结果
        return i;
    }

    /**
     * 修改鱼物批次
     *
     * @param cropBatch 鱼物批次
     * @return 结果
     */
    @Override
    public int updateCropBatch(CropBatch cropBatch) {
        cropBatch.setUpdateTime(DateUtils.getNowDate());
        cropBatch.setUpdateBy(SecurityUtils.getUserId().toString());
        return pastureBatchMapper.updateCropBatch(cropBatch);
    }

    /**
     * 批量删除鱼物批次
     *
     * @param batchIds 需要删除的鱼物批次主键
     * @return 结果
     */
    @Override
    public int deleteCropBatchByBatchIds(Long[] batchIds) {
        return pastureBatchMapper.deleteCropBatchByBatchIds(batchIds);
    }

    /**
     * 删除鱼物批次信息
     *
     * @param batchId 鱼物批次主键
     * @return 结果
     */
    @Override
    public int deleteCropBatchByBatchId(Long batchId) {
        return pastureBatchMapper.deleteCropBatchByBatchId(batchId);
    }

    /**
     * 给手机端批次列表查询数据
     *
     * @param cropBatch
     * @return
     */
    @Override
    public List<CropBatch> selectCropBatchListToMobile(CropBatch cropBatch) {
        //非管理员只能看批次负责人为自己的批次
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            cropBatch.setBatchHead(SecurityUtils.getUserId());
        }
        return pastureBatchMapper.selectCropBatchListToMobile(cropBatch);
    }
}

