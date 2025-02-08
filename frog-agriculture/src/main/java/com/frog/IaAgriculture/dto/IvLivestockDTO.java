package com.frog.IaAgriculture.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @ Description   :  牲畜查询返回实体类
 * @ Author        :
 * @ CreateDate    :  2024/10/12 19:30
 * @ Version       :  1.0
 */

@ApiModel(value = "分区查询返回实体类")
@Data
public class IvLivestockDTO {
    private String id;
    private String contractAddr;
    private String variety;
    private String partitionName;
    private String plantName;
    private Date plantDate;
    private String name; // pasture name
    private String remark;

}
