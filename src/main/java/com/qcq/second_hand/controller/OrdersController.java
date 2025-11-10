package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.Orders;
import com.qcq.second_hand.entity.Users;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.OrdersService;
import com.qcq.second_hand.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "订单管理", description = "订单相关接口")
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UsersService usersService;

    @Operation(summary = "创建订单", description = "用户购买商品时创建订单，并更新商品状态为已售出")
    @PostMapping("/create")
    public response createOrder(
            @Parameter(description = "商品ID", required = true)
            @RequestParam Long productId,
            @Parameter(description = "买家ID", required = true)
            @RequestParam Long buyerId,
            @Parameter(description = "交付方式", required = true)
            @RequestParam String deliveryMethod,
            @Parameter(description = "买家备注")
            @RequestParam(required = false) String buyerRemark) {

        try {
            // 参数验证
            if (productId == null || buyerId == null || deliveryMethod == null || deliveryMethod.trim().isEmpty()) {
                return new response(400, "参数不能为空", null);
            }

            // 限制交付方式长度
            if (deliveryMethod.length() > 50) {
                return new response(400, "交付方式长度不能超过50个字符", null);
            }

            // 创建订单并更新商品状态
            Orders order = ordersService.createOrder(productId, buyerId, deliveryMethod, buyerRemark);

            if (order == null) {
                return new response(500, "创建订单失败", null);
            }

            // 获取卖家信息
            Users seller = usersService.getUserById(order.getSellerId());

            if (seller == null) {
                return new response(500, "无法获取卖家信息", null);
            }

            // 构造返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("order", order);
            result.put("seller", seller);

            return response.success(result);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = "未知错误";
            }
            System.err.println("创建订单异常: " + e.getClass().getName() + ": " + errorMsg);
            e.printStackTrace();
            return new response(500, "创建订单失败: " + errorMsg, null);
        }
    }


    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详情及卖家信息")
    @GetMapping("/{orderId}")
    public response getOrderDetail(
            @Parameter(description = "订单ID", required = true)
            @PathVariable Long orderId) {

        try {
            Orders order = ordersService.getById(orderId);
            if (order == null) {
                return new response(404, "订单不存在", null);
            }

            // 获取卖家信息
            Users seller = usersService.getUserById(order.getSellerId());

            // 构造返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("order", order);
            result.put("seller", seller);

            return response.success(result);
        } catch (Exception e) {
            return new response(500, "获取订单详情失败: " + e.getMessage(), null);
        }
    }
}
