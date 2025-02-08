package com.frog.IaAgriculture.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ Description   :
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/13 15:33
 * @ Version       :  1.0
 */
@Data
public class BlockInfoVo {

    @ApiModelProperty(notes = "总节点数量")
    private Integer peersSize;

    @ApiModelProperty(notes = "观察")
    private Integer observerSize;

    @ApiModelProperty(notes = "共识")
    private Integer sealerSize;
    @ApiModelProperty(notes = "游离")
    private Integer freeSize;

    @ApiModelProperty(notes = "区块列表")
    private List<BlockVo> blocks;
    @ApiModelProperty(notes = "交易列表")
    private List<Map> transactions;


}
