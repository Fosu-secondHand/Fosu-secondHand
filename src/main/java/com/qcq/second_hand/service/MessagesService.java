package com.qcq.second_hand.service;


import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.Messages;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessagesService {

    Messages saveMessage(Messages messages);

    ChatSession findSession(Long sender, Long getter);

    ChatSession saveChatSession(ChatSession chatSession);

    ChatSession updateChatSession(ChatSession chatSession);

    List<ChatSession> selectListOfChatSession(Long userId);

    /**
     * 获取两个用户之间的聊天记录
     * @param userId1 用户 ID1
     * @param userId2 用户 ID2
     * @return 消息列表（按发送时间升序）
     */
    List<Messages> getChatHistory(Long userId1, Long userId2);

    /**
     * 获取两个用户之间的聊天记录（分页）
     * @param userId1 用户 ID1
     * @param userId2 用户 ID2
     * @param page 页码
     * @param size 每页数量
     * @return 消息列表
     */
    List<Messages> getChatHistoryByPage(Long userId1, Long userId2, int page, int size);

    /**
     * 获取用户的未读消息数量
     * @param userId 用户 ID
     * @return 未读消息数量
     */
    Integer getUnreadCount(Long userId);

    /**
     * 标记单条消息为已读
     * @param messageId 消息 ID
     * @return 是否成功
     */
    boolean markAsRead(Long messageId);

    /**
     * 批量标记消息为已读
     * @param messageIds 消息 ID 列表
     * @return 是否成功
     */
    boolean markMultipleAsRead(List<Long> messageIds);


}
