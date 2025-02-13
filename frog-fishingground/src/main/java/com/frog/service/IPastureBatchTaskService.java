package com.frog.service;

import java.util.List;
import com.frog.domain.PastureBatchTask;

/**
 * 批次任务Service接口
 *
 * @author xuweidong
 * @date 2023-05-24
 */
public interface IPastureBatchTaskService
{
    /**
     * 查询批次任务
     *
     * @param taskId 批次任务主键
     * @return 批次任务
     */
    public PastureBatchTask selectBatchTaskByTaskId(Long taskId);

    /**
     * 查询批次任务列表
     *
     * @param batchTask 批次任务
     * @return 批次任务集合
     */
    public List<PastureBatchTask> selectBatchTaskList(PastureBatchTask batchTask);

    /**
     * 新增批次任务
     *
     * @param batchTask 批次任务
     * @return 结果
     */
    public int insertBatchTask(PastureBatchTask batchTask);

    /**
     * 修改批次任务
     *
     * @param batchTask 批次任务
     * @return 结果
     */
    public int updateBatchTask(PastureBatchTask batchTask);

    /**
     * 批量删除批次任务
     *
     * @param taskIds 需要删除的批次任务主键集合
     * @return 结果
     */
    public int deleteBatchTaskByTaskIds(Long[] taskIds);

    /**
     * 删除批次任务信息
     *
     * @param taskId 批次任务主键
     * @return 结果
     */
    public int deleteBatchTaskByTaskId(Long taskId);

    /**
     * 给手机端的任务列表
     * @param batchTask
     * @return
     */
    public List<PastureBatchTask> selectBatchTaskListToMobile(PastureBatchTask batchTask);
}
