package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.qcq.second_hand.entity.other.RequestStatus;
import com.qcq.second_hand.entity.other.RequestStatusConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("trade_requests")
public class TradeRequest {

    /** 请求ID */
    @TableId(value = "request_id", type = IdType.AUTO)
    @Schema(description = "请求ID")
    private Long requestId;

    /** 商品ID */
    @TableField("product_id")
    @Schema(description = "商品ID")
    private Long productId;

    /** 买家ID */
    @TableField("buyer_id")
    @Schema(description = "买家ID")
    private Long buyerId;

    /** 卖家ID */
    @TableField("seller_id")
    @Schema(description = "卖家ID")
    private Long sellerId;

    /** 价格 */
    @TableField("price")
    @Schema(description = "价格")
    private BigDecimal price;

    @TableField("quantity")
    @Schema(description = "购买数量")
    private Integer quantity = 1; // 默认购买数量为1

    /** 交付方式 */
    @TableField("delivery_method")
    @Schema(description = "交付方式")
    private String deliveryMethod;

    /** 买家备注 */
    @TableField("buyer_remark")
    @Schema(description = "买家备注")
    private String buyerRemark;

    /** 状态 */
    @TableField(value = "status", typeHandler = RequestStatusConverter.class)
    @Schema(description = "状态")
    private RequestStatus status;

    /** 是否已读：0-未读，1-已读 */
    @TableField("is_read")
    @Schema(description = "是否已读：0-未读，1-已读")
    private Integer isRead = 0;

    /** 请求时间 */
    @TableField("request_time")
    @Schema(description = "请求时间")
    private LocalDateTime requestTime;

    /** 响应时间 */
    @TableField("response_time")
    @Schema(description = "响应时间")
    private LocalDateTime responseTime;

    /** 关联的订单ID - 虚拟字段，仅在处理后使用 */
    @TableField(exist = false)
    @Schema(description = "关联的订单ID")
    private Long orderId;

    // 关联商品信息
    @TableField(exist = false)
    private products product;

    // 关联买家信息
    @TableField(exist = false)
    private Users buyer;

    // 关联卖家信息
    @TableField(exist = false)
    private Users seller;

    // 用于状态变更通知的字段
    @TableField(exist = false)
    private Boolean statusChanged = false;
}
