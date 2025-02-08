package com.frog.agriculture.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置种植单元参数
 * @author neal
 * @date 2024/7/28  18:23
 */
@Data
public class UnitParams {

    public UnitParams(){
        this.batchIds = new ArrayList<>();
        this.deviceIds = new ArrayList<>();
        this.cameraIds = new ArrayList<>();
        this.sceneIds = new ArrayList<>();
    }
    private Long landId;
    private List<Long> batchIds;
    private List<Long> deviceIds;
    private List<Long> cameraIds;
    private List<Long> sceneIds;
}
