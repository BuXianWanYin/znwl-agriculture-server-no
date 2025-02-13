package com.frog.service.impl;/*
 * @author 不羡晚吟
 * @version 1.0
 */

import com.frog.agriculture.domain.Germplasm;
import com.frog.common.utils.DateUtils;
import com.frog.common.utils.SecurityUtils;
import com.frog.domain.Species;
import com.frog.mapper.SpeciesMapper;
import com.frog.service.IspeciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeciesServiceImpl implements IspeciesService {

    @Autowired
    private SpeciesMapper speciesMapper;

    /**
     * 查询种质
     *
     * @param speciesId 种质主键
     * @return 种质
     */
    @Override
    public Species selectSpeciesBySpeciesId(Long speciesId)
    {
        return speciesMapper.selectSpeciesBySpeciesId(speciesId);
    }

    /**
     * 查询种质列表
     *
     * @param species 种质
     * @return 种质
     */
    @Override
    public List<Species> selectSpeciesList(Species species)
    {
        return speciesMapper.selectSpeciesList(species);
    }

    /**
     * 新增种质
     *
     * @param species 种质
     * @return 结果
     */
    @Override
    public int insertSpecies(Species species)
    {
        species.setCreateBy(SecurityUtils.getUserId().toString());
        species.setCreateTime(DateUtils.getNowDate());
        return speciesMapper.insertSpecies(species);
    }

    /**
     * 修改种质
     *
     * @param species 种质
     * @return 结果
     */
    @Override
    public int updateSpecies(Species species)
    {
        species.setUpdateBy(SecurityUtils.getUserId().toString());
        species.setUpdateTime(DateUtils.getNowDate());
        return speciesMapper.updateSpecies(species);
    }

    /**
     * 批量删除种质
     *
     * @param germplasmIds 需要删除的种质主键
     * @return 结果
     */
    @Override
    public int deleteSpeciesBySpeciesIds(Long[] germplasmIds)
    {
        return speciesMapper.deleteSpeciesBySpeciesIds(germplasmIds);
    }

    /**
     * 删除种质信息
     *
     * @param germplasmId 种质主键
     * @return 结果
     */
    @Override
    public int deleteSpeciesBySpeciesId(Long germplasmId)
    {
        return speciesMapper.deleteSpeciesBySpeciesId(germplasmId);
    }

}
