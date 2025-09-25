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
}
