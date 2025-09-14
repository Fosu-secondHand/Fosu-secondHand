package com.qcq.second_hand.repository;


import com.qcq.second_hand.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    public ChatSession findByUserIdAndTargetId(Long userId, Long targetId);

    @Transactional
    @Modifying
    @Query("UPDATE ChatSession cs SET " +
            "cs.unreadCount = :unreadCount, " +
            "cs.lastMessage = :lastMessage " +
            "WHERE cs.userId = :userId AND cs.targetId = :targetId")
    ChatSession updateChatSessionByUserIdAndTargetId(
            @Param("unreadCount") Integer unreadCount,
            @Param("lastMessage") String lastMessage,
            @Param("userId") Long userId,
            @Param("targetId") Long targetId
    );

}
