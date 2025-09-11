package com.qcq.second_hand.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;


//验证连接的用户合法性（如校验 token），防止非法连接
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    // 握手前：验证用户身份
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从请求参数中获取userId和token（前端连接时携带）
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // 获取参数（前端连接时需携带：ws://xxx/ws/message?userId=1&token=xxx）
            String userId = httpRequest.getParameter("userId");
            String token = httpRequest.getParameter("token");

            // 验证token合法性（实际项目中调用你的认证逻辑）
            if (userId != null && validateToken(userId, token)) {
                // 将userId存入属性，供后续处理器使用
                attributes.put("userId", userId);
                return true; // 验证通过，允许握手
            }
        }
        return false; // 验证失败，拒绝连接
    }

    // 验证token的方法（示例）
    private boolean validateToken(String userId, String token) {
        // 实际项目中：调用你的认证服务（如JWT校验）
        return token != null && token.startsWith("valid_"); // 简化判断
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
