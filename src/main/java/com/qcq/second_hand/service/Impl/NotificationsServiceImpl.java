package com.qcq.second_hand.service.Impl;

import com.qcq.second_hand.entity.Notifications;
import com.qcq.second_hand.mapper.NotificationsMapper;
import com.qcq.second_hand.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationsServiceImpl implements NotificationsService {

    private final NotificationsMapper notificationsMapper;

    @Override
    public Notifications createNotification(Notifications notifications) {
        if (notifications.getCreateTime() == null) {
            notifications.setCreateTime(LocalDateTime.now());
        }
        notificationsMapper.insert(notifications);
        return notifications;
    }

    @Override
    public List<Notifications> getUserNotifications(Long userId) {
        return notificationsMapper.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public List<Notifications> getUnreadNotifications(Long userId) {
        return notificationsMapper.findByUserIdAndIsRead(userId, false);
    }

    @Override
    public Notifications markAsRead(Long notificationId) {
        Notifications notification = notificationsMapper.selectById(notificationId);
        if (notification == null) {
            throw new RuntimeException("通知不存在: " + notificationId);
        }
        notification.setIsRead(true);
        notificationsMapper.updateById(notification);
        return notification;
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationsMapper.updateAllUnreadToReadByUserId(userId);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationsMapper.countByUserIdAndIsRead(userId, false);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationsMapper.deleteById(notificationId);
    }
}
