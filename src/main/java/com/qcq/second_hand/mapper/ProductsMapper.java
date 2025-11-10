package com.qcq.second_hand.mapper;

import com.qcq.second_hand.entity.products;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProductsMapper {
    // 查询用户所有的商品
    List<products> selectAllProductsByUserId(@Param("userId") Long userId);

    // 根据分类ID推荐商品（排除指定商品）
    List<products> selectRecommendedProductsByCategoryId(
            @Param("categoryId") Long categoryId,
            @Param("productId") Long productId,
            @Param("limit") int limit
    );

    // 查询用户发布的所有商品（按时间倒序）
    List<products> selectAllProductsBySellerId(@Param("sellerId") Long sellerId);

    // 查询用户购买的所有商品（通过订单表关联）
    List<products> selectAllProductsByBuyerId(@Param("buyerId") Long buyerId);
}
