package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: 举报实体类
 * @Date: 2025-07-18
 * @Version:
 */
@Data
@TableName("reports")
@Schema(description = "举报实体类")
public class Reports implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 举报ID */
    @TableId(value = "report_id", type = IdType.AUTO)
    @Schema(description = "举报ID")
    private Long reportId;

    /** 关联举报人ID */
    @TableField("reporter_id")
    @Schema(description = "关联举报人ID")
    private Long reporterId;

    /** 关联被举报商品ID */
    @TableField("reported_product_id")
    @Schema(description = "关联被举报商品ID")
    private Long reportedProductId;

    /** 关联被举报用户ID */
    @TableField("reported_user_id")
    @Schema(description = "关联被举报用户ID")
    private Long reportedUserId;

    /** 举报原因 */
    @TableField("reason")
    @Schema(description = "举报原因")
    private String reason;

    /** 描述详情 */
    @TableField("description")
    @Schema(description = "描述详情")
    private String description;

    /** 举报次数 */
    @TableField("report_count")
    @Schema(description = "举报次数")
    private Integer reportCount;

    /** 处理结果 */
    @TableField("handle_result")
    @Schema(description = "处理结果")
    private String handleResult;

    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

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
