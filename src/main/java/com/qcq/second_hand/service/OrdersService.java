package com.qcq.second_hand.service;

import com.qcq.second_hand.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author YOYO
 * @since 2025-09-25
 */

public interface OrdersService extends IService<Orders> {
    // 删除 createOrder 方法，因为我们只通过交易请求创建订单

    Integer getSoldQuantityByProductId(Long productId);
}