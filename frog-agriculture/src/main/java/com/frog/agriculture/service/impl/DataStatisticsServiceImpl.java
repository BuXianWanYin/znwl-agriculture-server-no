package com.frog.agriculture.service.impl;

import com.frog.agriculture.domain.*;
import com.frog.agriculture.mapper.*;
import com.frog.agriculture.service.IDataStatisticsService;
import com.frog.common.core.domain.entity.SysUser;
import com.frog.common.utils.SecurityUtils;
import com.frog.agriculture.iotDomain.Device;
import com.frog.agriculture.iotDomain.Product;
import com.frog.agriculture.iotDomain.ThingsModel;
import com.frog.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.frog.iot.mapper.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Service
public class DataStatisticsServiceImpl implements IDataStatisticsService {

    @Autowired
    private DataStatisticsMapper dataStatisticsMapper;
    @Autowired
    private BaseinfoMapper baseinfoMapper;
    @Autowired
    private TraceRecordMapper traceRecordMapper;
    @Autowired
    private TraceVersionMapper traceVersionMapper;
    @Autowired
    private TraceCodeMapper traceCodeMapper;
    @Autowired
    private TraceSellproMapper traceSellproMapper;
    @Autowired
    private CropBatchMapper cropBatchMapper;
    @Autowired
    private BatchTaskMapper batchTaskMapper;
    @Autowired
    private TraceStaffMapper traceStaffMapper;
    @Autowired
    private TraceShopMapper traceShopMapper;

    private EmployeeMapper employeeMapper;
    //工作台
    public List<HashMap> selectBaseInfo(){
        return dataStatisticsMapper.selectBaseInfo();
    }
    public List<HashMap> selectDeviceInfo(){
        return dataStatisticsMapper.selectDeviceInfo();
    }
    public List<HashMap> selectDeviceJobInfo(){
        return dataStatisticsMapper.selectDeviceJobInfo();
    }
    public List<HashMap> selectRecordGroupByMonth(){
        return  dataStatisticsMapper.selectRecordGroupByMonth();
    }
    public List<HashMap> selectTaskInfo(){
        return  dataStatisticsMapper.selectTaskInfo();
    }
    public List<HashMap> selectAreaInfo(){
        return  dataStatisticsMapper.selectAreaInfo();
    }
    //大屏-溯源
    public List<HashMap> selectRecordGroupByCity(){
        return  dataStatisticsMapper.selectRecordGroupByCity();
    }
    public List<HashMap> selectRecordStatistics(){
        return  dataStatisticsMapper.selectRecordStatistics();
    }
    public List<HashMap> selectRecordGroupBySellpro(){
        return  dataStatisticsMapper.selectRecordGroupBySellpro();
    }
    /**
     * 查询统计
     * @param traceRecord
     * @return
     */
    @Override
    public List<HashMap> selectRecord(TraceRecord traceRecord){
        return dataStatisticsMapper.selectRecord(traceRecord);
    }

    //溯源报表上边面六个数据
    @Override
    public List<HashMap> selectTraceInfo() {
        return dataStatisticsMapper.selectTraceInfo();
    }

    //根据batchHead查询今日待完成任务
    @Override
    public HashMap selectToadyTaskCountByTaskHead() {

        return dataStatisticsMapper.selectToadyTaskCountByTaskHead(SecurityUtils.isAdmin(SecurityUtils.getUserId())?null:SecurityUtils.getUserId());
    }
}
