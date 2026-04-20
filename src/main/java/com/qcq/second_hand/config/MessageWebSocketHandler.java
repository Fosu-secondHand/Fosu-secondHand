package com.qcq.second_hand.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.Messages;
import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;


@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private com.qcq.second_hand.service.UsersService usersService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            userSessionMap.put(userId, session);

            try {
                List<ChatSession> chatSessionList = messagesService.selectListOfChatSession(Long.parseLong(userId));

                if (chatSessionList == null) {
                    chatSessionList = new java.util.ArrayList<>();
                }

                String json = objectMapper.writeValueAsString(chatSessionList);
                session.sendMessage(new TextMessage(json));

                System.out.println("用户 " + userId + " 建立WebSocket连接，当前在线数：" + userSessionMap.size() + "，发送会话列表成功，共 " + chatSessionList.size() + " 条会话");
            } catch (Exception e) {
                System.err.println("用户 " + userId + " 建立连接时发送会话列表失败: " + e.getMessage());
                e.printStackTrace();

                try {
                    String emptyJson = objectMapper.writeValueAsString(new java.util.ArrayList<>());
                    session.sendMessage(new TextMessage(emptyJson));
                } catch (IOException ex) {
                    System.err.println("发送空会话列表也失败: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId == null) return;

        try {
            Map<String, Object> messageMap = objectMapper.readValue(textMessage.getPayload(), Map.class);

            Object receiverIdObj = messageMap.get("receiverId");
            Object contentObj = messageMap.get("content");
            Object msgTypeObj = messageMap.get("msgType");
            Object metadataObj = messageMap.get("metadata");
            Object orderIdObj = messageMap.get("orderId");

            if (receiverIdObj == null || contentObj == null) {
                System.err.println("消息格式错误：缺少必要字段");
                return;
            }

            String receiverId = receiverIdObj.toString();
            String content = contentObj.toString();

            // 1. 获取 msgType
            Integer msgType = msgTypeObj != null ? Integer.parseInt(msgTypeObj.toString()) : 0;

            // 2. 获取 metadata
            String metadata = metadataObj != null ? objectMapper.writeValueAsString(metadataObj) : null;

            // ✅ 智能修复：如果 msgType 是 0 但 metadata 里有 images，自动修正为图片消息
            if (msgType == 0 && metadata != null && metadata.contains("\"images\"")) {
                System.out.println("检测到图片元数据但 msgType 为 0，自动修正为 1");
                msgType = 1;
            }

            String orderId = orderIdObj != null ? orderIdObj.toString() : null;

            Messages msg = new Messages();
            msg.setSenderId(Long.parseLong(userId));
            msg.setReceiverId(Long.parseLong(receiverId));
            msg.setContent(content);
            msg.setMsgType(msgType);
            msg.setMetadata(metadata);
            msg.setIsRead(0);
            msg.setSendTime(LocalDateTime.now());
            msg.setOrderId(orderId != null ? Long.parseLong(orderId) : null);
            messagesService.saveMessage(msg);


            ChatSession sessionInfo = messagesService.findSession(
                    Long.parseLong(receiverId),
                    Long.parseLong(userId)
            );

            Users sender = usersService.getUserById(Long.parseLong(userId));
            String senderName = sender != null ? sender.getNickname() : "未知用户";

            Map<String, Object> pushMsg = new HashMap<>();
            pushMsg.put("type", "single");
            pushMsg.put("sessionId", sessionInfo != null ? sessionInfo.getUserId() : null);
            pushMsg.put("senderId", userId);
            pushMsg.put("senderName", senderName);
            pushMsg.put("content", content);
            pushMsg.put("msgType", msgType);
            pushMsg.put("metadata", metadataObj);
            pushMsg.put("sendTime", msg.getSendTime().toString());
            pushMsg.put("msgId", msg.getMessageId());


            WebSocketSession receiverSession = userSessionMap.get(receiverId);
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(pushMsg)));
            }

            if (sessionInfo == null) {
                sessionInfo = new ChatSession();
                sessionInfo.setUserId(Long.parseLong(receiverId));
                sessionInfo.setTargetId(Long.parseLong(userId));
                sessionInfo.setUnreadCount(1);
                sessionInfo.setLastMessage(getPreviewContent(msgType, content, metadataObj));
                sessionInfo.setLastDate(LocalDateTime.now());
                messagesService.saveChatSession(sessionInfo);

                ChatSession reverseSession = new ChatSession();
                reverseSession.setUserId(Long.parseLong(userId));
                reverseSession.setTargetId(Long.parseLong(receiverId));
                reverseSession.setUnreadCount(0);
                reverseSession.setLastMessage(getPreviewContent(msgType, content, metadataObj));
                reverseSession.setLastDate(LocalDateTime.now());
                messagesService.saveChatSession(reverseSession);

            } else {
                sessionInfo.setLastDate(LocalDateTime.now());
                sessionInfo.setLastMessage(getPreviewContent(msgType, content, metadataObj));
                sessionInfo.setUnreadCount(sessionInfo.getUnreadCount() + 1);
                int updated = messagesService.updateChatSession(sessionInfo);

                if (updated == 0) {
                    System.err.println("更新接收方会话失败，尝试重新创建 - userId: " + receiverId + ", targetId: " + userId);
                    sessionInfo.setUnreadCount(1);
                    messagesService.saveChatSession(sessionInfo);
                }

                ChatSession reverseSession = messagesService.findSession(
                        Long.parseLong(userId),
                        Long.parseLong(receiverId)
                );

                if (reverseSession == null) {
                    reverseSession = new ChatSession();
                    reverseSession.setUserId(Long.parseLong(userId));
                    reverseSession.setTargetId(Long.parseLong(receiverId));
                    reverseSession.setUnreadCount(0);
                    reverseSession.setLastMessage(getPreviewContent(msgType, content, metadataObj));
                    reverseSession.setLastDate(LocalDateTime.now());
                    messagesService.saveChatSession(reverseSession);
                } else {
                    reverseSession.setLastDate(LocalDateTime.now());
                    reverseSession.setLastMessage(getPreviewContent(msgType, content, metadataObj));
                    int reverseUpdated = messagesService.updateChatSession(reverseSession);

                    if (reverseUpdated == 0) {
                        System.err.println("更新发送方会话失败，尝试重新创建 - userId: " + userId + ", targetId: " + receiverId);
                        messagesService.saveChatSession(reverseSession);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("处理消息时发生错误 - 用户ID: " + userId + ", 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getPreviewContent(Integer msgType, String content, Object metadataObj) {
        if (msgType == null || msgType == 0) {
            return content;
        } else if (msgType == 1) {
            return "[图片]";
        } else if (msgType == 2) {
            return "[文件]";
        } else if (msgType == 3) {
            return "[链接]";
        }
        return content;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            userSessionMap.remove(userId);
            System.out.println("用户 " + userId + " 断开WebSocket连接，当前在线数：" + userSessionMap.size());
        }
    }


    public void pushMessageToUser(String userId, Map<String, Object> message) throws IOException {
        WebSocketSession session = userSessionMap.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }
    }

}
