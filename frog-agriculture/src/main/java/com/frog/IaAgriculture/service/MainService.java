package com.frog.IaAgriculture.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.fisco.bcos.sdk.client.Client;
import org.springframework.stereotype.Service;
import com.frog.IaAgriculture.dto.MonthlyProcessCountDTO;
import com.frog.IaAgriculture.dto.VarietyCountDTO;
import com.frog.IaAgriculture.mapper.DeviceMapper;
import com.frog.IaAgriculture.mapper.IaPartitionFoodMapper;
import com.frog.IaAgriculture.mapper.IaPartitionMapper;
import com.frog.IaAgriculture.mapper.IaPastureMapper;
import com.frog.IaAgriculture.model.entity.IaPartition;
import com.frog.IaAgriculture.vo.CommonContant;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ Description   :  首页展示统计service
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/13 11:47
 * @ Version       :  1.0
 */
@Service
public class MainService {


    @Resource
    DeviceMapper deviceMapper;

    @Resource
    Client client;

    @Resource
    IaPastureMapper ivPastureMapper;

    @Resource
    IaPartitionMapper ivLivestockMapper;

    @Resource
    IaPartitionFoodMapper ivLivestockSlaughterMapper;

    public ResultVO allQuantityList() {
        Long pastureNum = ivPastureMapper.selectCount(null);
        Long inFenceNum = ivLivestockMapper.selectCount(new LambdaQueryWrapper<IaPartition>().eq(IaPartition::getStatus, CommonContant.LIVESTOCK_STATE_IN_FENCE));
        Long outFenceNum = ivLivestockMapper.selectCount(new LambdaQueryWrapper<IaPartition>().eq(IaPartition::getStatus, CommonContant.LIVESTOCK_STATE_OUT_FENCE));
        Long devNum = deviceMapper.selectCount(null);

        Map<String,Long> map = new HashMap<>();
        map.put("pastureNum",pastureNum);
        map.put("rawNum",inFenceNum);
        map.put("ripeNum",outFenceNum);
        map.put("devNum",devNum);

        return ResultVO.succeed(map);
    }

    public ResultVO varietyStatistics() {
        List<VarietyCountDTO> ivLivestocks = ivLivestockMapper.selectVarietyCount();
        return ResultVO.succeed(ivLivestocks);
    }

    public ResultVO monthlyProcessCount(Integer year) {
        List<MonthlyProcessCountDTO> data = ivLivestockSlaughterMapper.selectMonthlyCountByYear(year);
        List<MonthlyProcessCountDTO> monthlyProcessCountDTOS = fillMissingMonths(data);
        return ResultVO.succeed(monthlyProcessCountDTOS);
    }

    public ResultVO monthlyOutFence(Integer year) {
        List<MonthlyProcessCountDTO> data = ivLivestockMapper.monthlyOutFence(year);
        List<MonthlyProcessCountDTO> monthlyProcessCountDTOS = fillMissingMonths(data);
        return ResultVO.succeed(monthlyProcessCountDTOS);
    }


    private List<MonthlyProcessCountDTO> fillMissingMonths(List<MonthlyProcessCountDTO> originalList) {
        List<Integer> allMonths = generateMonths();
        Map<Integer, MonthlyProcessCountDTO> dtoMap = originalList.stream()
                .collect(Collectors.toMap(MonthlyProcessCountDTO::getMonth, dto -> dto));
        List<MonthlyProcessCountDTO> resultList = new ArrayList<>();
        for (Integer month : allMonths) {
            if (dtoMap.containsKey(month)) {
                resultList.add(dtoMap.get(month));
            } else {
                MonthlyProcessCountDTO emptyDTO = new MonthlyProcessCountDTO();
                emptyDTO.setMonth(month);
                emptyDTO.setCount(0);
                resultList.add(emptyDTO);
            }
        }

        return resultList;
    }

    private List<Integer> generateMonths() {
        List<Integer> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        return months;
    }

}
