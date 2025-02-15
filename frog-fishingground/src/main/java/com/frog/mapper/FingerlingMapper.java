package com.frog.mapper;

import com.frog.domain.Fingerling;

import java.util.List;

/**
 * 种质Mapper接口
 *
 * @author nealtsiao
 * @date 2023-05-13
 */
public interface FingerlingMapper
{
    /**
     * 查询种质
     *
     * @param fingerlingId 种质主键
     * @return 种质
     */
    public Fingerling selectFingerlingByFingerlingId(Long fingerlingId);

    /**
     * 查询种质列表
     *
     * @param fingerling 种质
     * @return 种质集合
     */
    public List<Fingerling> selectFingerlingList(Fingerling fingerling);

    /**
     * 新增种质
     *
     * @param fingerling 种质
     * @return 结果
     */
    public int insertFingerling(Fingerling fingerling);

    /**
     * 修改种质
     *
     * @param fingerling 种质
     * @return 结果
     */
    public int updateFingerling(Fingerling fingerling);

    /**
     * 删除种质
     *
     * @param fingerlingId 种质主键
     * @return 结果
     */
    public int deleteFingerlingByFingerlingId(Long fingerlingId);

    /**
     * 批量删除种质
     *
     * @param fingerlingIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFingerlingByFingerlingIds(Long[] fingerlingIds);
}
