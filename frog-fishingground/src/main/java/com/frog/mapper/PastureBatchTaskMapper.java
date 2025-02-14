package com.frog.mapper;

import java.util.HashMap;
import java.util.List;
import com.frog.domain.PastureBatchTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 批次任务Mapper接口
 *
 * @author xuweidong
 * @date 2023-05-24
 */
public interface PastureBatchTaskMapper
{
    /**
     * 查询批次任务
     *
     * @param taskId 批次任务主键
     * @return 批次任务
     */
    public PastureBatchTask selectPastureBatchTaskByTaskId(Long taskId);

    /**
     * 查询批次任务列表
     *
     * @param pastureBatchTask 批次任务
     * @return 批次任务集合
     */
    public List<PastureBatchTask> selectPastureBatchTaskList(PastureBatchTask pastureBatchTask);

    public HashMap selectPastureFinishTask(@Param("batchId") Long batchId);

    /**
     * 新增批次任务
     *
     * @param pastureBatchTask 批次任务
     * @return 结果
     */
    public int insertPastureBatchTask(PastureBatchTask pastureBatchTask);

    /**
     * 修改批次任务
     *
     * @param pastureBatchTask 批次任务
     * @return 结果
     */
    public int updatePastureBatchTask(PastureBatchTask pastureBatchTask);

    /**
     * 删除批次任务
     *
     * @param taskId 批次任务主键
     * @return 结果
     */
    public int deletePastureBatchTaskByTaskId(Long taskId);

    /**
     * 批量删除批次任务
     *
     * @param taskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePastureBatchTaskByTaskIds(Long[] taskIds);

    /**
     * 给手机端的任务列表
     * @param pastureBatchTask
     * @return
     */
    public List<PastureBatchTask> selectPastureBatchTaskListToMobile(PastureBatchTask pastureBatchTask);
}
