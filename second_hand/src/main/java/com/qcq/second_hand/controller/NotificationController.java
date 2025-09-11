package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Notifications;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final NotificationsService notificationsService;

    @Autowired
    public NotificationController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    // 创建通知
    @PostMapping
    public response<Notifications> createNotification(@RequestBody Notifications notifications) {
        return response.success(notificationsService.createNotification(notifications));
    }

    // 获取用户所有通知
    @GetMapping("/user/{userId}")
    public response<List<Notifications>> getUserNotifications(@PathVariable Long userId) {
        return response.success(notificationsService.getUserNotifications(userId));
    }

    // 获取用户未读通知
    @GetMapping("/unread/{userId}")
    public response<List<Notifications>> getUnreadNotifications(@PathVariable Long userId) {
        return response.success(notificationsService.getUnreadNotifications(userId));
    }

    // 标记通知为已读
    @PutMapping("/read/{notificationId}")
    public response<Notifications> markAsRead(@PathVariable Long notificationId) {
        return response.success(notificationsService.markAsRead(notificationId));
    }

    // 标记所有通知为已读
    @PutMapping("/read-all/{userId}")
    public response<String> markAllAsRead(@PathVariable Long userId) {
        notificationsService.markAllAsRead(userId);
        return response.success("所有通知已标记为已读");
    }

    // 获取未读通知数量
    @GetMapping("/unread-count/{userId}")
    public response<Long> getUnreadCount(@PathVariable Long userId) {
        return response.success(notificationsService.getUnreadCount(userId));
    }

    // 删除通知
    @DeleteMapping("/{notificationId}")
    public response<String> deleteNotification(@PathVariable Long notificationId) {
        notificationsService.deleteNotification(notificationId);
        return response.success("通知删除成功");
    }
}
