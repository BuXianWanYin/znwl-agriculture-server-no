package com.frog.service.impl;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.agriculture.domain.TraceRecord;
import com.frog.agriculture.mapper.DataStatisticsMapper;
import com.frog.common.utils.SecurityUtils;
import com.frog.mapper.FishDataStatisticsMapper;
import com.frog.service.FishDataStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class FishDataStatisticsServiceImpl implements FishDataStatisticsService {

    @Autowired
    private FishDataStatisticsMapper fishDataStatisticsMapper;

    //工作台
    public List<HashMap> selectBaseInfo(){
        return fishDataStatisticsMapper.selectBaseInfo();
    }

//
//    public List<HashMap> selectDeviceInfo(){
//        return dataStatisticsMapper.selectDeviceInfo();
//    }
//    public List<HashMap> selectDeviceJobInfo(){
//        return dataStatisticsMapper.selectDeviceJobInfo();
//    }
//    public List<HashMap> selectRecordGroupByMonth(){
//        return  dataStatisticsMapper.selectRecordGroupByMonth();
//    }
//    public List<HashMap> selectTaskInfo(){
//        return  dataStatisticsMapper.selectTaskInfo();
//    }
//    public List<HashMap> selectAreaInfo(){
//        return  dataStatisticsMapper.selectAreaInfo();
//    }
//    //大屏-溯源
//    public List<HashMap> selectRecordGroupByCity(){
//        return  dataStatisticsMapper.selectRecordGroupByCity();
//    }
//    public List<HashMap> selectRecordStatistics(){
//        return  dataStatisticsMapper.selectRecordStatistics();
//    }
//    public List<HashMap> selectRecordGroupBySellpro(){
//        return  dataStatisticsMapper.selectRecordGroupBySellpro();
//    }
//    /**
//     * 查询统计
//     * @param traceRecord
//     * @return
//     */
//    @Override
//    public List<HashMap> selectRecord(TraceRecord traceRecord){
//        return dataStatisticsMapper.selectRecord(traceRecord);
//    }
//
//    //溯源报表上边面六个数据
//    @Override
//    public List<HashMap> selectTraceInfo() {
//        return dataStatisticsMapper.selectTraceInfo();
//    }
//
//    //根据batchHead查询今日待完成任务
//    @Override
//    public HashMap selectToadyTaskCountByTaskHead() {
//
//        return dataStatisticsMapper.selectToadyTaskCountByTaskHead(SecurityUtils.isAdmin(SecurityUtils.getUserId())?null:SecurityUtils.getUserId());
//    }
}
