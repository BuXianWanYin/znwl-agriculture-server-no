package com.frog.IaAgriculture.mqtt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.frog.IaAgriculture.domain.Device;
import com.frog.IaAgriculture.mapper.DeviceMapper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 动态检测设备在线离线状态
 */
@Component
public class DeviceStatusJob {


    @Resource
    DeviceMapper deviceMapper;

    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void init() {
        //每十秒执行一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {

                //每次执行十条
                long size = 10L;
                Long count = deviceMapper.selectCount(null);
                for (long i = 0L; i < count; i += size) {

                    LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.last("LIMIT " + i + ", " + size);
                    List<Device> devices = deviceMapper.selectList(queryWrapper);

                    //判断集合是否为空
                    if (CollUtil.isNotEmpty(devices)) {
                        devices.forEach(e -> {
                            Date date = new Date();

                            long timeDifferenceInMillis = date.getTime() - e.getUpdateTime().getTime();

                            // 转换为秒
                            long timeDifferenceInSeconds = timeDifferenceInMillis / 1000;

                            // 判断时间差是否大于等于 5 秒、修改设备在线状态
                            if (Math.abs(timeDifferenceInSeconds) >= 7) {
                                e.setStatus("0");
                            } else {
                                e.setStatus("1");
                            }
                            deviceMapper.updateById(e);
                        });
                    }
                }

                Console.log("检测设备状态成功");
            }
        });

        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();

    }

}
