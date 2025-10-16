package com.qcq.second_hand.mapper;

import com.qcq.second_hand.entity.products;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProductsMapper {
    // 查询用户所有的商品
    List<products> selectAllProductsByUserId(@Param("userId") Long userId);
    ;
}
