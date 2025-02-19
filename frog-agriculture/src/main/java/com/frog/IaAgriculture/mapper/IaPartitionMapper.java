package com.frog.IaAgriculture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.frog.IaAgriculture.dto.IvLivestockDTO;
import com.frog.IaAgriculture.dto.IvLivestockOutDTO;
import com.frog.IaAgriculture.dto.MonthlyProcessCountDTO;
import com.frog.IaAgriculture.dto.VarietyCountDTO;
import com.frog.IaAgriculture.model.entity.IaPartition;

import java.util.List;

/**
 * @ Description   :
 * @ Author        :  morton
 * @ CreateDate    :  2024/10/13 17:16
 * @ Version       :  1.0
 */
@Mapper
public interface IaPartitionMapper extends BaseMapper<IaPartition> {

    @Select({
            "<script>",
            "SELECT l.id, l.contract_addr, l.variety, l.partition_name, l.plant_name, l.plant_date, p.name, l.remark",
            "FROM ia_partition l",
            "LEFT JOIN ia_pasture p ON l.pasture_id = p.id",
            "WHERE l.status=0",
            "<if test='name != null and name.trim() != \"\"'>",
            "AND p.name LIKE CONCAT('%', #{name}, '%')",
            "</if>",
            "<if test='partitionId != null and partitionId.trim() != \"\"'>",
            "AND l.id = #{partitionId}",
            "</if>",
            "</script>"
    })
    Page<IvLivestockDTO> selectPartitionWithPagination(
            Page<IvLivestockDTO> page,
            @Param("name") String name,
            @Param("partitionId") String partitionId
    );


    @Select({
            "<script>",
            "SELECT l.id, l.variety, l.plant_name, l.plant_date, p.name, l.process_state,",
            "(SELECT COUNT(*) FROM ia_partition_food s WHERE s.ia_partition_id = l.id) AS number,",  // 使用子查询计算总数量
            "l.process_date, l.remark",
            "FROM ia_partition l",
            "LEFT JOIN ia_pasture p ON l.pasture_id = p.id",
            "WHERE l.status = 1",
            "<if test='name != null and name.trim() != \"\"'>",
            "AND p.name LIKE CONCAT('%', #{name}, '%')",
            "</if>",
            "<if test='livestockId != null and livestockId.trim() != \"\"'>",
            "AND l.id = #{livestockId}",
            "</if>",
            "</script>"
    })
    Page<IvLivestockOutDTO> selectLivestockOut(Page<IvLivestockOutDTO> pageNum,
                                               @Param("name") String name,
                                               @Param("livestockId") String livestockId);


    @Select("SELECT variety, COUNT(*) as count FROM ia_partition GROUP BY variety")
    List<VarietyCountDTO> selectVarietyCount();

    @Select("SELECT MONTH(harvest_date) AS month, COUNT(*) AS count " +
            "FROM ia_partition " +
            "WHERE YEAR(harvest_date) = #{year} AND status = 1 " +
            "GROUP BY MONTH(harvest_date) " +
            "ORDER BY month")
    List<MonthlyProcessCountDTO> monthlyOutFence(int year);


}
