package com.frog.IaAgriculture.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.frog.IaAgriculture.model.IaFeeding;


/**
 * 施肥
 */
@Mapper
public interface IaFeedingMapper extends BaseMapper<IaFeeding> {


    @Select("SELECT " +
            "  ia.*, " +
            "  ip.partition_name  " +
            " FROM " +
            " `ia_feeding` ia " +
            " LEFT JOIN ia_partition ip ON ip.id = ia.ia_partition_id " +
            " ${ew.customSqlSegment}")
    Page<IaFeeding> pageList(@Param("ew") QueryWrapper<IaFeeding> wrapper, Page<IaFeeding> page);
}
