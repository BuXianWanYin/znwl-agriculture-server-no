package com.frog.IaAgriculture.controller;


import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock.TransactionResult;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.ObserverList;
import org.fisco.bcos.sdk.client.protocol.response.Peers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.frog.IaAgriculture.vo.BlockInfoVo;
import com.frog.IaAgriculture.vo.BlockVo;
import com.frog.IaAgriculture.vo.ResultVO;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/block")
@Api(produces = "链管理",tags = "区块链管理")
public class BlockController {

    @Resource
    private Client client;

    @Value("${system.node}")
    private Integer node;

    @GetMapping("/blockchainInfo")
    @ApiOperation(value = "区块信息")
    public ResultVO<BlockInfoVo> listBlock(){

        BlockInfoVo blockInfoVo = new BlockInfoVo();

        Peers peers = client.getPeers();
        int peersSize = peers.getPeers().size()+1;
        blockInfoVo.setPeersSize(peersSize);


        //观察节点
        ObserverList observerList = client.getObserverList();
        int observerSize = observerList.getObserverList().size();
        blockInfoVo.setObserverSize(observerSize);

        //共识节点
        int sealerSize =peersSize-observerSize;
        blockInfoVo.setSealerSize(sealerSize);


        //游离节点
        int freeSize=node-peersSize;
        blockInfoVo.setFreeSize(freeSize);

        BlockNumber blockNumber = client.getBlockNumber();
        Long blockNumbers =Long.parseLong( blockNumber.getBlockNumber().toString());


        Long forSize= 10L;
        if (blockNumbers-forSize<=0L) {
            forSize=blockNumbers;
        }

        Long limit=blockNumbers-100;
        List<Map> mapArrayList=new ArrayList<>();
        for (Long i = blockNumbers; i <=blockNumbers ; i--) {

            if (i<0) {
                break;
            }
            BcosBlock blockByNumber = client.getBlockByNumber(BigInteger.valueOf(i), true);
            List<TransactionResult> transactions = blockByNumber.getBlock().getTransactions();
            for (TransactionResult tr : transactions){

                //替换里面格式为json格式
                String  transaction= tr.toString().replaceAll("=",":");
                Map map = JSON.parseObject(transaction, Map.class);
                mapArrayList.add(map);
                if (mapArrayList.size()==10 ) {
                    break;
                }
            }
            if (mapArrayList.size()==10 ) {
                break;
            }

            if (limit==i){
                break;
            }

        }
        blockInfoVo.setTransactions(mapArrayList);

        List<BlockVo> list=new ArrayList<>();
        for (Long i = 0L; i <forSize ; i++) {
            BcosBlock blockByNumber = client.getBlockByNumber(BigInteger.valueOf(blockNumbers), true);
            BcosBlock.Block block = blockByNumber.getBlock();
            BlockVo blockVo = new BlockVo();
            String hash = block.getHash();
            blockVo.setHash(hash );
            blockVo.setGasLimit( block.getGasLimit());
            blockVo.setNumber(block.getNumber());
            String timestamp = block.getTimestamp();
            LocalDateTime localDateTime = timestampToLocalDateTime(timestamp);
            blockVo.setLocalDateTime( localDateTime);
            blockVo.setParentHash(  block.getParentHash());

            blockVo.setBlock(block);
            list.add(blockVo);
            blockNumbers-=1;

        }

        blockInfoVo.setBlocks(list);

        return ResultVO.succeed(blockInfoVo);
    }

    private LocalDateTime timestampToLocalDateTime(String var0) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(var0.substring(2), 16) / 1000L), ZoneId.systemDefault());
    }

}
