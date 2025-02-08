package com.frog.IaAgriculture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.frog.IaAgriculture.dto.MonthlyProcessCountDTO;
import com.frog.IaAgriculture.model.IaPartitionFood;

import java.util.List;
import java.util.Map;

/**
 * 分区食品
 */
@Mapper
public interface IaPartitionFoodMapper extends BaseMapper<IaPartitionFood> {

    @Select({"SELECT MONTH(date) AS month, COUNT(*) AS count", "FROM ia_partition_food", "WHERE YEAR(date) = #{year}", "GROUP BY MONTH(date)", "ORDER BY month ASC"})
    List<MonthlyProcessCountDTO> selectMonthlyCountByYear(@Param("year") int year);

    @Select("SELECT " + "DATE(date_time) AS day, " + "CONCAT(ROUND(AVG(CAST(temperature AS DECIMAL(10, 2))), 2), ' °C') AS avg_temperature, " + "CONCAT(ROUND(AVG(CAST(humidity AS DECIMAL(10, 2))), 2), ' %') AS avg_humidity, " + "CONCAT(ROUND(AVG(CAST(airquality AS DECIMAL(10, 2))), 2), ' AQI') AS avg_airquality, " + "CONCAT(ROUND(AVG(CAST(pressure AS DECIMAL(10, 2))), 2), ' hPa') AS avg_pressure, " + "CONCAT(ROUND(AVG(CAST(p AS DECIMAL(10, 2))), 2), ' hPa') AS avg_p, " + "CONCAT(ROUND(AVG(CAST(lux AS DECIMAL(10, 2))), 2), ' lx') AS avg_lux, " + "CONCAT(ROUND(AVG(CAST(soil AS DECIMAL(10, 2))), 2), ' °C') AS avg_soil_temperature, " + "CONCAT(ROUND(AVG(CAST(air AS DECIMAL(10, 2))), 2), ' AQI') AS avg_air, " + "CONCAT(ROUND(AVG(CAST(relay AS DECIMAL(10, 2))), 2), ' 状态') AS avg_relay " + "FROM sensor_value " + "WHERE pasture_id = #{pastureId} " + "GROUP BY DATE(date_time)")
    List<Map<String, Object>> calculateDailyAverages(String pastureId);

}
