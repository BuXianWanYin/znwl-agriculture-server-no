package com.frog.agriculture.service.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.frog.common.core.domain.model.AIStandardJobDTO;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.config.BotConfig;
import com.frog.utils.AIModelApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.StandardJobMapper;
import com.frog.agriculture.domain.StandardJob;
import com.frog.agriculture.service.IStandardJobService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标准作业任务Service业务层处理
 *
 * @author xuweidong
 * @date 2023-05-24
 */
@Service
public class StandardJobServiceImpl implements IStandardJobService {
    @Autowired
    private StandardJobMapper standardJobMapper;

    /**
     * 查询标准作业任务
     *
     * @param jobId 标准作业任务主键
     * @return 标准作业任务
     */
    @Override
    public StandardJob selectStandardJobByJobId(Long jobId) {
        return standardJobMapper.selectStandardJobByJobId(jobId);
    }

    /**
     * 查询标准作业任务列表
     *
     * @param standardJob 标准作业任务
     * @return 标准作业任务
     */
    @Override
    public List<StandardJob> selectStandardJobList(StandardJob standardJob) {
        return standardJobMapper.selectStandardJobList(standardJob);
    }

    /**
     * 新增标准作业任务
     *
     * @param standardJob 标准作业任务
     * @return 结果
     */
    @Override
    public int insertStandardJob(StandardJob standardJob) {
        standardJob.setCreateBy(SecurityUtils.getUserId().toString());
        standardJob.setCreateTime(DateUtils.getNowDate());
        return standardJobMapper.insertStandardJob(standardJob);
    }

    /**
     * 修改标准作业任务
     *
     * @param standardJob 标准作业任务
     * @return 结果
     */
    @Override
    public int updateStandardJob(StandardJob standardJob) {
        standardJob.setUpdateBy(SecurityUtils.getUserId().toString());
        standardJob.setUpdateTime(DateUtils.getNowDate());
        return standardJobMapper.updateStandardJob(standardJob);
    }

    /**
     * 批量删除标准作业任务
     *
     * @param jobIds 需要删除的标准作业任务主键
     * @return 结果
     */
    @Override
    public int deleteStandardJobByJobIds(Long[] jobIds) {
        return standardJobMapper.deleteStandardJobByJobIds(jobIds);
    }

    /**
     * 删除标准作业任务信息
     *
     * @param jobId 标准作业任务主键
     * @return 结果
     */
    @Override
    public int deleteStandardJobByJobId(Long jobId) {
        return standardJobMapper.deleteStandardJobByJobId(jobId);
    }

    @Transactional
    @Override
    public int aiInsertStandardJob(AIStandardJobDTO aiStandardJobDTO) {
        String data = AIModelApiUtils.aiInsertStandardJobJson(aiStandardJobDTO);
        // 解析JSON数据
        JSONObject jsonObject = JSON.parseObject(data);
        JSONArray jobsArray = jsonObject.getJSONObject("message").getJSONObject("content").getJSONArray("jobs");
        StandardJob standardJob = new StandardJob();
        standardJob.setGermplasmId(aiStandardJobDTO.getGermplasmId());
        // 处理每个工作项
        int count = 0;
        for (int i = 0; i < jobsArray.size(); i++) {
            JSONObject jobObject = jobsArray.getJSONObject(i);
            standardJob.setJobName(jobObject.get("jobName").toString());
            standardJob.setCycleUnit(jobObject.get("cycUnit").toString());
            standardJob.setJobStart(Long.valueOf(jobObject.get("jobStart").toString()));
            standardJob.setJobFinish(Long.valueOf(jobObject.get("jobFinish").toString()));
            standardJob.setCreateBy(SecurityUtils.getUserId().toString());
            standardJob.setCreateTime(DateUtils.getNowDate());
            count += standardJobMapper.insertStandardJob(standardJob);
        }
        return count;
    }
}
