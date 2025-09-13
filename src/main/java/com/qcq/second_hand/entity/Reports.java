package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 举报实体类
 * @Date: 2025-07-18
 * @Version:
 */
@Entity
@Data
@Table(name = "reports")
public class Reports implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 举报ID */
    @Id
    @TableId(value = "report_id", type = IdType.AUTO)
    @Schema(description = "举报ID")
    private Long reportId;

    /** 关联举报人ID */
    @Schema(description = "关联举报人ID")
    private Long reporterId;

    /** 关联被举报商品ID */
    @Schema(description = "关联被举报商品ID")
    private Long reportedProductId;

    /** 关联被举报用户ID */
    @Schema(description = "关联被举报用户ID")
    private Long reportedUserId;

    /** 举报原因 */
    @Schema(description = "举报原因")
    private String reason;

    /** 描述详情 */
    @Schema(description = "描述详情")
    private String description;

    /** 举报次数 */
    @Schema(description = "举报次数")
    private Integer reportCount;

    /** 处理结果 */
    @Schema(description = "处理结果")
    private String handleResult;

    /**
     * 举报类型枚举
     */
    public enum ReportTypeEnum {
        SPAM(0, "垃圾信息"),
        FRAUD(1, "欺诈行为"),
        INAPPROPRIATE(2, "不当内容");

        private final int code;
        private final String desc;

        ReportTypeEnum(int code, String desc) {
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
