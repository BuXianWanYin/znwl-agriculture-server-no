package com.frog.agriculture.mapper;

import com.frog.agriculture.domain.TraceRecord;

import java.util.HashMap;
import java.util.List;

public interface DataStatisticsMapper {

    public List<HashMap> selectBaseInfo();
    public List<HashMap> selectDeviceInfo();
    public List<HashMap> selectDeviceJobInfo();
    public List<HashMap> selectRecordGroupByMonth();
    public List<HashMap> selectTaskInfo();
    public List<HashMap> selectAreaInfo();
    public List<HashMap> selectRecordGroupByCity();
    public List<HashMap> selectRecordStatistics();
    public List<HashMap> selectRecordGroupBySellpro();
    //查询溯源的记录列表
    public List<HashMap> selectRecord(TraceRecord traceRecord);
    //查询溯源报表功能上边的六
    public List<HashMap> selectTraceInfo();
    //根据batchHead查询今日待完成任务
    public HashMap selectToadyTaskCountByTaskHead(Long batchHead);

}
