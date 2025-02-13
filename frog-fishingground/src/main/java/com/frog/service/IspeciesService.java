package com.frog.service;/*
 * @author 不羡晚吟
 * @version 1.0
 * 鱼种 Service 接口
 */

import com.frog.domain.Species;

import java.util.List;

public interface IspeciesService {
    /**
     * 查询鱼种
     *
     * @param species_id 鱼种主键
     * @return 鱼种
     */
    public Species selectSpeciesBySpeciesId(Long species_id);

    /**
     * 查询鱼种列表
     *
     * @param species 鱼种
     * @return 鱼种集合
     */
    public List<Species> selectSpeciesList(Species species);

    /**
     * 新增鱼种
     *
     * @param species 鱼种
     * @return 结果
     */
    public int insertSpecies(Species species);

    /**
     * 修改鱼种
     *
     * @param species 鱼种
     * @return 结果
     */
    public int updateSpecies(Species species);

    /**
     * 批量删除鱼种
     *
     * @param speciesIds 需要删除的鱼种主键集合
     * @return 结果
     */
    public int deleteSpeciesBySpeciesIds(Long[] speciesIds);

    /**
     * 删除鱼种信息
     *
     * @param speciesId 鱼种主键
     * @return 结果
     */
    public int deleteSpeciesBySpeciesId(Long speciesId);
}
