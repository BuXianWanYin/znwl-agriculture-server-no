package com.frog.controller;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.agriculture.domain.TraceRecord;
import com.frog.agriculture.service.IDataStatisticsService;
import com.frog.agriculture.service.ITraceRecordService;
import com.frog.common.core.controller.BaseController;
import com.frog.common.core.domain.AjaxResult;
import com.frog.common.core.page.TableDataInfo;
import com.frog.service.FishDataStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/fishPasture/statistics")
public class FishDataStatisticsController extends BaseController {
    @Autowired // 自动装配数据统计服务接口实现类对象
    private FishDataStatisticsService fishDataStatisticsService; // 定义数据统计服务接口
    @Autowired // 自动装配追溯记录服务接口实现类对象
    private ITraceRecordService traceRecordService; // 定义追溯记录服务接口

    @GetMapping("/selectFishBaseInfo")
    public TableDataInfo selectBaseInfo(){ // 获取基础信息的方法，返回表格数据对象
        List<HashMap> list = fishDataStatisticsService.selectBaseInfo(); // 调用数据统计服务获取基础信息列表
        return getDataTable(list); // 调用父类方法包装返回数据并返回
    }

//    @GetMapping("/selectDeviceInfo")
//    public TableDataInfo selectDeviceInfo(){ // 获取设备信息的方法，返回表格数据对象
//        List<HashMap> list = dataStatisticsService.selectDeviceInfo(); // 调用数据统计服务获取设备信息列表
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
//    @GetMapping("/selectDeviceJobInfo")
//    public TableDataInfo selectDeviceJobInfo(){ // 获取设备任务信息的方法，返回表格数据对象
//        List<HashMap> list = dataStatisticsService.selectDeviceJobInfo(); // 调用数据统计服务获取设备任务信息列表
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
//    @GetMapping("/selectRecordGroupByMonth")
//    public TableDataInfo selectRecordGroupByMonth(){ // 按月分组获取记录信息的方法，返回表格数据对象
//        List<HashMap> list = dataStatisticsService.selectRecordGroupByMonth(); // 调用数据统计服务获取按月分组的记录信息列表
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
    @GetMapping("/selectFishTaskInfo")
    public TableDataInfo selectTaskInfo(){ // 获取任务信息的方法，返回表格数据对象
        List<HashMap> list = fishDataStatisticsService.selectTaskInfo(); // 调用数据统计服务获取任务信息列表
        return getDataTable(list); // 包装分页数据并返回
    }
//
//    @GetMapping("/selectAreaInfo")
//    public TableDataInfo selectAreaInfo(){ // 定义获取区域信息的方法，返回表格数据对象
//        List<HashMap> list = dataStatisticsService.selectAreaInfo(); // 调用数据统计服务获取区域信息列表
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
//    @GetMapping("/selectRecordGroupByCity")
//    public TableDataInfo selectRecordGroupByCity(){ // 按城市分组获取记录信息的方法，返回表格数据对象
//        List<HashMap> list = dataStatisticsService.selectRecordGroupByCity(); // 调用数据统计服务获取按城市分组的记录信息列表
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
//    @GetMapping("/selectRecordStatistics")
//    public TableDataInfo selectRecordStatistics(){ // 获取记录统计信息的方法，返回表格数据对象
//        List<HashMap> list = dataStatisticsService.selectRecordStatistics(); // 调用数据统计服务获取记录统计信息列表
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
//    @GetMapping("/selectRecordGroupBySellpro")
//    public TableDataInfo selectRecordGroupBySellpro(){ // 按销售产品分组获取记录信息的方法，返回表格数据对象
//        List<HashMap> list = dataStatisticsService.selectRecordGroupBySellpro(); // 调用数据统计服务获取按销售产品分组的记录信息列表
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
//    @GetMapping("/selectRecord")
//    public TableDataInfo selectRecord(TraceRecord traceRecord) { // 获取记录信息的方法，参数为追溯记录实体，返回表格数据对象
//        startPage(); // 初始化分页参数
//        List<HashMap> list = dataStatisticsService.selectRecord(traceRecord); // 调用数据统计服务获取记录信息列表，使用传入的追溯记录对象作为查询条件
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
//    @GetMapping("/selectTraceInfo")
//    public TableDataInfo selectTraceInfo(){ // 获取追溯信息的方法，返回表格数据对象
//        List<HashMap> list = dataStatisticsService.selectTraceInfo(); // 调用数据统计服务获取追溯信息列表
//        return getDataTable(list); // 包装分页数据并返回
//    }
//
//    @GetMapping("/selectToadyTaskCountByTaskHead")
//    public AjaxResult selectToadyTaskCountByTaskHead(){ // 获取今日任务统计信息的方法，返回Ajax结果对象
//        HashMap data = dataStatisticsService.selectToadyTaskCountByTaskHead(); // 调用数据统计服务获取今日任务统计数据
//        return AjaxResult.success(data); // 使用成功状态返回Ajax结果
//    }
}
