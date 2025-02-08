package com.frog.agriculture.mapper;

import java.util.List;
import com.frog.agriculture.domain.TraceCodeTemplate;

/**
 * 溯源码模版Mapper接口
 * 
 * @author nealtsiao
 * @date 2024-04-02
 */
public interface TraceCodeTemplateMapper 
{
    /**
     * 查询溯源码模版
     * 
     * @param templateId 溯源码模版主键
     * @return 溯源码模版
     */
    public TraceCodeTemplate selectTraceCodeTemplateByTemplateId(Long templateId);

    /**
     * 查询溯源码模版列表
     * 
     * @param traceCodeTemplate 溯源码模版
     * @return 溯源码模版集合
     */
    public List<TraceCodeTemplate> selectTraceCodeTemplateList(TraceCodeTemplate traceCodeTemplate);

    /**
     * 新增溯源码模版
     * 
     * @param traceCodeTemplate 溯源码模版
     * @return 结果
     */
    public int insertTraceCodeTemplate(TraceCodeTemplate traceCodeTemplate);

    /**
     * 修改溯源码模版
     * 
     * @param traceCodeTemplate 溯源码模版
     * @return 结果
     */
    public int updateTraceCodeTemplate(TraceCodeTemplate traceCodeTemplate);

    /**
     * 删除溯源码模版
     * 
     * @param templateId 溯源码模版主键
     * @return 结果
     */
    public int deleteTraceCodeTemplateByTemplateId(Long templateId);

    /**
     * 批量删除溯源码模版
     * 
     * @param templateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTraceCodeTemplateByTemplateIds(Long[] templateIds);
}
