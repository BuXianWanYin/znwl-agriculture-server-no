package com.frog.IaAgriculture.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationPageVo {

    private Long id;

    private Long pensionId;

    private String pensionName;

    private String pensionAddress;

    private Long fromSecurityBureauId;

    private String fromSecurityBureauName;

    private String fromSecurityBureauAddress;

    private Long toSecurityBureauId;

    private String toSecurityBureauName;

    private String toSecurityBureauAddress;

    private String idNumber;

    private String rejectionMsg;

    private String reasonForSubmission;

    private LocalDateTime createTime;

    private Integer mark;

    private Integer status;

}
