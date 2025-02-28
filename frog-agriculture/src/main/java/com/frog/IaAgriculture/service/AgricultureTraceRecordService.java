package com.frog.IaAgriculture.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frog.IaAgriculture.domain.AgricultureTraceRecord;
import com.frog.IaAgriculture.mapper.AgricultureTraceRecordMapper;
import com.frog.IaAgriculture.model.entity.IaPartition;
import com.frog.IaAgriculture.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AgricultureTraceRecordService extends ServiceImpl<AgricultureTraceRecordMapper, AgricultureTraceRecord> {

    @Autowired
    private AgricultureTraceRecordMapper agricultureTraceRecordMapper;

    public void addAgricultureTraceRecord(AgricultureTraceRecord agricultureTraceRecord) {
        agricultureTraceRecord.setQueryDate(new Date());
        agricultureTraceRecordMapper.insert(agricultureTraceRecord);
    }

    public ResultVO getAgricultureTraceRecord() {
        return ResultVO.succeed(agricultureTraceRecordMapper.selectList(new LambdaQueryWrapper<AgricultureTraceRecord>()));
    }
}
