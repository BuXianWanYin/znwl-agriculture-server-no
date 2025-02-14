package com.frog.mapper;

import java.util.List;
import com.frog.agriculture.domain.Germplasm;
import com.frog.domain.Species;
import org.apache.ibatis.annotations.Select;

/**
 * 鱼种Mapper接口
 *
 */

public interface SpeciesMapper
{
    /**
     * 鱼种
     *
     * @param speciesId 鱼种id
     * @return 鱼种
     */
    public Species selectSpeciesBySpeciesId(Long speciesId);

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
     * 删除鱼种
     *
     * @param speciesId 鱼种主键
     * @return 结果
     */
    public int deleteSpeciesBySpeciesId(Long speciesId);

    /**
     * 批量删除鱼种
     *
     * @param speciesIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSpeciesBySpeciesIds(Long[] speciesIds);
}
