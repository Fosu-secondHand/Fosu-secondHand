package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
    /**
     * 当前用户ID
     */
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(name = "msgType")
    private Long msgType;

    /**
     * 聊天对象ID（用户ID或群组ID）
     */
    @Column(name = "target_id", nullable = false)
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


    @Column(name = "last_date")
    private LocalDateTime lastDate;


    public ChatSession() {
    }

    public ChatSession(Long userId, Long targetId, Integer unreadCount, String lastMessage, LocalDateTime lastDate
    ) {
        this.userId=userId;
        this.targetId=targetId;
        this.unreadCount=unreadCount;
        this.lastMessage=lastMessage;
        this.lastDate=lastDate;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMsgType() {
        return msgType;
    }

    public void setMsgType(Long msgType) {
        this.msgType = msgType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
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
