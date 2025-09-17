package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qcq.second_hand.entity.other.Status;
import com.qcq.second_hand.utils.EncryptedFieldTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: 用户实体类
 * @Date: 2025-07-18
 * @Version:
 */
@Entity
@Data
@Table(name = "users")
public class Users implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Id
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "用户ID")
    @Column(name = "user_id")
    private Long userId;

    /** 微信OpenID（加密）  */
    @TableField(value = "openid", typeHandler = EncryptedFieldTypeHandler.class)
    @Schema(description = "微信OpenID")
    private String openid;

    /** 用户名 */
    @JsonProperty("Username")
    @Schema(description = "用户名")
    private String username;

    /** 头像URL */
    @Schema(description = "头像URL")
    private String avatar;

    /** 手机号（加密） */
    @TableField(value = "phone", typeHandler = EncryptedFieldTypeHandler.class)
    @Schema(description = "手机号（加密）")
    private String phone;

    /** 注册时间 */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "注册时间")
    private LocalDateTime datetime;

    /** 最后登录时间 */
    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLogin;

    /** 信用评分 */
    @Schema(description = "信用评分")
    private Byte creditScore;

    /** 用户状态 */
    @Schema(description = "用户状态")
    private Status status;

    /**
     * 用户状态枚举
     */
    public enum StatusEnum {
        ACTIVE(0, "活跃"),
        FROZEN(1, "冻结"),
        BANNED(2, "封禁");

        private final int code;
        private final String desc;

        StatusEnum(int code, String desc) {
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
