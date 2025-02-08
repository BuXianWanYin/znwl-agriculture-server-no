package com.frog.IaAgriculture.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ApiModel(value = "溯源信息")
@Data
public class TraceabilityDTO implements Serializable {

    @ApiModelProperty(value = "大棚信息")
    private Object ivPastureInfo;

    @ApiModelProperty(value = "分区的信息")
    private Object iaPartitionInfo;

    @ApiModelProperty(value = "商品信息")
    private Object iaPartitionFoodSensorValueInfo;

    @ApiModelProperty(value = "施肥信息")
    private Object iaPartitionFeedingInfo;

    @ApiModelProperty(value = "环境信息")
    private List<Map<String, Object>> map;

    @ApiModelProperty(value = "大棚合约地址")
    private Object contractAddr;
}
