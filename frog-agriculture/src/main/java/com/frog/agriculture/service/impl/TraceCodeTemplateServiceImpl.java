package com.frog.agriculture.service.impl;

import java.util.List;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.TraceCodeTemplateMapper;
import com.frog.agriculture.domain.TraceCodeTemplate;
import com.frog.agriculture.service.ITraceCodeTemplateService;

/**
 * 溯源码模版Service业务层处理
 * 
 * @author nealtsiao
 * @date 2024-04-02
 */
@Service
public class TraceCodeTemplateServiceImpl implements ITraceCodeTemplateService 
{
    @Autowired
    private TraceCodeTemplateMapper traceCodeTemplateMapper;

    /**
     * 查询溯源码模版
     * 
     * @param templateId 溯源码模版主键
     * @return 溯源码模版
     */
    @Override
    public TraceCodeTemplate selectTraceCodeTemplateByTemplateId(Long templateId)
    {
        return traceCodeTemplateMapper.selectTraceCodeTemplateByTemplateId(templateId);
    }

    /**
     * 查询溯源码模版列表
     * 
     * @param traceCodeTemplate 溯源码模版
     * @return 溯源码模版
     */
    @Override
    public List<TraceCodeTemplate> selectTraceCodeTemplateList(TraceCodeTemplate traceCodeTemplate)
    {
        return traceCodeTemplateMapper.selectTraceCodeTemplateList(traceCodeTemplate);
    }

    /**
     * 新增溯源码模版
     * 
     * @param traceCodeTemplate 溯源码模版
     * @return 结果
     */
    @Override
    public int insertTraceCodeTemplate(TraceCodeTemplate traceCodeTemplate)
    {
        traceCodeTemplate.setCreateBy(SecurityUtils.getUserId().toString());
        traceCodeTemplate.setCreateTime(DateUtils.getNowDate());
        return traceCodeTemplateMapper.insertTraceCodeTemplate(traceCodeTemplate);
    }

    /**
     * 修改溯源码模版
     * 
     * @param traceCodeTemplate 溯源码模版
     * @return 结果
     */
    @Override
    public int updateTraceCodeTemplate(TraceCodeTemplate traceCodeTemplate)
    {
        traceCodeTemplate.setUpdateBy(SecurityUtils.getUserId().toString());
        traceCodeTemplate.setUpdateTime(DateUtils.getNowDate());
        return traceCodeTemplateMapper.updateTraceCodeTemplate(traceCodeTemplate);
    }

    /**
     * 批量删除溯源码模版
     * 
     * @param templateIds 需要删除的溯源码模版主键
     * @return 结果
     */
    @Override
    public int deleteTraceCodeTemplateByTemplateIds(Long[] templateIds)
    {
        return traceCodeTemplateMapper.deleteTraceCodeTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除溯源码模版信息
     * 
     * @param templateId 溯源码模版主键
     * @return 结果
     */
    @Override
    public int deleteTraceCodeTemplateByTemplateId(Long templateId)
    {
        return traceCodeTemplateMapper.deleteTraceCodeTemplateByTemplateId(templateId);
    }
}
