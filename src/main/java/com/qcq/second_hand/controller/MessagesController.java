package com.qcq.second_hand.controller;


import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.Messages;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.MessagesService;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/message")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;


    /*
    * 前端发送sender，getter，message，senderName保存消息和会话
    * */
    @PostMapping("/sendMessage")
    public response sendMessages(@RequestBody Map<String, String> map) {
        // 解析参数（已有代码）
        String sender = map.get("sender");
        String getter = map.get("getter");
        String message = map.get("message");
        String orderId = map.get("orderId");
        String senderName = map.get("senderName");

        Long senderId = Long.parseLong(senderName);
        Long getterId = Long.parseLong(getter);

        //保存消息
        Messages msg = new Messages();
        msg.setContent(message);
        msg.setMsgType(0); // 假设0是文本消息
        msg.setIsRead(0); // 0表示未读
        msg.setSenderId(senderId);
        msg.setReceiverId(getterId);
        msg.setSendTime(LocalDateTime.now());
        msg.setOrderId(Long.parseLong(orderId));
        messagesService.saveMessage(msg);

        //查找是否存在会话
        ChatSession chatSession=messagesService.findSession(senderId,getterId);
        if(chatSession!=null)
        {
            chatSession.setLastMessage(message);
            chatSession.setUnreadCount(chatSession.getUnreadCount()+1);
        }
        else
        {
            chatSession = new ChatSession();
            // 基本信息：当前会话属于接收者（getter），目标是发送者（sender）
            chatSession.setUserId(getterId); // 会话归属用户（接收消息的用户）
            chatSession.setTargetId(getterId); // 聊天对象ID（发送消息的用户）
            chatSession.setUnreadCount(1);

            messagesService.saveChatSession(chatSession);
        }
        return response.success("消息新增成功，会话保存成功");
    }

}
