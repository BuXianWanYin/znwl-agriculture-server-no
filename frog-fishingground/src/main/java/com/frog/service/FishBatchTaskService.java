package com.frog.service;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.domain.FishBatchTask;

import java.util.List;



public interface FishBatchTaskService {
    /**
     * 查询批次任务
     *
     * @param taskId 批次任务主键
     * @return 批次任务
     */
    public FishBatchTask selectBatchTaskByTaskId(Long taskId);

    /**
     * 查询批次任务列表
     *
     * @param fishBatchTask 批次任务
     * @return 批次任务集合
     */
    public List<FishBatchTask> selectBatchTaskList(FishBatchTask fishBatchTask);

    /**
     * 新增批次任务
     *
     * @param fishBatchTask 批次任务
     * @return 结果
     */
    public int insertBatchTask(FishBatchTask fishBatchTask);

    /**
     * 修改批次任务
     *
     * @param fishBatchTask 批次任务
     * @return 结果
     */
    public int updateBatchTask(FishBatchTask fishBatchTask);

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
     *
     * @param fishBatchTask
     * @return
     */
    public List<FishBatchTask> selectBatchTaskListToMobile(FishBatchTask fishBatchTask);
}
