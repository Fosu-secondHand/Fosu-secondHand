package com.qcq.second_hand.service;

import com.qcq.second_hand.entity.Notifications;
import java.util.List;

public interface NotificationsService {

    // 创建通知
    Notifications createNotification(Notifications notifications);

    // 获取用户所有通知
    List<Notifications> getUserNotifications(Long userId);

    // 获取用户未读通知
    List<Notifications> getUnreadNotifications(Long userId);

    // 标记通知为已读
    Notifications markAsRead(Long notificationId);

    // 批量标记为已读
    void markAllAsRead(Long userId);

    // 获取未读通知数量
    Long getUnreadCount(Long userId);

    // 删除通知
    void deleteNotification(Long notificationId);
}