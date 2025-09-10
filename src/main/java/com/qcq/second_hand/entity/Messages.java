package com.qcq.second_hand.entity;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable; import java.time.LocalDateTime;
/**
 @Description: 消息实体类
 @Date: 2025-07-18
 @Version: */

@Data
@TableName("Messages")
@Schema(description = "消息实体类")
public class Messages implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 消息ID */
    @TableId(value = "message_id", type = IdType.AUTO)
    @Schema(description = "消息ID") private Long messageId;

    /** 关联订单ID */
    @Schema(description = "关联订单ID")
    private Long orderId;

    /** 发送者ID */
    @Schema(description = "发送者ID")
    private Long senderId;

    /** 接收者ID */
    @Schema(description = "接收者ID")
    private Long receiverId;

    /** 消息内容 */
    @Schema(description = "消息内容")
    private String content;

    /** 发送时间 */
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    /** 是否已读（0-未读，1-已读） */
    @Schema(description = "是否已读（0-未读，1-已读）")
    private Integer isRead;

    /** 消息类型（枚举） */
    @Schema(description = "消息类型：0-文本，1-图片，2-文件")
    private Integer msgType;

    /**消息类型枚举 */
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
        public int getCode() { return code;
        }
        public String getDesc() {
            return desc;
        }
    }
}