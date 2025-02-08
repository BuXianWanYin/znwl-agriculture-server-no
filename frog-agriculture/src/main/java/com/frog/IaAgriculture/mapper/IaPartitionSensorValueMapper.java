package com.frog.IaAgriculture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.frog.IaAgriculture.model.IaPartitionSensorValue;

import java.util.List;

@Mapper
public interface IaPartitionSensorValueMapper extends BaseMapper<IaPartitionSensorValue> {

    @Select("SELECT `pasture_id`, AVG(`temperature`) temperature,  AVG(`humidity`) AS humidity, AVG(`airquality`) AS airquality, AVG(`pressure`) AS pressure" +
            " FROM  `sensor_value` " +
            " WHERE  DATE(`date_time`) =  DATE(#{formattedDate}) GROUP BY  `pasture_id`")
    List<IaPartitionSensorValue> get(@Param("formattedDate") String formattedDate);

}
