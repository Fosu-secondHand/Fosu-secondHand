// 修改 WebSocketAuthInterceptor.java
package com.qcq.second_hand.config;

import com.qcq.second_hand.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

// 验证连接的用户合法性（如校验 token），防止非法连接
// 验证连接的用户合法性（如校验 token），防止非法连接
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private TokenService tokenService;

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // 握手前：验证用户身份
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("WebSocket握手开始...");

        // 从请求参数中获取userId和token（前端连接时携带）
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // 获取参数（前端连接时需携带：ws://xxx/ws/message?userId=1&token=xxx）
            String userId = httpRequest.getParameter("userId");
            String token = httpRequest.getParameter("token");

            System.out.println("收到连接请求 - userId: " + userId + ", token: " + token);

            // 验证token合法性（使用真实的token验证服务）
            if (userId != null && validateToken(userId, token)) {
                System.out.println("WebSocket握手成功 - 用户ID: " + userId);
                // 将userId存入属性，供后续处理器使用
                attributes.put("userId", userId);
                return true; // 验证通过，允许握手
            } else {
                System.out.println("WebSocket握手失败 - userId: " + userId + ", token有效: " + (token != null));
            }
        }
        System.out.println("WebSocket握手被拒绝");
        return false; // 验证失败，拒绝连接
    }

    // 验证token的方法（使用真实的验证逻辑）
    private boolean validateToken(String userId, String token) {
        System.out.println("开始验证token - userId: " + userId + ", tokenService为空: " + (tokenService == null));

        // 实际项目中：调用TokenService进行验证
        if (token == null || userId == null) {
            System.out.println("token或userId为空");
            return false;
        }

        try {
            // 使用TokenService验证token
            if (tokenService != null && tokenService.validateToken(token)) {
                // 验证userId是否匹配token中的用户信息
                Long tokenUserId = tokenService.getUserIdFromToken(token);
                boolean userIdMatch = tokenUserId != null && tokenUserId.toString().equals(userId);
                System.out.println("Token验证结果 - token有效: true, userId匹配: " + userIdMatch);
                return userIdMatch;
            } else {
                System.out.println("Token验证失败 - tokenService: " + (tokenService != null) + ", token有效: " + (tokenService != null && tokenService.validateToken(token)));
            }
        } catch (Exception e) {
            System.out.println("Token验证异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            System.out.println("WebSocket握手后异常: " + exception.getMessage());
        }
    }
}
