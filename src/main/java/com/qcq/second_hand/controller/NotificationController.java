package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Notifications;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.NotificationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "通知管理", description = "通知相关接口")
@RestController
@RequestMapping(value = "/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final NotificationsService notificationsService;

    @Autowired
    public NotificationController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @Operation(summary = "创建通知", description = "创建新的通知")
    @PostMapping
    public response<Notifications> createNotification(
            @Parameter(description = "通知信息", required = true)
            @RequestBody Notifications notifications) {
        return response.success(notificationsService.createNotification(notifications));
    }

    @Operation(summary = "获取用户所有通知", description = "根据用户ID获取所有通知")
    @GetMapping("/user/{userId}")
    public response<List<Notifications>> getUserNotifications(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        return response.success(notificationsService.getUserNotifications(userId));
    }

    @Operation(summary = "获取用户未读通知", description = "根据用户ID获取未读通知")
    @GetMapping("/unread/{userId}")
    public response<List<Notifications>> getUnreadNotifications(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        return response.success(notificationsService.getUnreadNotifications(userId));
    }

    @Operation(summary = "标记通知为已读", description = "根据通知ID标记通知为已读")
    @PutMapping("/read/{notificationId}")
    public response<Notifications> markAsRead(
            @Parameter(description = "通知ID", required = true)
            @PathVariable Long notificationId) {
        return response.success(notificationsService.markAsRead(notificationId));
    }

    @Operation(summary = "标记所有通知为已读", description = "根据用户ID标记所有通知为已读")
    @PutMapping("/read-all/{userId}")
    public response<String> markAllAsRead(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        notificationsService.markAllAsRead(userId);
        return response.success("所有通知已标记为已读");
    }

    @Operation(summary = "获取未读通知数量", description = "根据用户ID获取未读通知数量")
    @GetMapping("/unread-count/{userId}")
    public response<Long> getUnreadCount(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        return response.success(notificationsService.getUnreadCount(userId));
    }

    @Operation(summary = "删除通知", description = "根据通知ID删除通知")
    @DeleteMapping("/{notificationId}")
    public response<String> deleteNotification(
            @Parameter(description = "通知ID", required = true)
            @PathVariable Long notificationId) {
        notificationsService.deleteNotification(notificationId);
        return response.success("通知删除成功");
    }
}
