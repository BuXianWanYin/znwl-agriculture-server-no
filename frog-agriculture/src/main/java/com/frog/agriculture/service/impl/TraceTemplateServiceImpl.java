package com.frog.agriculture.service.impl;

import java.util.List;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.TraceTemplateMapper;
import com.frog.agriculture.domain.TraceTemplate;
import com.frog.agriculture.service.ITraceTemplateService;

/**
 * 溯源模版Service业务层处理
 *
 * @author nealtsiao
 * @date 2024-04-14
 */
@Service
public class TraceTemplateServiceImpl implements ITraceTemplateService
{
    @Autowired
    private TraceTemplateMapper traceTemplateMapper;

    /**
     * 查询溯源模版
     *
     * @param templateId 溯源模版主键
     * @return 溯源模版
     */
    @Override
    public TraceTemplate selectTraceTemplateByTemplateId(Long templateId)
    {
        return traceTemplateMapper.selectTraceTemplateByTemplateId(templateId);
    }

    /**
     * 查询溯源模版列表
     *
     * @param traceTemplate 溯源模版
     * @return 溯源模版
     */
    @Override
    public List<TraceTemplate> selectTraceTemplateList(TraceTemplate traceTemplate)
    {
        return traceTemplateMapper.selectTraceTemplateList(traceTemplate);
    }

    /**
     * 新增溯源模版
     *
     * @param traceTemplate 溯源模版
     * @return 结果
     */
    @Override
    public int insertTraceTemplate(TraceTemplate traceTemplate)
    {
        traceTemplate.setCreateBy(SecurityUtils.getUserId().toString());
        traceTemplate.setCreateTime(DateUtils.getNowDate());
        return traceTemplateMapper.insertTraceTemplate(traceTemplate);
    }

    /**
     * 修改溯源模版
     *
     * @param traceTemplate 溯源模版
     * @return 结果
     */
    @Override
    public int updateTraceTemplate(TraceTemplate traceTemplate)
    {
        traceTemplate.setUpdateBy(SecurityUtils.getUserId().toString());
        traceTemplate.setUpdateTime(DateUtils.getNowDate());
        return traceTemplateMapper.updateTraceTemplate(traceTemplate);
    }

    /**
     * 批量删除溯源模版
     *
     * @param templateIds 需要删除的溯源模版主键
     * @return 结果
     */
    @Override
    public int deleteTraceTemplateByTemplateIds(Long[] templateIds)
    {
        return traceTemplateMapper.deleteTraceTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除溯源模版信息
     *
     * @param templateId 溯源模版主键
     * @return 结果
     */
    @Override
    public int deleteTraceTemplateByTemplateId(Long templateId)
    {
        return traceTemplateMapper.deleteTraceTemplateByTemplateId(templateId);
    }

    /**
     * 根据traceCode查询溯源页面模版数据
     * @param traceCode
     * @return
     */
    @Override
    public TraceTemplate getTraceTemplateByTraceCode(String traceCode) {
        return traceTemplateMapper.getTraceTemplateByTraceCode(traceCode);
    }
}