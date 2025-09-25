package com.qcq.second_hand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author YOYO
 * @since 2025-09-25
 */
@Getter
@Setter
@TableName("orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单唯一标识
     */
    @TableId(value = "order_Id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 商品id 关联products表
     */
    @TableField("product_Id")
    private Long productId;

    /**
     * 买家id 关联users表
     */
    @TableField("buyer_Id")
    private Long buyerId;

    /**
     * 卖家id 关联users表
     */
    @TableField("seller_Id")
    private Long sellerId;

    /**
     * 实际成交价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 下单时间
     */
    @TableField("order_Time")
    private LocalDateTime orderTime;

    /**
     * 交付方式
     */
    @TableField("delivery_Method")
    private String deliveryMethod;

    /**
     * 订单状态
     */
    @TableField("status")
    private String status;

    /**
     * 买家备注
     */
    @TableField("buyer_Remark")
    private String buyerRemark;
}
