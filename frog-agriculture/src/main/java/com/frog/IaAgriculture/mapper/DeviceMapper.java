package com.frog.IaAgriculture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.frog.IaAgriculture.domain.Device;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 设备mapper
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    @Update(" UPDATE device SET device_id= #{device.deviceId}, pasture_id = #{device.pastureId}, device_name = #{device.deviceName}, status = #{device.status}, date = #{device.date}, update_time = #{device.updateTime}, remark = #{device.remark} WHERE id = #{device.id}")
    int updateDevice(@Param("device") Device device);

    // 根据设备序号查询设备绑定信息
    @Select("SELECT pasture_id, batch_id FROM device WHERE sensorType = #{sensorType}")
    Device selectSensorById(String sensorType);

    // 根据设备序号查询水质设备绑定信息
    @Select("SELECT fish_pasture_id, fish_pasture_batch_id FROM device WHERE sensorType = #{sensorType}")
    Device selectWaterById(String sensorId);
//    @Update("UPDATE device SET device_name = #{device.deviceName} WHERE id = #{device.id}")
//    int updateDevice(@Param("device") Device device);

}
