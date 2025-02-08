package com.frog.IaAgriculture.dto;

import lombok.Data;

import java.util.Date;

/**
 * @ Description   :
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/13 21:06
 * @ Version       :  1.0
 */
@Data
public class IvLivestockOutDTO {
    private String id;
    private String variety;
    private String plantName;
    private Date plantDate;
    private String name;
    private Integer processState;// pasture name
    private Integer number;
    private Date processDate;
    private String remark;
}
