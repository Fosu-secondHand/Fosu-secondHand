package com.qcq.second_hand.service.Impl;


import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.Messages;
import com.qcq.second_hand.mapper.ChatSessionMapper;
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

    public Messages saveMessage(Messages messages)
    {
        return messagesRepository.save(messages);
    }

    public ChatSession findSession(Long sender,Long getter)
    {
        return chatSessionRepository.findByUserIdAndTargetId(sender,getter);
    }

    public ChatSession saveChatSession(ChatSession chatSession)
    {
        return chatSessionRepository.save(chatSession);
    }

    public ChatSession updateChatSession(ChatSession chatSession)
    {
        return chatSessionRepository.updateChatSessionByUserIdAndTargetId(chatSession.getUnreadCount(),chatSession.getLastMessage(),chatSession.getUserId(),chatSession.getTargetId());
    }

    public List<ChatSession> selectListOfChatSession(Long userId)
    {
        return chatSessionMapper.selectByUserId(userId);
    }
}
