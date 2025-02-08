package com.frog.agriculture.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.frog.agriculture.domain.TraceCode;
import com.frog.agriculture.domain.TraceVersion;
import com.frog.agriculture.mapper.TraceCodeMapper;
import com.frog.agriculture.mapper.TraceVersionMapper;
import com.frog.agriculture.model.GenerateCodeParams;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.frog.agriculture.mapper.TraceSellproMapper;
import com.frog.agriculture.domain.TraceSellpro;
import com.frog.agriculture.service.ITraceSellproService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 溯源产品Service业务层处理
 * 
 * @author nealtsiao
 * @date 2023-08-07
 */
@Service
public class TraceSellproServiceImpl implements ITraceSellproService 
{
    @Autowired
    private TraceSellproMapper traceSellproMapper;
    @Autowired
    private TraceVersionMapper traceVersionMapper;
    @Autowired
    private TraceCodeMapper traceCodeMapper;
    /**
     * 查询溯源产品
     * 
     * @param sellproId 溯源产品主键
     * @return 溯源产品
     */
    @Override
    public TraceSellpro selectTraceSellproBySellproId(Long sellproId)
    {
        return traceSellproMapper.selectTraceSellproBySellproId(sellproId);
    }

    /**
     * 查询溯源产品列表
     * 
     * @param traceSellpro 溯源产品
     * @return 溯源产品
     */
    @Override
    public List<TraceSellpro> selectTraceSellproList(TraceSellpro traceSellpro)
    {
        return traceSellproMapper.selectTraceSellproList(traceSellpro);
    }

    /**
     * 新增溯源产品
     * 
     * @param traceSellpro 溯源产品
     * @return 结果
     */
    @Override
    public int insertTraceSellpro(TraceSellpro traceSellpro)
    {
        traceSellpro.setCreateBy(SecurityUtils.getUserId().toString());
        traceSellpro.setCreateTime(DateUtils.getNowDate());
        return traceSellproMapper.insertTraceSellpro(traceSellpro);
    }

    /**
     * 修改溯源产品
     * 
     * @param traceSellpro 溯源产品
     * @return 结果
     */
    @Override
    public int updateTraceSellpro(TraceSellpro traceSellpro)
    {
        traceSellpro.setUpdateBy(SecurityUtils.getUserId().toString());
        traceSellpro.setUpdateTime(DateUtils.getNowDate());
        return traceSellproMapper.updateTraceSellpro(traceSellpro);
    }

    /**
     * 批量删除溯源产品
     * 
     * @param sellproIds 需要删除的溯源产品主键
     * @return 结果
     */
    @Override
    public int deleteTraceSellproBySellproIds(Long[] sellproIds)
    {
        return traceSellproMapper.deleteTraceSellproBySellproIds(sellproIds);
    }

    /**
     * 删除溯源产品信息
     * 
     * @param sellproId 溯源产品主键
     * @return 结果
     */
    @Override
    public int deleteTraceSellproBySellproId(Long sellproId)
    {
        return traceSellproMapper.deleteTraceSellproBySellproId(sellproId);
    }

    /**
     * 生成溯源码
     * @param generateCodePrameter 生成溯源码参数
     * @return 结果
     */
    @Override
    @Transactional
    public synchronized int  generateTraceCode(GenerateCodeParams generateCodePrameter) {

        try {
            if(generateCodePrameter==null
                    || generateCodePrameter.getSellproId()==null
                    ||generateCodePrameter.getUrl().isEmpty()
                    || generateCodePrameter.getNumber()==0)
            {
                return 0;
            }
            if(generateCodePrameter.getNumber()>1000){
                return 0;
            }

            int number = generateCodePrameter.getNumber();
            String url = generateCodePrameter.getUrl();
            String perfix = generateCodePrameter.getPerfix();
            Long sellproId = generateCodePrameter.getSellproId();

            //先确定属源码的格式
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = format.format(new Date());
            if(perfix.isEmpty()){
                perfix = "frog";
            }
            String codePerfix = perfix+dateString;
            //插入version
            TraceVersion traceVersion = new TraceVersion();
            traceVersion.setSellproId(sellproId);
            traceVersion.setCodeRange(codePerfix+"000001"+"-"+codePerfix+String.format("%06d", number));
            traceVersion.setCreateBy(SecurityUtils.getUserId().toString());
            traceVersion.setCreateTime(DateUtils.getNowDate());
            traceVersion.setStatus("0");
             traceVersionMapper.insertTraceVersion(traceVersion);
            Long traceVersionId = traceVersion.getVersionId();
            //循环插入code
            for(int i =1;i<=number;i++){
                String code = codePerfix+String.format("%06d", i);
                TraceCode traceCode = new TraceCode();
                traceCode.setSellproId(sellproId);
                traceCode.setVersionId(traceVersionId);
                traceCode.setTraceCode(code);
                traceCode.setTraceUrl(url+code);
                traceCode.setCreateBy(SecurityUtils.getUserId().toString());
                traceCode.setCreateTime(DateUtils.getNowDate());
                traceCode.setStatus("0");
                traceCodeMapper.insertTraceCode(traceCode);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("生成溯源码发生异常,数据已经回滚，对数据没有影响",e);
        }
        return 1;
    }
}
