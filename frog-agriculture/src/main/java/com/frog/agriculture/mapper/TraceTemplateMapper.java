package com.frog.agriculture.mapper;

import java.util.List;
import com.frog.agriculture.domain.TraceTemplate;

/**
 * 溯源模版Mapper接口
 *
 * @author nealtsiao
 * @date 2024-04-14
 */
public interface TraceTemplateMapper
{
    /**
     * 查询溯源模版
     *
     * @param templateId 溯源模版主键
     * @return 溯源模版
     */
    public TraceTemplate selectTraceTemplateByTemplateId(Long templateId);

    /**
     * 查询溯源模版列表
     *
     * @param traceTemplate 溯源模版
     * @return 溯源模版集合
     */
    public List<TraceTemplate> selectTraceTemplateList(TraceTemplate traceTemplate);

    /**
     * 新增溯源模版
     *
     * @param traceTemplate 溯源模版
     * @return 结果
     */
    public int insertTraceTemplate(TraceTemplate traceTemplate);

    /**
     * 修改溯源模版
     *
     * @param traceTemplate 溯源模版
     * @return 结果
     */
    public int updateTraceTemplate(TraceTemplate traceTemplate);

    /**
     * 删除溯源模版
     *
     * @param templateId 溯源模版主键
     * @return 结果
     */
    public int deleteTraceTemplateByTemplateId(Long templateId);

    /**
     * 批量删除溯源模版
     *
     * @param templateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTraceTemplateByTemplateIds(Long[] templateIds);

    /**
     * 根据溯源码查询溯源页面模版数据
     * @param traceCode 溯源码
     * @return
     */
    public TraceTemplate getTraceTemplateByTraceCode(String traceCode);
}