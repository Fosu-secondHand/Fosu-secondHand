// src/main/java/com/qcq/second_hand/config/TokenInterceptor.java
package com.qcq.second_hand.config;

import com.qcq.second_hand.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/wechat/login",
            "/user/login",
            "/products/list",
            "/products/search",
            "/products/detail",
            "/products/filter",
            "/wechat/updateUserInfo",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars",
            "/products/upload/image",
            "/uploads",
            "/covers",
            "/error"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullURL = requestURI + (queryString != null ? "?" + queryString : "");

        // 检查是否在排除路径中（使用 contains 匹配）
        for (String excludePath : EXCLUDE_PATHS) {
            if (requestURI.contains(excludePath)) {
                System.out.println("跳过token验证: " + fullURL);
                return true;
            }
        }

        System.out.println("完整请求URL: " + fullURL);

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