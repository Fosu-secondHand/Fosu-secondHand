// TradeRequestService.java - 更新接口
package com.qcq.second_hand.service;

import com.qcq.second_hand.entity.TradeRequest;
import java.util.List;

public interface TradeRequestService {
    /**
     * 创建交易请求
     */
    TradeRequest createTradeRequest(Long productId, Long buyerId, String deliveryMethod, Integer quantity, String buyerRemark);

    /**
     * 获取用户收到的交易请求
     */
    List<TradeRequest> getReceivedTradeRequests(Long userId);

    /**
     * 获取用户发出的交易请求
     */
    List<TradeRequest> getSentTradeRequests(Long userId);

    /**
     * 处理交易请求
     */
    TradeRequest processTradeRequest(Long requestId, String action);

    /**
     * 根据ID获取交易请求
     */
    TradeRequest getTradeRequestById(Long requestId);

    /**
     * 标记交易请求为已读
     */
    boolean markAsRead(Long requestId);

    /**
     * 标记多个交易请求为已读
     */
    boolean markMultipleAsRead(List<Long> requestIds);

    /**
     * 获取用户未读交易请求数量
     */
    Integer getUnreadCount(Long userId);

    /**
     * 获取买家的未读交易请求数量（卖家状态变更时通知买家）
     */
    Integer getUnreadCountForBuyer(Long userId);

    /**
     * 标记买家的交易请求为未读（当卖家处理请求时）
     */
    void markAsUnreadForBuyer(Long requestId);
}
