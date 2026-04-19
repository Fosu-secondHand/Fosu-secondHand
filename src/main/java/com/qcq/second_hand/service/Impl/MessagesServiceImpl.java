package com.qcq.second_hand.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.Messages;
import com.qcq.second_hand.mapper.ChatSessionMapper;
import com.qcq.second_hand.mapper.MessagesMapper;
import com.qcq.second_hand.repository.ChatSessionRepository;
import com.qcq.second_hand.repository.MessagesRepository;
import com.qcq.second_hand.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagesServiceImpl implements MessagesService {

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    ChatSessionMapper chatSessionMapper;

    @Autowired
    private MessagesMapper messagesMapper;

    @Override
    public List<Messages> getChatHistory(Long userId1, Long userId2) {
        QueryWrapper<Messages> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
                        wrapper.eq("sender_id", userId1).eq("receiver_id", userId2)
                                .or()
                                .eq("sender_id", userId2).eq("receiver_id", userId1)
                )
                .orderByAsc("send_time"); // 按时间升序排列

        return messagesMapper.selectList(queryWrapper);
    }

    @Override
    public List<Messages> getChatHistoryByPage(Long userId1, Long userId2, int page, int size) {
        QueryWrapper<Messages> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
                        wrapper.eq("sender_id", userId1).eq("receiver_id", userId2)
                                .or()
                                .eq("sender_id", userId2).eq("receiver_id", userId1)
                )
                .orderByAsc("send_time"); // 按时间升序排列

        // 分页查询
        Page<Messages> pages = new Page<>(page, size);
        IPage<Messages> result = messagesMapper.selectPage(pages, queryWrapper);

        return result.getRecords();
    }

    @Override
    public Messages saveMessage(Messages messages) {
        return messagesRepository.save(messages);
    }

    @Override
    public ChatSession findSession(Long sender, Long getter) {
        return chatSessionRepository.findByUserIdAndTargetId(sender, getter);
    }

    @Override
    public ChatSession saveChatSession(ChatSession chatSession) {
        return chatSessionRepository.save(chatSession);
    }

    @Override
    public int updateChatSession(ChatSession chatSession) {
        return chatSessionRepository.updateChatSessionByUserIdAndTargetId(
                chatSession.getUnreadCount(),
                chatSession.getLastMessage(),
                chatSession.getLastDate(),
                chatSession.getUserId(),
                chatSession.getTargetId()
        );
    }

    @Override
    public List<ChatSession> selectListOfChatSession(Long userId) {
        return chatSessionMapper.selectByUserId(userId);
    }


    @Override
    public Integer getUnreadCount(Long userId) {
        QueryWrapper<Messages> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId)  // 接收者是该用户的消息
                .eq("is_read", 0);          // 未读状态
        return Math.toIntExact(messagesMapper.selectCount(queryWrapper));
    }

    @Override
    public boolean markAsRead(Long messageId) {
        UpdateWrapper<Messages> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("message_id", messageId)
                .set("is_read", 1);
        return messagesMapper.update(updateWrapper) > 0;
    }

    @Override
    public boolean markMultipleAsRead(List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return false;
        }

        UpdateWrapper<Messages> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("message_id", messageIds)
                .set("is_read", 1);
        return messagesMapper.update(updateWrapper) > 0;
    }

}
