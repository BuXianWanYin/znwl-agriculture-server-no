package com.frog.service;

import java.util.List;

import com.frog.agriculture.domain.CropBatch;

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
    public CropBatch selectCropBatchByBatchId(Long batchId);

    /**
     * 查询鱼物批次列表
     *
     * @param cropBatch 鱼物批次
     * @return 鱼物批次集合
     */
    public List<CropBatch> selectCropBatchList(CropBatch cropBatch);

    /**
     * 新增鱼物批次
     *
     * @param cropBatch 鱼物批次
     * @return 结果
     */
    public int insertCropBatch(CropBatch cropBatch);

    /**
     * 修改鱼物批次
     *
     * @param cropBatch 鱼物批次
     * @return 结果
     */
    public int updateCropBatch(CropBatch cropBatch);

    /**
     * 批量删除鱼物批次
     *
     * @param batchIds 需要删除的鱼物批次主键集合
     * @return 结果
     */
    public int deleteCropBatchByBatchIds(Long[] batchIds);

    /**
     * 删除鱼物批次信息
     *
     * @param batchId 鱼物批次主键
     * @return 结果
     */
    public int deleteCropBatchByBatchId(Long batchId);

    /**
     * 给手机端批次列表查询数据
     *
     * @param cropBatch
     * @return
     */
    public List<CropBatch> selectCropBatchListToMobile(CropBatch cropBatch);
}
