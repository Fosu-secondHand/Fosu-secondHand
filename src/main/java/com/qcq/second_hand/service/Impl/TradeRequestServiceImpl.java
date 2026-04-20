package com.qcq.second_hand.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.Messages;
import com.qcq.second_hand.entity.Orders;
import com.qcq.second_hand.entity.TradeRequest;
import com.qcq.second_hand.entity.other.RequestStatus;
import com.qcq.second_hand.entity.other.productStatus;
import com.qcq.second_hand.entity.products;
import com.qcq.second_hand.mapper.TradeRequestMapper;
import com.qcq.second_hand.repository.productsRepository;
import com.qcq.second_hand.service.NotificationPushService;
import com.qcq.second_hand.service.OrdersService;
import com.qcq.second_hand.service.TradeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class TradeRequestServiceImpl extends ServiceImpl<TradeRequestMapper, TradeRequest> implements TradeRequestService {

    @Autowired
    private productsRepository productsRepository;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private NotificationPushService notificationService;

    @Autowired
    private com.qcq.second_hand.service.MessagesService messagesService;


    @Override
    @Transactional
    public TradeRequest createTradeRequest(Long productId, Long buyerId, String deliveryMethod, Integer quantity, String buyerRemark) {
        // 验证商品是否存在且处于可销售状态
        products product = productsRepository.findByProductId(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        if (product.getStatus() != productStatus.ON_SALE) {
            throw new RuntimeException("商品不可购买，当前状态：" + product.getStatus());
        }

        // 验证买家不能是卖家
        if (buyerId.equals(product.getSellerId())) {
            throw new RuntimeException("不能向自己购买商品");
        }

        // 验证商品数量是否足够
        if (quantity <= 0) {
            throw new RuntimeException("购买数量必须大于0");
        }

        if (quantity > product.getQuantity()) {
            throw new RuntimeException("商品库存不足，当前库存：" + product.getQuantity());
        }

        // 创建交易请求
        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setProductId(productId);
        tradeRequest.setBuyerId(buyerId);
        tradeRequest.setSellerId(product.getSellerId());
        tradeRequest.setPrice(product.getPrice());
        tradeRequest.setQuantity(quantity);
        tradeRequest.setDeliveryMethod(deliveryMethod);
        tradeRequest.setBuyerRemark(buyerRemark);
        tradeRequest.setStatus(RequestStatus.PENDING);
        tradeRequest.setIsRead(0);
        tradeRequest.setRequestTime(LocalDateTime.now());

        // 保存交易请求
        this.save(tradeRequest);

        // ✅ 修复1：在 messages 表中插入一条消息记录（持久化）
        Messages message = new Messages();
        message.setSenderId(buyerId);
        message.setReceiverId(tradeRequest.getSellerId());
        message.setContent("发起了交易请求");
        message.setMsgType(3);
        message.setOrderId(null);
        message.setIsRead(0);
        message.setSendTime(LocalDateTime.now());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "TRADE_REQUEST");
        metadata.put("requestId", tradeRequest.getRequestId());
        metadata.put("productId", productId);
        metadata.put("quantity", quantity);
        metadata.put("price", tradeRequest.getPrice());
        metadata.put("deliveryMethod", deliveryMethod);
        if (buyerRemark != null) {
            metadata.put("buyerRemark", buyerRemark);
        }

        try {
            message.setMetadata(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(metadata));
        } catch (Exception e) {
            throw new RuntimeException("消息元数据序列化失败: " + e.getMessage());
        }

        messagesService.saveMessage(message);

        // ✅ 修复2：确保买卖双方的会话都被正确创建或更新
        LocalDateTime now = LocalDateTime.now();
        String lastMessageContent = "[交易请求] 发起了交易请求";

        // 创建/更新卖家的会话（接收方，未读数+1）
        ChatSession sellerSession = messagesService.findSession(tradeRequest.getSellerId(), buyerId);
        if (sellerSession == null) {
            sellerSession = new ChatSession();
            sellerSession.setUserId(tradeRequest.getSellerId());
            sellerSession.setTargetId(buyerId);
            sellerSession.setUnreadCount(1);
            sellerSession.setLastMessage(lastMessageContent);
            sellerSession.setLastDate(now);
            messagesService.saveChatSession(sellerSession);
        } else {
            sellerSession.setUnreadCount(sellerSession.getUnreadCount() + 1);
            sellerSession.setLastMessage(lastMessageContent);
            sellerSession.setLastDate(now);
            int updated = messagesService.updateChatSession(sellerSession);
            if (updated == 0) {
                System.err.println("更新卖家会话失败，重新创建 - sellerId: " + tradeRequest.getSellerId() + ", buyerId: " + buyerId);
                messagesService.saveChatSession(sellerSession);
            }
        }

        // ✅ 修复3：创建/更新买家的会话（发送方，未读数为0）
        ChatSession buyerSession = messagesService.findSession(buyerId, tradeRequest.getSellerId());
        if (buyerSession == null) {
            buyerSession = new ChatSession();
            buyerSession.setUserId(buyerId);
            buyerSession.setTargetId(tradeRequest.getSellerId());
            buyerSession.setUnreadCount(0);
            buyerSession.setLastMessage(lastMessageContent);
            buyerSession.setLastDate(now);
            messagesService.saveChatSession(buyerSession);
        } else {
            buyerSession.setLastMessage(lastMessageContent);
            buyerSession.setLastDate(now);
            int updated = messagesService.updateChatSession(buyerSession);
            if (updated == 0) {
                System.err.println("更新买家会话失败，重新创建 - buyerId: " + buyerId + ", sellerId: " + tradeRequest.getSellerId());
                messagesService.saveChatSession(buyerSession);
            }
        }

        // ✅ 修复4：使用正确的通知方法（新请求，不是状态变更）
        notificationService.notifyNewTradeRequest(tradeRequest, tradeRequest.getSellerId().toString());

        // 更新卖家的未读消息数
        Integer unreadCount = getUnreadCount(tradeRequest.getSellerId());
        notificationService.notifyUnreadCountUpdate(tradeRequest.getSellerId(), unreadCount);

        return tradeRequest;
    }


    @Override
    public List<TradeRequest> getReceivedTradeRequests(Long userId) {
        return baseMapper.selectReceivedRequests(userId); // 使用自定义查询方法
    }

    @Override
    public List<TradeRequest> getSentTradeRequests(Long userId) {
        return baseMapper.selectSentRequests(userId); // 使用自定义查询方法
    }

    @Override
    @Transactional
    public TradeRequest processTradeRequest(Long requestId, String action) {
        // 获取交易请求
        TradeRequest tradeRequest = this.getById(requestId);
        if (tradeRequest == null) {
            throw new RuntimeException("交易请求不存在");
        }

        if (tradeRequest.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("交易请求已被处理");
        }

        RequestStatus oldStatus = tradeRequest.getStatus(); // 保存旧状态

        if ("ACCEPTED".equalsIgnoreCase(action)) {
            // 在接受交易前再次验证库存
            products product = productsRepository.findByProductId(tradeRequest.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            // 验证商品是否仍处于可销售状态
            if (product.getStatus() != productStatus.ON_SALE) {
                throw new RuntimeException("商品不可购买，当前状态：" + product.getStatus());
            }

            // 验证库存是否充足
            if (tradeRequest.getQuantity() > product.getQuantity()) {
                throw new RuntimeException("商品库存不足，当前库存：" + product.getQuantity());
            }

            // 检查是否已经存在基于此交易请求的订单（防止重复创建）
            QueryWrapper<Orders> orderQuery = new QueryWrapper<>();
            orderQuery.eq("product_Id", tradeRequest.getProductId())
                    .eq("buyer_Id", tradeRequest.getBuyerId())
                    .eq("quantity", tradeRequest.getQuantity())
                    .ge("order_Time", tradeRequest.getRequestTime()); // 订单时间应该在请求时间之后
            long existingOrdersCount = ordersService.count(orderQuery);

            if (existingOrdersCount > 0) {
                throw new RuntimeException("订单已存在，请勿重复处理");
            }

            tradeRequest.setStatus(RequestStatus.ACCEPTED);

            // 创建订单
            Orders order = new Orders();
            order.setProductId(tradeRequest.getProductId());
            order.setBuyerId(tradeRequest.getBuyerId());
            order.setSellerId(tradeRequest.getSellerId());
            order.setPrice(tradeRequest.getPrice());
            order.setQuantity(tradeRequest.getQuantity());
            order.setOrderTime(LocalDateTime.now());
            order.setDeliveryMethod(tradeRequest.getDeliveryMethod());
            order.setStatus("paid");
            order.setBuyerRemark(tradeRequest.getBuyerRemark());

            boolean orderCreated = ordersService.save(order);
            if (!orderCreated) {
                throw new RuntimeException("订单创建失败");
            }

            // 如果需要，可以在交易请求中设置关联的订单ID（仅在内存中）
            tradeRequest.setOrderId(order.getOrderId());

            // 更新商品库存和状态
            if (product != null) {
                // 减少商品库存
                int remainingQuantity = product.getQuantity() - tradeRequest.getQuantity();
                product.setQuantity(Math.max(0, remainingQuantity));

                // 如果库存为0，则标记为已售罄
                if (product.getQuantity() <= 0) {
                    product.setStatus(productStatus.SOLD);
                }
                productsRepository.save(product);
            }
        }
        else if ("REJECTED".equalsIgnoreCase(action)) {
            tradeRequest.setStatus(RequestStatus.REJECTED);
        } else {
            throw new RuntimeException("无效的操作类型");
        }

        tradeRequest.setResponseTime(LocalDateTime.now());
        this.updateById(tradeRequest);

        // 状态发生变化后，需要通知相关用户
        if (oldStatus != tradeRequest.getStatus()) {
            // 通知买家交易请求状态已变更（设为未读）
            markAsUnreadForBuyer(tradeRequest.getRequestId());

            // 推送状态变更通知给买家
            notificationService.notifyTradeRequestStatusChange(tradeRequest, tradeRequest.getBuyerId().toString());

            // 更新买家的未读消息数
            Integer unreadCount = getUnreadCountForBuyer(tradeRequest.getBuyerId());
            notificationService.notifyUnreadCountUpdate(tradeRequest.getBuyerId(), unreadCount);
        }

        return tradeRequest;
    }

    @Override
    public TradeRequest getTradeRequestById(Long requestId) {
        TradeRequest tradeRequest = this.getById(requestId);
        if (tradeRequest != null) {
            // 标记为已读
            markAsRead(requestId);
        }
        return tradeRequest;
    }

    @Override
    @Transactional
    public boolean markAsRead(Long requestId) {
        UpdateWrapper<TradeRequest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("request_id", requestId)
                .set("is_read", 1);
        return this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean markMultipleAsRead(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            return false;
        }
        UpdateWrapper<TradeRequest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("request_id", requestIds)
                .set("is_read", 1);
        return this.update(updateWrapper);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        QueryWrapper<TradeRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_id", userId)  // 对于卖家来说，收到的请求是需要关注的
                .eq("is_read", 0);        // 未读状态
        return Math.toIntExact(this.count(queryWrapper));
    }

    /**
     * 获取买家的未读交易请求数量（卖家状态变更时通知买家）
     */
    public Integer getUnreadCountForBuyer(Long userId) {
        QueryWrapper<TradeRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("buyer_id", userId)  // 对于买家来说，卖家的回应是需要关注的
                .eq("is_read", 0);        // 未读状态
        return Math.toIntExact(this.count(queryWrapper));
    }

    /**
     * 标记买家的交易请求为未读（当卖家处理请求时）
     */
    @Transactional
    public void markAsUnreadForBuyer(Long requestId) {
        UpdateWrapper<TradeRequest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("request_id", requestId)
                .set("is_read", 0); // 设为未读
        this.update(updateWrapper);
    }

    // 添加一个获取未读交易请求的方法
    public List<TradeRequest> getUnreadTradeRequests(Long userId) {
        QueryWrapper<TradeRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_id", userId)
                .eq("is_read", 0)
                .orderByDesc("request_time");
        return this.list(queryWrapper);
    }
}
