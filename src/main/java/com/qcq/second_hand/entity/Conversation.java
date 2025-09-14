package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户会话表
 * </p>
 *
 * @author YOYO
 * @since 2025-09-14
 */
@Getter
@Setter
@TableName("conversation")
public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 较小的用户ID
     */
    @TableField("user_id_1")
    private Long userId1;

    /**
     * 较大的用户ID
     */
    @TableField("user_id_2")
    private Long userId2;

    /**
     * 未读消息数
     */
    @TableField("unread_count")
    private Integer unreadCount;

    /**
     * 最后一条消息内容
     */
    @TableField("last_message")
    private String lastMessage;

    /**
     * 最后一条消息的ID，用于精准查找
     */
    @TableField("last_message_id")
    private Long lastMessageId;

    /**
     * 最后一条消息的时间
     */
    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
