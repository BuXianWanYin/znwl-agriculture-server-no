package com.frog.service;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.agriculture.domain.TraceRecord;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
@Service
public interface FishDataStatisticsService {
        public List<HashMap> selectBaseInfo();
//        public List<HashMap> selectDeviceInfo();
//        public List<HashMap> selectDeviceJobInfo();
//        public List<HashMap> selectRecordGroupByMonth();
        public List<HashMap> selectTaskInfo();
//        public List<HashMap> selectAreaInfo();
//        public List<HashMap> selectRecordGroupByCity();
//        public List<HashMap> selectRecordStatistics();
//        public List<HashMap> selectRecordGroupBySellpro();
//        public List<HashMap> selectRecord(TraceRecord traceRecord);
//        //溯源报表功能上面的六个数据
//        public List<HashMap> selectTraceInfo();
//        public HashMap selectToadyTaskCountByTaskHead();

    }

