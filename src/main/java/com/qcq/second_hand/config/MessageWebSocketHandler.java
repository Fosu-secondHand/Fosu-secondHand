package com.qcq.second_hand.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.Messages;
import com.qcq.second_hand.service.MessagesService;
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


//处理连接、消息接收，并实现向指定用户推送消息的功能
@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {

    // 1. 线程安全的用户-会话映射表：key=用户ID（String），value=WebSocket会话（WebSocketSession）
    private final Map<String, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();

    // 2. 消息服务：注入后用于操作Messages表（保存、查询消息）
    @Autowired
    private MessagesService messagesService; // 消息服务（保存消息到数据库）

    // 3. JSON工具：用于解析前端发送的JSON消息，以及序列化推送消息
    @Autowired
    private ObjectMapper objectMapper; // JSON序列化工具

    /*
    * 连接建立时触发：将用户ID与Session绑定
    * 建立连接时自动获取所有关于该用户的会话表
    * */
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

    /*
    * 接收前端发送的消息（如客户端主动发送的消息）如果接收方在线就直接发送给接收方如果不在线就存起，然后更新双方的会话表
    * */
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
            Integer msgType = msgTypeObj != null ? Integer.parseInt(msgTypeObj.toString()) : 0;
            String metadata = metadataObj != null ? objectMapper.writeValueAsString(metadataObj) : null;
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

            Map<String, Object> pushMsg = new HashMap<>();
            pushMsg.put("type", "single");
            pushMsg.put("sessionId", sessionInfo != null ? sessionInfo.getUserId() : null);
            pushMsg.put("senderId", userId);
            pushMsg.put("senderName", "发送者名称");
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
                sessionInfo.setLastMessage(getPreviewContent(msgType, content, metadataObj));
                sessionInfo.setUserId(Long.parseLong(receiverId));
                sessionInfo.setTargetId(Long.parseLong(userId));
                sessionInfo.setUnreadCount(1);
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
                messagesService.updateChatSession(sessionInfo);

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
                    messagesService.updateChatSession(reverseSession);
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

    // 3. 连接关闭时触发：移除用户Session
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            userSessionMap.remove(userId);
            System.out.println("用户 " + userId + " 断开WebSocket连接，当前在线数：" + userSessionMap.size());
        }
    }


    // 4. 主动推送消息给指定用户（供其他服务调用）
    public void pushMessageToUser(String userId, Map<String, Object> message) throws IOException {
        WebSocketSession session = userSessionMap.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        }
    }

}
