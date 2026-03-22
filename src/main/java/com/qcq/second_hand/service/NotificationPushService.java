// NotificationPushService.java
package com.qcq.second_hand.service;

import com.qcq.second_hand.entity.TradeRequest;
import com.qcq.second_hand.entity.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationPushService {  // 重命名类为 NotificationPushService

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private com.qcq.second_hand.service.NotificationsService notificationsService; // 注入接口

    /**
     * 推送交易请求状态变更通知
     */
    public void notifyTradeRequestStatusChange(TradeRequest tradeRequest, String targetUserId) {
        // 创建通知记录
        Notifications notification = new Notifications();
        notification.setUserId(Long.parseLong(targetUserId));
        notification.setTitle("交易请求状态变更");
        notification.setContent("您的交易请求状态已变更为: " + tradeRequest.getStatus());
        notification.setIsRead(false);
        notification.setCreateTime(LocalDateTime.now()); // 注意：使用 createTime 而不是 createdAt
        notification.setRelatedId(tradeRequest.getRequestId()); // 关联交易请求ID
        notification.setType(Integer.valueOf("TRADE_REQUEST_STATUS_CHANGE"));

        // 保存通知到数据库
        notificationsService.createNotification(notification);

        // 推送实时通知
        messagingTemplate.convertAndSendToUser(targetUserId, "/queue/notifications", notification);
    }

    /**
     * 推送未读消息数量更新通知
     */
    public void notifyUnreadCountUpdate(Long userId, Integer unreadCount) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/unread-count", unreadCount);
    }

    /**
     * 推送新交易请求通知
     */
    public void notifyNewTradeRequest(TradeRequest tradeRequest, String targetUserId) {
        // 创建通知记录
        Notifications notification = new Notifications();
        notification.setUserId(Long.parseLong(targetUserId));
        notification.setTitle("新的交易请求");
        notification.setContent("您收到了一个新的交易请求，来自用户ID: " + tradeRequest.getBuyerId());
        notification.setIsRead(false);
        notification.setCreateTime(LocalDateTime.now()); // 注意：使用 createTime
        notification.setRelatedId(tradeRequest.getRequestId());
        notification.setType(Integer.valueOf("NEW_TRADE_REQUEST"));

        // 保存通知到数据库
        notificationsService.createNotification(notification);

        // 推送实时通知
        messagingTemplate.convertAndSendToUser(targetUserId, "/queue/notifications", notification);
    }

    /**
     * 获取用户未读通知数量
     */
    public Long getUserUnreadCount(Long userId) {
        return notificationsService.getUnreadCount(userId);
    }

    /**
     * 获取用户的通知列表
     */
    public List<Notifications> getUserNotifications(Long userId) {
        return notificationsService.getUserNotifications(userId);
    }

    /**
     * 标记通知为已读
     */
    public Notifications markNotificationAsRead(Long notificationId) {
        return notificationsService.markAsRead(notificationId);
    }

    /**
     * 批量标记用户所有通知为已读
     */
    public void markAllUserNotificationsAsRead(Long userId) {
        notificationsService.markAllAsRead(userId);
    }
}
