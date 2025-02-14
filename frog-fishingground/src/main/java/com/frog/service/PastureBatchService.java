package com.frog.service;

import java.util.List;

import com.frog.domain.PastureBatch;

/**
 * 鱼物批次Service接口
 *
 * @author aj
 * @date 2023-05-13
 */
public interface PastureBatchService {
    /**
     * 查询鱼物批次
     *
     * @param batchId 鱼物批次主键
     * @return 鱼物批次
     */
    public PastureBatch selectPastureBatchByBatchId(Long batchId);

    /**
     * 查询鱼物批次列表
     *
     * @param PastureBatch 鱼物批次
     * @return 鱼物批次集合
     */
    public List<PastureBatch> selectPastureBatchList(PastureBatch PastureBatch);

    /**
     * 新增鱼物批次
     *
     * @param PastureBatch 鱼物批次
     * @return 结果
     */
    public int insertPastureBatch(PastureBatch PastureBatch);

    /**
     * 修改鱼物批次
     *
     * @param PastureBatch 鱼物批次
     * @return 结果
     */
    public int updatePastureBatch(PastureBatch PastureBatch);

    /**
     * 批量删除鱼物批次
     *
     * @param batchIds 需要删除的鱼物批次主键集合
     * @return 结果
     */
    public int deletePastureBatchByBatchIds(Long[] batchIds);

    /**
     * 删除鱼物批次信息
     *
     * @param batchId 鱼物批次主键
     * @return 结果
     */
    public int deletePastureBatchByBatchId(Long batchId);

    /**
     * 给手机端批次列表查询数据
     *
     * @param PastureBatch
     * @return
     */
    public List<PastureBatch> selectPastureBatchListToMobile(PastureBatch PastureBatch);
}
