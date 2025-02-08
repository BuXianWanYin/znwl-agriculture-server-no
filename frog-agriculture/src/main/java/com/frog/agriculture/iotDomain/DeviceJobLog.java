package com.frog.agriculture.iotDomain;

import com.frog.common.annotation.Excel;
import com.frog.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 设备定时任务日志对象 iot_device_job_log
 * 
 * @author nealtsiao
 * @date 2023-09-14
 */
@Data
public class DeviceJobLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务日志ID */
    private Long jobLogId;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String jobName;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long jobId;

    /** 任务组名 */
    @Excel(name = "任务组名")
    private String jobGroup;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 日志信息 */
    @Excel(name = "日志信息")
    private String jobMessage;

    /** 执行状态（0正常 1失败） */
    @Excel(name = "执行状态", readConverterExp = "0=正常,1=失败")
    private String status;

    /** 异常信息 */
    @Excel(name = "异常信息")
    private String exceptionInfo;

    @Excel(name="设备ID")
    private Long deviceId;

    @Excel(name="定时任务类型")
    private Integer jobType;
}
