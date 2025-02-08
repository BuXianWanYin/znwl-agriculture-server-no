package com.frog.IaAgriculture.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @ Description   :
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/13 15:34
 * @ Version       :  1.0
 */
@Data
public class BlockVo {

    @ApiModelProperty(notes = "hash")
    public String hash;
    @ApiModelProperty(notes = "gas限制")
    public String gasLimit;
    @ApiModelProperty(notes = "区块编号")
    public BigInteger number;
    @ApiModelProperty(notes = "父hash")
    public String parentHash;
    @ApiModelProperty(notes = "时间")
    public LocalDateTime localDateTime;
    @ApiModelProperty(notes = "详细信息")
    public BcosBlock.Block block;
}
