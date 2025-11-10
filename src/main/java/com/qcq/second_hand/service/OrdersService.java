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

    /**
     * 创建订单并更新商品状态
     * @param productId 商品ID
     * @param buyerId 买家ID
     * @param deliveryMethod 交付方式
     * @param buyerRemark 买家备注
     * @return 创建的订单对象
     */
    Orders createOrder(Long productId, Long buyerId, String deliveryMethod, String buyerRemark);
}
