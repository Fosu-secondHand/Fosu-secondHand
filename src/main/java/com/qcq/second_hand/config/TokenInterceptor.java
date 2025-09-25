// src/main/java/com/qcq/second_hand/config/TokenInterceptor.java
package com.qcq.second_hand.config;

import com.qcq.second_hand.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest; // 注意：Spring Boot 6+ 使用 jakarta
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");

        // 如果没有token，从请求参数中获取
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }

        // 如果仍然没有token，尝试从Bearer Token中获取
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证token
        if (token != null && tokenService.validateToken(token)) {
            // 将用户ID存储到请求属性中，供后续使用
            Long userId = tokenService.getUserIdFromToken(token);
            request.setAttribute("userId", userId);
            request.setAttribute("token", token);
            return true;
        }

        // token无效，返回401未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未授权访问\",\"data\":null}");
        return false;
    }
}
