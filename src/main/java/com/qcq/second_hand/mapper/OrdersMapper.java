package com.qcq.second_hand.mapper;

import com.qcq.second_hand.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YOYO
 * @since 2025-09-25
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    // 查询指定商品的已售出总数量
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM orders WHERE product_Id = #{productId}")
    Integer findSoldQuantityByProductId(@Param("productId") Long productId);
}