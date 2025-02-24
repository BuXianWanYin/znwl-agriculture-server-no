package com.frog.IaAgriculture.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 设备实体
 */
@ApiModel(value = "设备")
@Data
@TableName(value = "device")  // 指定数据库表名
public class Device {

    /**
     * id
     */
    @ApiModelProperty(value = "id", example = "123")
    @TableField(value = "id")  // 指定数据库列名
    private String id;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", example = "123", required = true)
    @TableField(value = "device_id")  // 指定数据库列名
    private String deviceId;

    /**
     * 大棚id，空为没有绑定牧场
     */
    @ApiModelProperty(value = "大棚id", example = "123")
    @TableField(value = "pasture_id")  // 指定数据库列名
    private String pastureId;

    /**
     * 分区id，空为没有绑定牧场
     */
    @ApiModelProperty(value = "分区id", example = "123")
    @TableField(value = "batch_id")  // 指定数据库列名
    private String batchId;

    @ApiModelProperty(value = "区块地址", example = "123")
    @TableField(value = "address")  // 指定数据库列名
    private String address;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称", example = "温度计", required = true)
    @TableField(value = "device_name")  // 指定数据库列名
    private String deviceName;

    /**
     * 设备状态，1/在线，0/不在线
     */
    @ApiModelProperty(value = "设备状态", example = "1/在线，0/不在线")
    @TableField(value = "status")  // 指定数据库列名
    private String status;

    /**
     * 传感器序号  通过序号调用SensorCommandMappingUtil的方法 查询对应指令
     */
    @ApiModelProperty(value = "传感器序号", example = "1" + "传感器对应序号\n" +
                                                            "1 风向传感器\n" +
                                                            "2 百叶箱\n" +
                                                            "3 风速传感器\n" +
                                                            "4 土壤温度水分变送器\n" +
                                                            "5 土壤 pH 传感器\n" +
                                                            "6 土壤水分电导率传感器\n" +
                                                            "8 水质传感器")
    @TableField(value = "sensorType")  // 指定数据库列名
    private String sensorType;

    /**
     * 传感器指令
     */
    @ApiModelProperty(value = "传感器指令", example = "06 03 00 00 00 04 45 BE")
    @TableField(value = "sensor_command")  // 指定数据库列名
    private String sensorCommand;

    /**
     * 添加日期
     */
    @ApiModelProperty(value = "添加日期", example = "2024-10-11 21:54:32")
    @TableField(value = "date")  // 指定数据库列名
    private Date date;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "2024-10-11 21:54:32", hidden = true)
    @TableField(value = "update_time")  // 指定数据库列名
    private Date updateTime;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "这是测量温度的设备")
    @TableField(value = "remark")  // 指定数据库列名
    private String remark;
}
