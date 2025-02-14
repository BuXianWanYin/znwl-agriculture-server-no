package com.frog.mapper;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.agriculture.domain.BatchTask;
import com.frog.domain.FishBatchTask;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * 批次任务Mapper接口
 *
 * @author qgj
 * @date 2025-02-14
 */
public interface FishBatchTaskMapper {
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

    public HashMap selectFinishTask(@Param("batchId") Long batchId);

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
     * 删除批次任务
     *
     * @param taskId 批次任务主键
     * @return 结果
     */
    public int deleteBatchTaskByTaskId(Long taskId);

    /**
     * 批量删除批次任务
     *
     * @param taskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBatchTaskByTaskIds(Long[] taskIds);

    /**
     * 给手机端的任务列表
     * @param fishBatchTask
     * @return
     */
    public List<FishBatchTask> selectBatchTaskListToMobile(FishBatchTask fishBatchTask);
}
