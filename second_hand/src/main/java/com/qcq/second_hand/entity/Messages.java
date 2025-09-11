package com.qcq.second_hand.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: 消息实体类（JPA版本）
 * @Date: 2025-07-18
 * @Version:
 */
@Data
@Entity
@Table(name = "messages")  // 指定数据库表名
@Schema(description = "消息实体类")
public class Messages implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 主键自增策略（依赖数据库）
    @Column(name = "message_id", nullable = false)  // 映射数据库字段，非空
    @Schema(description = "消息ID")
    private Long messageId;

    /** 关联订单ID */
    @Column(name = "order_id")  // 数据库字段名使用下划线命名法
    @Schema(description = "关联订单ID")
    private Long orderId;

    /** 发送者ID */
    @Column(name = "sender_id", nullable = false)  // 非空字段
    @Schema(description = "发送者ID")
    private Long senderId;

    /** 接收者ID */
    @Column(name = "receiver_id", nullable = false)  // 非空字段
    @Schema(description = "接收者ID")
    private Long receiverId;

    /** 消息内容 */
    @Column(name = "content", nullable = false, length = 500)  // 限制长度，非空
    @Schema(description = "消息内容")
    private String content;

    /** 发送时间 */
    @Column(name = "send_time", nullable = false)  // 非空字段
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    /** 是否已读（0-未读，1-已读） */
    @Column(name = "is_read", nullable = false)
    @Schema(description = " 是否已读（0-未读，1-已读）")
    private Integer isRead = 0;  // 默认未读

    /** 消息类型（0-文本，1-图片，2-文件） */
    @Column(name = "msg_type", nullable = false)
    @Schema(description = "消息类型（0-文本，1-图片，2-文件）")
    private Integer msgType;

    /**
     * 消息类型枚举
     */
    public enum MsgTypeEnum {
        TEXT(0, "文本"),
        IMAGE(1, "图片"),
        FILE(2, "文件");

        private final int code;
        private final String desc;

        MsgTypeEnum(int code, String desc) {
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

    // Lombok的@Data已经生成了getter、setter、toString等方法，无需手动编写
}
