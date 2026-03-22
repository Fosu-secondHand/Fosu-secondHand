// TradeRequestController.java - 添加WebSocket连接相关接口
package com.qcq.second_hand.controller;

import com.qcq.second_hand.entity.TradeRequest;
import com.qcq.second_hand.response.response;
import com.qcq.second_hand.service.NotificationPushService;
import com.qcq.second_hand.service.TradeRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "交易请求管理", description = "交易请求相关接口")
@RestController
@RequestMapping("/trade-requests")
public class TradeRequestController {

    @Autowired
    private TradeRequestService tradeRequestService;

    @Autowired
    private NotificationPushService notificationService;

    @Operation(summary = "创建交易请求", description = "买方发起交易请求")
    @PostMapping("/create")
    public response createTradeRequest(
            @Parameter(description = "商品ID", required = true)
            @RequestParam Long productId,
            @Parameter(description = "买家ID", required = true)
            @RequestParam Long buyerId,
            @Parameter(description = "交付方式", required = true)
            @RequestParam String deliveryMethod,
            @Parameter(description = "购买数量", required = true)
            @RequestParam Integer quantity,
            @Parameter(description = "买家备注")
            @RequestParam(required = false) String buyerRemark) {
        try {
            TradeRequest tradeRequest = tradeRequestService.createTradeRequest(
                    productId, buyerId, deliveryMethod, quantity, buyerRemark);
            return response.success(tradeRequest);
        } catch (Exception e) {
            return new response(500, "创建交易请求失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取用户收到的交易请求", description = "获取指定用户收到的所有交易请求")
    @GetMapping("/received")
    public response getReceivedTradeRequests(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId) {
        try {
            List<TradeRequest> requests = tradeRequestService.getReceivedTradeRequests(userId);
            return response.success(requests);
        } catch (Exception e) {
            return new response(500, "获取交易请求失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取用户发出的交易请求", description = "获取指定用户发出的所有交易请求")
    @GetMapping("/sent")
    public response getSentTradeRequests(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId) {
        try {
            List<TradeRequest> requests = tradeRequestService.getSentTradeRequests(userId);
            return response.success(requests);
        } catch (Exception e) {
            return new response(500, "获取交易请求失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "处理交易请求", description = "卖方同意或拒绝交易请求")
    @PostMapping("/process")
    public response processTradeRequest(
            @Parameter(description = "请求ID", required = true)
            @RequestParam Long requestId,
            @Parameter(description = "处理结果(ACCEPTED/REJECTED)", required = true)
            @RequestParam String action) {
        try {
            TradeRequest result = tradeRequestService.processTradeRequest(requestId, action);
            return response.success(result);
        } catch (Exception e) {
            return new response(500, "处理交易请求失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取交易请求详情", description = "根据请求ID获取交易请求详情")
    @GetMapping("/detail/{requestId}")
    public response getTradeRequestDetail(
            @Parameter(description = "请求ID", required = true)
            @PathVariable Long requestId) {
        try {
            TradeRequest request = tradeRequestService.getTradeRequestById(requestId);
            return response.success(request);
        } catch (Exception e) {
            return new response(500, "获取交易请求详情失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "标记交易请求为已读", description = "将指定交易请求标记为已读状态")
    @PostMapping("/mark-read/{requestId}")
    public response markAsRead(
            @Parameter(description = "请求ID", required = true)
            @PathVariable Long requestId) {
        try {
            boolean success = tradeRequestService.markAsRead(requestId);
            if (success) {
                return response.success("标记成功");
            } else {
                return new response(500, "标记失败", null);
            }
        } catch (Exception e) {
            return new response(500, "标记已读失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "批量标记交易请求为已读", description = "将多个交易请求标记为已读状态")
    @PostMapping("/mark-read-batch")
    public response markMultipleAsRead(
            @Parameter(description = "请求ID列表", required = true)
            @RequestBody List<Long> requestIds) {
        try {
            boolean success = tradeRequestService.markMultipleAsRead(requestIds);
            if (success) {
                return response.success("批量标记成功");
            } else {
                return new response(500, "批量标记失败", null);
            }
        } catch (Exception e) {
            return new response(500, "批量标记已读失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取未读交易请求数量", description = "获取指定用户的未读交易请求数量")
    @GetMapping("/unread-count")
    public response getUnreadCount(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId) {
        try {
            Integer count = tradeRequestService.getUnreadCount(userId);
            return response.success(count);
        } catch (Exception e) {
            return new response(500, "获取未读数量失败: " + e.getMessage(), null);
        }
    }

    @Operation(summary = "获取买家未读交易请求数量", description = "获取买家因卖家状态变更而产生的未读数量")
    @GetMapping("/unread-count-buyer")
    public response getUnreadCountForBuyer(
            @Parameter(description = "买家ID", required = true)
            @RequestParam Long userId) {
        try {
            Integer count = tradeRequestService.getUnreadCountForBuyer(userId);
            return response.success(count);
        } catch (Exception e) {
            return new response(500, "获取买家未读数量失败: " + e.getMessage(), null);
        }
    }
}
