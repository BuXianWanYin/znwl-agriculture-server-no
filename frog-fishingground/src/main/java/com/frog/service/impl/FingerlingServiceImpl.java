package com.frog.service.impl;

import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.Fingerling;
import com.frog.mapper.FingerlingMapper;
import com.frog.service.IFingerlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 种质Service业务层处理
 *
 * @author nealtsiao
 * @date 2023-05-18
 */
@Service
public class FingerlingServiceImpl implements IFingerlingService
{
    @Autowired
    private FingerlingMapper fingerlingMapper;

    /**
     * 查询种质
     *
     * @param fingerlingId 种质主键
     * @return 种质
     */
    @Override
    public Fingerling selectFingerlingByFingerlingId(Long fingerlingId)
    {
        return fingerlingMapper.selectFingerlingByFingerlingId(fingerlingId);
    }

    /**
     * 查询种质列表
     *
     * @param fingerling 种质
     * @return 种质
     */
    @Override
    public List<Fingerling> selectFingerlingList(Fingerling fingerling)
    {
        return fingerlingMapper.selectFingerlingList(fingerling);
    }

    /**
     * 新增种质
     *
     * @param fingerling 种质
     * @return 结果
     */
    @Override
    public int insertFingerling(Fingerling fingerling)
    {
        fingerling.setCreateBy(SecurityUtils.getUserId().toString());
        fingerling.setCreateTime(DateUtils.getNowDate());
        return fingerlingMapper.insertFingerling(fingerling);
    }

    /**
     * 修改种质
     *
     * @param fingerling 种质
     * @return 结果
     */
    @Override
    public int updateFingerling(Fingerling fingerling)
    {
        fingerling.setUpdateBy(SecurityUtils.getUserId().toString());
        fingerling.setUpdateTime(DateUtils.getNowDate());
        return fingerlingMapper.updateFingerling(fingerling);
    }

    /**
     * 批量删除种质
     *
     * @param fingerlingIds 需要删除的种质主键
     * @return 结果
     */
    @Override
    public int deleteFingerlingByFingerlingIds(Long[] fingerlingIds)
    {
        return fingerlingMapper.deleteFingerlingByFingerlingIds(fingerlingIds);
    }

    /**
     * 删除种质信息
     *
     * @param fingerlingId 种质主键
     * @return 结果
     */
    @Override
    public int deleteFingerlingByFingerlingId(Long fingerlingId)
    {
        return fingerlingMapper.deleteFingerlingByFingerlingId(fingerlingId);
    }
}
