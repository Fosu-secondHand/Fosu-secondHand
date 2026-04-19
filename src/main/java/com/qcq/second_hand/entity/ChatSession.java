package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天会话实体类
 * 存储用户与聊天对象的会话信息，包括未读消息数和最后一条消息
 */
@TableName("chat_session")
@Data
@Entity
@Table(name = "chat_session")
public class ChatSession implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ChatSessionId id;

    /**
     * 当前用户ID
     */
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

    /**
     * 消息类型
     */
    @Column(name = "msg_type")
    private Integer msgType;

    /**
     * 聊天对象ID（用户ID或群组ID）
     */
    @Column(name = "target_id", nullable = false, insertable = false, updatable = false)
    private Long targetId;

    /**
     * 未读消息数量
     */
    @Column(name = "unread_count")
    private Integer unreadCount = 0;

    /**
     * 最后一条消息
     */
    @Column(name = "last_message")
    private String lastMessage;

    /**
     * 最后消息时间
     */
    @Column(name = "last_date")
    private LocalDateTime lastDate;

    public ChatSession() {
    }

    public ChatSession(Long userId, Long targetId, Integer unreadCount, String lastMessage, LocalDateTime lastDate) {
        this.id = new ChatSessionId(userId, targetId);
        this.userId = userId;
        this.targetId = targetId;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
        this.lastDate = lastDate;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null && this.userId != null && this.targetId != null) {
            this.id = new ChatSessionId(this.userId, this.targetId);
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        if (this.id != null) {
            this.id.setUserId(userId);
        } else if (userId != null && this.targetId != null) {
            this.id = new ChatSessionId(userId, this.targetId);
        }
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
        if (this.id != null) {
            this.id.setTargetId(targetId);
        } else if (this.userId != null && targetId != null) {
            this.id = new ChatSessionId(this.userId, targetId);
        }
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastDate() {
        return lastDate;
    }

    public void setLastDate(LocalDateTime lastDate) {
        this.lastDate = lastDate;
    }
}
