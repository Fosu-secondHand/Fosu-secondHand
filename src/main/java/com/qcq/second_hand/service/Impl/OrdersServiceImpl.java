package com.qcq.second_hand.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcq.second_hand.entity.Orders;
import com.qcq.second_hand.entity.products;
import com.qcq.second_hand.entity.other.productStatus;
import com.qcq.second_hand.mapper.OrdersMapper;
import com.qcq.second_hand.repository.productsRepository;
import com.qcq.second_hand.service.OrdersService;
import com.qcq.second_hand.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author YOYO
 * @since 2025-09-25
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private productsRepository productsRepository;

    @Autowired
    private UsersService usersService;

//    // 在 createOrder 方法中添加数量参数和更新逻辑
//
//    @Override
//    @Transactional
//    public Orders createOrder(Long productId, Long buyerId, String deliveryMethod, String buyerRemark, Integer quantity) {
//        try {
//            // 参数验证
//            if (productId == null || buyerId == null || deliveryMethod == null) {
//                throw new RuntimeException("参数不能为空");
//            }
//
//            // 验证购买数量
//            if (quantity == null || quantity <= 0) {
//                throw new RuntimeException("购买数量必须大于0");
//            }
//
//            // 检查是否存在相同参数的未处理订单（防止重复提交）
//            QueryWrapper<Orders> duplicateCheck = new QueryWrapper<>();
//            duplicateCheck.eq("product_Id", productId)
//                    .eq("buyer_Id", buyerId)
//                    .eq("quantity", quantity)
//                    .eq("status", "paid");
//
//            // 检查最近5分钟内是否有相同订单
//            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
//            duplicateCheck.ge("order_Time", fiveMinutesAgo);
//
//            long duplicateCount = this.count(duplicateCheck);
//            if (duplicateCount > 0) {
//                throw new RuntimeException("订单正在处理中，请勿重复提交");
//            }
//
//            // 检查商品是否存在且处于可销售状态
//            products product = productsRepository.findByProductId(productId);
//            if (product == null) {
//                throw new RuntimeException("商品不存在");
//            }
//
//            if (product.getStatus() != productStatus.ON_SALE) {
//                throw new RuntimeException("商品不可购买，当前状态：" + product.getStatus());
//            }
//
//            // 验证库存是否足够
//            if (product.getQuantity() < quantity) {
//                throw new RuntimeException("库存不足，当前库存：" + product.getQuantity() + "，请求购买：" + quantity);
//            }
//
//            // 验证买家ID是否存在且不是卖家本人
//            if (buyerId.equals(product.getSellerId())) {
//                throw new RuntimeException("不能购买自己发布的商品");
//            }
//
//            // 创建订单
//            Orders order = new Orders();
//            order.setProductId(productId);
//            order.setBuyerId(buyerId);
//            order.setSellerId(product.getSellerId());
//            order.setPrice(product.getPrice());
//            order.setQuantity(quantity); // 使用传入的数量
//            order.setOrderTime(LocalDateTime.now());
//
//            // 限制交付方式长度
//            if (deliveryMethod.length() > 50) {
//                deliveryMethod = deliveryMethod.substring(0, 50);
//            }
//            order.setDeliveryMethod(deliveryMethod);
//
//            order.setStatus("paid"); // 订单初始状态
//
//            // 限制买家备注长度
//            if (buyerRemark != null && buyerRemark.length() > 200) {
//                buyerRemark = buyerRemark.substring(0, 200);
//            }
//            order.setBuyerRemark(buyerRemark);
//
//            // 保存订单
//            boolean saveResult = this.save(order);
//            if (!saveResult) {
//                throw new RuntimeException("订单保存失败");
//            }
//
//            // 更新商品库存和状态
//            int remainingQuantity = product.getQuantity() - quantity;
//            product.setQuantity(Math.max(0, remainingQuantity)); // 确保不会小于0
//
//            // 如果库存为0，则标记为已售罄
//            if (product.getQuantity() <= 0) {
//                product.setStatus(productStatus.SOLD);
//            }
//            // 否则保持在售状态
//            boolean updateResult = productsRepository.save(product) != null;
//            if (!updateResult) {
//                throw new RuntimeException("商品状态更新失败");
//            }
//
//            return order;
//        } catch (Exception e) {
//            throw new RuntimeException("创建订单过程中发生错误: " + e.getMessage(), e);
//        }
//    }


    // 在 OrdersServiceImpl.java 中添加
@Override
public Integer getSoldQuantityByProductId(Long productId) {
    return baseMapper.findSoldQuantityByProductId(productId);
}

}