package com.frog.agriculture.service;

import java.util.List;
import com.frog.agriculture.domain.TraceCode;
//import org.aspectj.weaver.tools.Trace;

import javax.servlet.http.HttpServletResponse;

/**
 * 溯源码Service接口
 * 
 * @author nealtsiao
 * @date 2023-08-04
 */
public interface ITraceCodeService 
{
    /**
     * 查询溯源码
     * 
     * @param codeId 溯源码主键
     * @return 溯源码
     */
    public TraceCode selectTraceCodeByCodeId(Long codeId);

    /**
     * 查询溯源码列表
     * 
     * @param traceCode 溯源码
     * @return 溯源码集合
     */
    public List<TraceCode> selectTraceCodeList(TraceCode traceCode);

    /**
     * 查询异常溯源码类标
     *
     * @param traceCode 溯源码
     * @return 溯源码集合
     */
    public List<TraceCode> selectAlertTraceCodeList(TraceCode traceCode);

    /**
     * 新增溯源码
     * 
     * @param traceCode 溯源码
     * @return 结果
     */
    public int insertTraceCode(TraceCode traceCode);

    /**
     * 批量生产溯源码
     * @param versionId
     * @param CodeNum
     * @return
     */
    public void batchInsertTraceCode(Long versionId,Long CodeNum);

    /**
     * 修改溯源码
     * 
     * @param traceCode 溯源码
     * @return 结果
     */
    public int updateTraceCode(TraceCode traceCode);

    /**
     * 批量删除溯源码
     * 
     * @param codeIds 需要删除的溯源码主键集合
     * @return 结果
     */
    public int deleteTraceCodeByCodeIds(Long[] codeIds);

    /**
     * 删除溯源码信息
     * 
     * @param codeId 溯源码主键
     * @return 结果
     */
    public int deleteTraceCodeByCodeId(Long codeId);

    /**
     * 打包溯源码图片文件压缩ZIP，输出到HttpServletResponse
     * @param httpServletResponse
     * @param traceCode
     */
    public void downloadTraceCodeImagesZip(HttpServletResponse httpServletResponse, TraceCode traceCode);

    /**
     * 更具traceCode查询溯源码
     * @param traceCode
     * @return
     */
    public TraceCode selectTraceCodeByTraceCode(String traceCode);

}
