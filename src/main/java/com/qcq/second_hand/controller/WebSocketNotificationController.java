// WebSocketNotificationController.java
package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.TradeRequest;
import com.qcq.second_hand.service.NotificationPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketNotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationPushService notificationService;

    // WebSocket连接端点，用于接收订阅请求
    @MessageMapping("/subscribe")
    @SendTo("/topic/notifications")
    public String subscribe(String userId) {
        // 用户订阅通知频道
        return "Subscribed to notifications for user: " + userId;
    }
}
