package com.qcq.second_hand.repository;


import com.qcq.second_hand.entity.ChatSession;
import com.qcq.second_hand.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {

    ChatSession findByUserIdAndTargetId(Long userId, Long targetId);

    @Transactional
    @Modifying
    @Query("UPDATE ChatSession cs SET " +
            "cs.unreadCount = :unreadCount, " +
            "cs.lastMessage = :lastMessage, " +
            "cs.lastDate = :lastDate " +
            "WHERE cs.userId = :userId AND cs.targetId = :targetId")
    int updateChatSessionByUserIdAndTargetId(
            @Param("unreadCount") Integer unreadCount,
            @Param("lastMessage") String lastMessage,
            @Param("lastDate") LocalDateTime lastDate,
            @Param("userId") Long userId,
            @Param("targetId") Long targetId
    );

}
