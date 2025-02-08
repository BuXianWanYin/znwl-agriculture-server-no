package com.frog.IaAgriculture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.frog.IaAgriculture.domain.SensorValue;

@Mapper
public interface SensorValueMapper extends BaseMapper<SensorValue> {
    @Select("SELECT * FROM `sensor_value` WHERE pasture_id = #{pastureId} ORDER BY date_time desc limit 1")
    SensorValue getLastDateOne(@Param("pastureId") String pastureId);

}
