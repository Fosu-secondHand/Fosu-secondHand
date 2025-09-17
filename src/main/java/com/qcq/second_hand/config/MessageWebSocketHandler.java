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
        // 从握手拦截器的属性中获取userId
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            userSessionMap.put(userId, session);
            WebSocketSession receiverSession =session;

            List<ChatSession> chatSessionList=messagesService.selectListOfChatSession(Long.parseLong(userId));

            // 2. 将列表转为JSON字符串
            String json = objectMapper.writeValueAsString(chatSessionList);
            receiverSession.sendMessage(new TextMessage(json));

            System.out.println("用户 " + userId + " 建立WebSocket连接，当前在线数：" + userSessionMap.size()+"  发送会话表列表成功");
        }
    }

    /*
    * 接收前端发送的消息（如客户端主动发送的消息）如果接收方在线就直接发送给接收方如果不在线就存起，然后更新双方的会话表
    * */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId == null) return;

        // 解析前端发送的消息（JSON格式）textMessage.getPayload()：获取前端发送的原始 JSON 字符串；
        //objectMapper.readValue(...)：将 JSON 字符串解析为 Map，方便提取字段。
        Map<String, String> messageMap = objectMapper.readValue(textMessage.getPayload(), Map.class);
        String receiverId = messageMap.get("receiverId"); // 接收者ID
        String content = messageMap.get("content"); // 消息内容
        String orderId = messageMap.get("orderId"); // 订单ID（可选）

        // 保存消息到数据库
        Messages msg = new Messages();
        msg.setSenderId(Long.parseLong(userId));
        msg.setReceiverId(Long.parseLong(receiverId));
        msg.setContent(content);
        msg.setMsgType(0); // 0-文本消息
        msg.setIsRead(0); // 0-未读
        msg.setSendTime(LocalDateTime.now());
        msg.setOrderId(orderId != null ? Long.parseLong(orderId) : null);
        messagesService.saveMessage(msg); // 保存消息


        // 更新会话信息（创建或更新ChatSession）
        ChatSession sessionInfo = messagesService.findSession(
                Long.parseLong(receiverId), // 会话归属用户（接收者）
                Long.parseLong(userId)      // 目标用户（发送者）
        );

        // 构建要推送的消息对象
        Map<String, Object> pushMsg = new HashMap<>();
        pushMsg.put("type", "single"); // 单聊消息
        pushMsg.put("sessionId", sessionInfo != null ? sessionInfo.getUserId() : null);
        pushMsg.put("senderId", userId);
        pushMsg.put("senderName", "发送者名称"); // 实际应从用户表查询
        pushMsg.put("content", content);
        pushMsg.put("sendTime", msg.getSendTime().toString());
        pushMsg.put("msgId", msg.getMessageId());


        // 推送消息给接收者（如果接收者在线）
        WebSocketSession receiverSession = userSessionMap.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(pushMsg)));
        }

        // 如果会话不存在则创建，存在则更新
        if (sessionInfo == null) {
            // 创建新会话
            sessionInfo = new ChatSession();
            sessionInfo.setLastMessage(content);
            sessionInfo.setUserId(Long.parseLong(receiverId));
            sessionInfo.setTargetId(Long.parseLong(userId));
            sessionInfo.setUnreadCount(1);
            sessionInfo.setLastDate(LocalDateTime.now());
            messagesService.saveChatSession(sessionInfo);

            sessionInfo.setUserId(Long.parseLong(receiverId));
            sessionInfo.setTargetId(Long.parseLong(userId));
            messagesService.saveChatSession(sessionInfo);

        } else {
            sessionInfo.setLastDate(LocalDateTime.now());
            sessionInfo.setLastMessage(content);
            // 更新已有会话
            sessionInfo.setUnreadCount(sessionInfo.getUnreadCount() + 1);
            messagesService.updateChatSession(sessionInfo);

            sessionInfo.setUserId(Long.parseLong(userId));
            sessionInfo.setTargetId(Long.parseLong(receiverId));
            sessionInfo.setUnreadCount(0);

        }
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
