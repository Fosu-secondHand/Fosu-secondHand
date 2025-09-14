package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: 通知实体类
 * @Date: 2025-07-18
 * @Version:
 */
@Data
@TableName("notifications")
@Schema(description = "通知实体类")
public class Notifications implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 通知ID */
    @TableId(value = "notification_id", type = IdType.AUTO)
    @Schema(description = "通知ID")
    private Long notificationId;

    /** 用户ID */
    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    /** 标题 */
    @TableField("title")
    @Schema(description = "标题")
    private String title;

    /** 内容 */
    @TableField("content")
    @Schema(description = "内容")
    private String content;

    /** 类型（枚举） */
    @TableField("type")
    @Schema(description = "类型：1-系统 2-私信 3-交易")
    private Integer type;

    /** 是否已读（0-未读，1-已读） */
    @TableField("is_read")
    @Schema(description = "是否已读（0-未读，1-已读）")
    private Boolean isRead = false;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 关联ID */
    @TableField("related_id")
    @Schema(description = "关联ID")
    private Long relatedId;

    /**
     * 通知类型枚举
     */
    public enum NotificationTypeEnum {
        SYSTEM(1, "系统"),
        MESSAGE(2, "私信"),
        TRANSACTION(3, "交易");

        private final int code;
        private final String desc;

        NotificationTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
