package com.frog.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.agriculture.domain.CropBatch;
import com.frog.agriculture.mapper.CropBatchMapper;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import org.fisco.bcos.sdk.transaction.model.dto.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.domain.PastureBatchTask;
import com.frog.mapper.PastureBatchTaskMapper;
import com.frog.service.IPastureBatchTaskService;
import vip.blockchain.agriculture.model.bo.PlatformOffHarvestInputBO;
import vip.blockchain.agriculture.service.PlatformService;

import javax.annotation.Resource;

/**
 * 批次任务Service业务层处理
 *
 * @author xuweidong
 * @date 2023-05-24
 */
@Service
public class PastureBatchTaskServiceImpl implements IPastureBatchTaskService
{
    @Autowired
    private PastureBatchTaskMapper pastureBatchTaskMapper;

    @Autowired
    private CropBatchMapper cropBatchMapper;

    @Resource
    PlatformService platformService;

    /**
     * 查询批次任务
     *
     * @param taskId 批次任务主键
     * @return 批次任务
     */
    @Override
    public PastureBatchTask selectPastureBatchTaskByTaskId(Long taskId)
    {
        return pastureBatchTaskMapper.selectPastureBatchTaskByTaskId(taskId);
    }

    /**
     * 查询批次任务列表
     *
     * @param batchTask 批次任务
     * @return 批次任务
     */
    @Override
    public List<PastureBatchTask> selectPastureBatchTaskList(PastureBatchTask batchTask)
    {
        Long userId = SecurityUtils.getUserId();
        if(!SecurityUtils.isAdmin(userId)){
            batchTask.getParams().put("batchHead", userId);
        }
        return pastureBatchTaskMapper.selectPastureBatchTaskList(batchTask);
    }


    /**
     * 新增批次任务
     *
     * @param batchTask 批次任务
     * @return 结果
     */
    @Override
    public int insertPastureBatchTask(PastureBatchTask batchTask)
    {
        batchTask.setCreateBy(SecurityUtils.getUserId().toString());
        batchTask.setCreateTime(DateUtils.getNowDate());
        return pastureBatchTaskMapper.insertPastureBatchTask(batchTask);
    }

    /**
     * 修改批次任务
     *
     * @param batchTask 批次任务
     * @return 结果
     */
    @Override
    public int updatePastureBatchTask(PastureBatchTask batchTask) {
        // 设置更新者为当前用户的ID
        batchTask.setUpdateBy(SecurityUtils.getUserId().toString());
        // 设置更新时间为当前时间
        batchTask.setUpdateTime(DateUtils.getNowDate());
        // 调用数据访问层更新批次任务，并返回受影响的行数
        int i = pastureBatchTaskMapper.updatePastureBatchTask(batchTask);
        // 每次更新检查一下任务是否都已经完成
        HashMap<String, Long> hm = pastureBatchTaskMapper.selectPastureFinishTask(batchTask.getBatchId());
        // 根据批次ID查询对应的CropBatch对象
        CropBatch cropBatch = cropBatchMapper.selectCropBatchByBatchId(batchTask.getBatchId());
        // 检查完成的任务数量，如果为0表示任务已完成
        if (hm.get("num") == 0) {
            // 更新CropBatch状态为"1"，表示已完成
            cropBatch.setStatus("1");
            // 更新数据库中的CropBatch状态
            cropBatchMapper.updateCropBatch(cropBatch);
            // 创建一个合约地址列表
            ArrayList<String> listContract = new ArrayList<>();
            // 再次查询CropBatch数据以获取合约地址
            CropBatch data = cropBatchMapper.selectCropBatchByBatchId(batchTask.getBatchId());
            // 将合约地址添加到列表中
            listContract.add(data.getContractAddress());
            // for (IaPartition ivLivestock : ivLivestocks) {
            //     listContract.add(ivLivestock.getContractAddr());
            // }
            // 准备上链操作
            PlatformOffHarvestInputBO pohi = new PlatformOffHarvestInputBO();
            // 设置合约地址列表
            pohi.set_partitionsss(listContract);
            try {
                // 调用平台服务执行上链操作
                TransactionResponse ot = platformService.offHarvest(pohi);
                // 检查上链结果，如果不成功则抛出异常
                if (!ot.getReceiptMessages().equals(CommonContant.SUCCESS_MESSAGE)) {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                // 捕获异常并抛出运行时异常
                throw new RuntimeException(e);
            }
        } else {
            // 如果任务未完成，则将CropBatch状态设为"0"
            cropBatch.setStatus("0");
            // 更新数据库中的CropBatch状态
            cropBatchMapper.updateCropBatch(cropBatch);
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
    public int deletePastureBatchTaskByTaskIds(Long[] taskIds)
    {
        return pastureBatchTaskMapper.deletePastureBatchTaskByTaskIds(taskIds);
    }

    /**
     * 删除批次任务信息
     *
     * @param taskId 批次任务主键
     * @return 结果
     */
    @Override
    public int deletePastureBatchTaskByTaskId(Long taskId)
    {
        return pastureBatchTaskMapper.deletePastureBatchTaskByTaskId(taskId);
    }

    /**
     * 给手机端的任务列表
     * @param batchTask
     * @return
     */
    @Override
    public List<PastureBatchTask> selectPastureBatchTaskListToMobile(PastureBatchTask batchTask) {
        batchTask.setBatchHead(SecurityUtils.isAdmin(SecurityUtils.getUserId())?null:SecurityUtils.getUserId());
        return pastureBatchTaskMapper.selectPastureBatchTaskListToMobile(batchTask);
    }
}
