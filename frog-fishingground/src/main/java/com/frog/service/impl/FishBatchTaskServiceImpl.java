package com.frog.service.impl;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.agriculture.domain.CropBatch;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.FishBatchTask;
import com.frog.domain.PastureBatch;
import com.frog.mapper.FishBatchTaskMapper;
import com.frog.mapper.PastureBatchMapper;
import com.frog.service.FishBatchTaskService;
import com.frog.service.FishPondTraceabData;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.blockchain.agriculture.model.bo.PlatformOffHarvestInputBO;
import vip.blockchain.agriculture.service.PlatformService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//鱼

@Service
public class FishBatchTaskServiceImpl implements FishBatchTaskService {

    //鱼批次任务
    @Autowired
    private FishBatchTaskMapper fishBatchTaskMapper;


    //鱼物批次
    @Autowired
    private PastureBatchMapper pastureBatchMapper;

    @Resource
    PlatformService platformService;

    @Autowired
    private Client client;
    private FishPondTraceabData fishPondTraceabData;

    /**
     * 查询批次任务
     *
     * @param taskId 批次任务主键
     * @return 批次任务
     */
    @Override
    public FishBatchTask selectBatchTaskByTaskId(Long taskId)
    {
        return fishBatchTaskMapper.selectBatchTaskByTaskId(taskId);
    }

    /**
     * 查询批次任务列表
     *
     * @param fishBatchTask 批次任务
     * @return 批次任务
     */
    @Override
    public List<FishBatchTask> selectBatchTaskList(FishBatchTask fishBatchTask)
    {
//        Long userId = SecurityUtils.getUserId();
//        if(!SecurityUtils.isAdmin(userId)){
//            fishBatchTask.getParams().put("batchHead", userId);
//        }
        return fishBatchTaskMapper.selectBatchTaskList(fishBatchTask);
    }


    /**
     * 新增批次任务
     *
     * @param fishBatchTask 批次任务
     * @return 结果
     */
    @Override
    public int insertBatchTask(FishBatchTask fishBatchTask)
    {
        fishBatchTask.setCreateBy(SecurityUtils.getUserId().toString());
        fishBatchTask.setCreateTime(DateUtils.getNowDate());
        return fishBatchTaskMapper.insertBatchTask(fishBatchTask);
    }

    /**
     * 修改批次任务
     *
     * @param fishBatchTask 批次任务
     * @return 结果
     */
    @Override
    public int updateBatchTask(FishBatchTask fishBatchTask) {
        // 设置更新者为当前用户的ID
        fishBatchTask.setUpdateBy(SecurityUtils.getUserId().toString());
        // 设置更新时间为当前时间
        fishBatchTask.setUpdateTime(DateUtils.getNowDate());
        // 调用数据访问层更新批次任务，并返回受影响的行数
        int i = fishBatchTaskMapper.updateBatchTask(fishBatchTask);
        // 每次更新检查一下任务是否都已经完成
        HashMap<String, Long> hm = fishBatchTaskMapper.selectFinishTask(fishBatchTask.getBatchId());
        // 根据批次ID查询对应的CropBatch对象
        PastureBatch pastureBatch = pastureBatchMapper.selectPastureBatchByBatchId(fishBatchTask.getBatchId());
        // 检查完成的任务数量，如果为0表示任务已完成
        if (hm.get("num") == 0) {
            // 更新CropBatch状态为"1"，表示已完成
            pastureBatch.setStatus("1");
            // 更新数据库中的CropBatch状态
            pastureBatchMapper.updatePastureBatch(pastureBatch);
            try {
                // 调用平台服务执行上链操作
                this.fishPondTraceabData = FishPondTraceabData.load(pastureBatch.getContractAddress(), client, client.getCryptoSuite().getCryptoKeyPair());
                // 检查上链结果，如果不成功则抛出异常
                TransactionReceipt transactionReceipt = fishPondTraceabData.modifyIsCaught(true);
                if (!transactionReceipt.isStatusOK()) {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                // 捕获异常并抛出运行时异常
                throw new RuntimeException(e);
            }
        } else {
            // 如果任务未完成，则将CropBatch状态设为"0"
            pastureBatch.setStatus("0");
            // 更新数据库中的CropBatch状态
            pastureBatchMapper.updatePastureBatch(pastureBatch);
        }
        // 返回更新操作影响的行数
        return i;
    }

    /**
     * 批量删除批次任务
     *
     * @param taskIds 需要删除的批次任务主键
     * @return 结果
     */
    @Override
    public int deleteBatchTaskByTaskIds(Long[] taskIds)
    {
        return fishBatchTaskMapper.deleteBatchTaskByTaskIds(taskIds);
    }

    /**
     * 删除批次任务信息
     *
     * @param taskId 批次任务主键
     * @return 结果
     */
    @Override
    public int deleteBatchTaskByTaskId(Long taskId)
    {
        return fishBatchTaskMapper.deleteBatchTaskByTaskId(taskId);
    }

    /**
     * 给手机端的任务列表
     *
     * @param fishBatchTask
     * @return
     */
    @Override
    public List<FishBatchTask> selectBatchTaskListToMobile(FishBatchTask fishBatchTask) {
        fishBatchTask.setBatchHead(SecurityUtils.isAdmin(SecurityUtils.getUserId())?null:SecurityUtils.getUserId());
        return fishBatchTaskMapper.selectBatchTaskListToMobile(fishBatchTask);
    }
}
