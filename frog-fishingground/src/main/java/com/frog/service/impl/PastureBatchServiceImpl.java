package com.frog.service.impl;


import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.hutool.core.util.StrUtil;
import com.frog.IaAgriculture.dto.ErrorCodeEnum;
import com.frog.IaAgriculture.exception.ServerException;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.agriculture.domain.StandardJob;
import com.frog.agriculture.domain.TaskLog;
import com.frog.agriculture.mapper.TaskLogMapper;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.FishBatchTask;
import com.frog.domain.PastureBatch;
import com.frog.mapper.FishBatchTaskMapper;
import com.frog.mapper.FishPastureMapper;
import com.frog.mapper.PastureBatchMapper;
import com.frog.model.FishPasture;
import com.frog.service.FishPondTraceabData;
import com.frog.service.IspeciesService;
import com.frog.service.PastureBatchService;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.StandardJobMapper;
import org.springframework.transaction.annotation.Transactional;
import vip.blockchain.agriculture.service.PlatformService;
import vip.blockchain.fishsheService.FishTraceabFrameService;
import vip.blockchain.model.po.Hbase.FishshedInputBo;

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
    private FishBatchTaskMapper fishBatchTaskMapper;
    @Autowired
    private TaskLogMapper taskLogMapper;

    @Autowired
    private FishTraceabFrameService fishTraceabFrameService;
    @Autowired
    private IspeciesService speciesService;
    @Autowired
    Client client;
    private FishPondTraceabData fishPondTraceabData;

    @Resource
    FishPastureMapper fishPastureMapper;

    @Resource
    PlatformService platformService;

    /**
     * 查询鱼物批次
     *
     * @param batchId 鱼物批次主键
     * @return 鱼物批次
     */
    @Override
    public PastureBatch selectPastureBatchByBatchId(Long batchId) {
        return pastureBatchMapper.selectPastureBatchByBatchId(batchId);
    }

    /**
     * 查询鱼物批次列表
     *
     * @param PastureBatch 鱼物批次
     * @return 鱼物批次
     */
    @Override
    public List<PastureBatch> selectPastureBatchList(PastureBatch PastureBatch) {
        Long userId = SecurityUtils.getUserId();
        if (!SecurityUtils.isAdmin(userId)) {
            PastureBatch.setBatchHead(SecurityUtils.getUserId());
        }
        return pastureBatchMapper.selectPastureBatchList(PastureBatch);
    }

    /**
     * 新增鱼物批次
     *
     * @param PastureBatch 鱼物批次
     * @return 结果
     */
    @Override
    @Transactional
    public int insertPastureBatch(PastureBatch PastureBatch) {


        // 根据土地ID查询相应的IaPasture对象
        FishPasture iaPasture = fishPastureMapper.selectById(PastureBatch.getLandId().toString());
        // 获取对应的合约地址
        String psContractAddr = iaPasture.getContractAddr();
        // 如果IaPasture对象为空或者合约地址为空，返回0表示失败
        if (iaPasture == null || StrUtil.isBlank(psContractAddr)) {
            return 0;
        }
        int remainingDigits = 12;
        long min = (long) Math.pow(10, remainingDigits - 1);
        long max = (long) Math.pow(10, remainingDigits) - 1;
        Random random = new Random();
        long randomPart = min + (long) (random.nextDouble() * (max - min + 1));
        // 生成一个新的唯一ID作为批次ID
        long batchId = Long.parseLong("1889" + randomPart);
        // 设置鱼物批次的ID
        PastureBatch.setBatchId(batchId);
        // 设置创建时间为当前时间
        PastureBatch.setCreateTime(DateUtils.getNowDate());
        // 设置创建者为当前用户的ID
        PastureBatch.setCreateBy(SecurityUtils.getUserId().toString());

        // 上链操作
        Date now = new Date();  // 获取当前时间

        FishshedInputBo fishshedInputBo = new FishshedInputBo();
        SecureRandom secureRandom = new SecureRandom();
        fishshedInputBo.setTraceabId(PastureBatch.getBatchId());
        fishshedInputBo.setFishVarieties(String.valueOf(PastureBatch.getSpeciesId()));
        fishshedInputBo.setPondName(PastureBatch.getBatchName());
        fishshedInputBo.setReleaseDate(String.valueOf(PastureBatch.getStartTime()));
        fishshedInputBo.setCropArea(String.valueOf(PastureBatch.getCropArea()));
        fishshedInputBo.setNotes(PastureBatch.getCreateBy());
        fishshedInputBo.setBreedingBatchName(PastureBatch.getBatchName());
        fishshedInputBo.setOfFishFarm(psContractAddr);

        try {
            // 调用平台服务进行上链
            TransactionResponse transactionResponse = fishTraceabFrameService.createBatch(fishshedInputBo.getFieldValuesAsList());
            if (transactionResponse.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(fishshedInputBo.getTraceabId());
                String contractAddress = fishTraceabFrameService.getTraceAddress(objects).getValues();
                // 设置鱼物批次的合约地址
                PastureBatch.setContractAddress(contractAddress.substring(2, contractAddress.length() - 2));
            } else {
                throw new RuntimeException();  // 如果上链失败，抛出异常
            }
        } catch (Exception e) {
            throw new RuntimeException(e);  // 捕获异常并抛出
        }

        // 结束上链操作，插入鱼物批次到数据库
        int i = pastureBatchMapper.insertPastureBatch(PastureBatch);
        // 创建查询条件，查询标准作业列表
        StandardJob queryPar = new StandardJob();
        queryPar.setGermplasmId(PastureBatch.getSpeciesId());
        // 查询标准作业列表
        List<StandardJob> sjList = standardJobMapper.selectStandardJobList(queryPar);

        try {
            this.fishPondTraceabData = FishPondTraceabData.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
        } catch (ContractException e) {
            e.printStackTrace();
        }

        // 遍历标准作业列表，生成批次任务
        for (StandardJob sj : sjList) {
            FishBatchTask bt = new FishBatchTask();
            // 设置批次任务的相关信息
            bt.setBatchId(PastureBatch.getBatchId());
            bt.setTaskHead(PastureBatch.getBatchHead());
            bt.setTaskName(sj.getJobName());

            // 计算周期单位，如果单位为"0"，则周期为1，否则为7
            int mult = sj.getCycleUnit().equals("0") ? 1 : 7;
            try {
                // 设置计划开始时间和计划完成时间
                bt.setPlanStart(DateUtils.plusDay((int) (sj.getJobStart() * mult), PastureBatch.getStartTime()));
                bt.setPlanFinish(DateUtils.plusDay((int) (sj.getJobFinish() * mult), PastureBatch.getStartTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);  // 捕获解析异常并抛出
            }
            // 插入批次任务到数据库
            fishBatchTaskMapper.insertBatchTask(bt);
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

//    public boolean createBatch(PastureBatch pastureBatch) throws ABICodecException, TransactionBaseException, IOException {
//        FishshedInputBo fishshedInputBo = new FishshedInputBo();
//        fishshedInputBo.setTraceabId(pastureBatch.getLandId());
//        fishshedInputBo.setFishVarieties(String.valueOf(pastureBatch.getSpeciesId()));
//        fishshedInputBo.setPondName(pastureBatch.getBatchName());
//        fishshedInputBo.setReleaseDate(String.valueOf(pastureBatch.getStartTime()));
//        fishshedInputBo.setCropArea(String.valueOf(pastureBatch.getCropArea()));
//        fishshedInputBo.setNotes("test");
//        fishshedInputBo.setBreedingBatchName(pastureBatch.getBatchName());
//        fishshedInputBo.setOfFishFarm(String.valueOf(pastureBatch.getSpeciesId()));
//        try {
//            TransactionResponse batch = fishTraceabFrameService.createBatch(fishshedInputBo.getFieldValuesAsList());
//        }catch (RuntimeException ex){
//            throw new RuntimeException("区块链出错!");
//        }
//
//        return true;
//    }

    /**
     * 修改鱼物批次
     *
     * @param PastureBatch 鱼物批次
     * @return 结果
     */
    @Override
    public int updatePastureBatch(PastureBatch PastureBatch) {
        PastureBatch.setUpdateTime(DateUtils.getNowDate());
        PastureBatch.setUpdateBy(SecurityUtils.getUserId().toString());
        try {
            this.fishPondTraceabData = FishPondTraceabData.load(PastureBatch.getContractAddress(),client, client.getCryptoSuite().getCryptoKeyPair());
            TransactionReceipt transactionReceipt = this.fishPondTraceabData.modifyPondInfo(PastureBatch.getBatchName(), String.valueOf(PastureBatch.getSpeciesId()), PastureBatch.getBatchName(), String.valueOf(PastureBatch.getStartTime()), PastureBatch.getCreateBy());
            if (transactionReceipt.isStatusOK()) {
                // 如果响应成功
                String values = this.fishPondTraceabData.getContractAddress();
                if (StringUtils.isBlank(values)) { // 如果合同地址为空
                    throw new ServerException("合约地址不存在"); // 抛出服务器异常
                }
                PastureBatch.setContractAddress(values); // 设置合同地址
            } else {
                throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器错误异常
            }
        } catch (Exception e) { // 捕获异常
            e.printStackTrace(); // 打印异常
            throw new ServerException(ErrorCodeEnum.CONTENT_SERVER_ERROR); // 抛出服务器错误异常
        }
        return pastureBatchMapper.updatePastureBatch(PastureBatch);
    }

    /**
     * 批量删除鱼物批次
     *
     * @param batchIds 需要删除的鱼物批次主键
     * @return 结果
     */
    @Override
    public int deletePastureBatchByBatchIds(Long[] batchIds) {
        return pastureBatchMapper.deletePastureBatchByBatchIds(batchIds);
    }

    /**
     * 删除鱼物批次信息
     *
     * @param batchId 鱼物批次主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deletePastureBatchByBatchIds(Long batchId) {
        FishBatchTask fishBatchTask = new FishBatchTask();
        fishBatchTask.setBatchId(batchId);
        fishBatchTask.setDelFlag("2");//设置成删除
        fishBatchTaskMapper.updateBatchTaskWhereBatchId(fishBatchTask);
        return pastureBatchMapper.deletePastureBatchByBatchId(batchId);
    }

    /**
     * 给手机端批次列表查询数据
     *
     * @param PastureBatch
     * @return
     */
    @Override
    public List<PastureBatch> selectPastureBatchListToMobile(PastureBatch PastureBatch) {
        //非管理员只能看批次负责人为自己的批次
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            PastureBatch.setBatchHead(SecurityUtils.getUserId());
        }
        return pastureBatchMapper.selectPastureBatchListToMobile(PastureBatch);
    }
}

