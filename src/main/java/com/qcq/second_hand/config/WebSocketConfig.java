package com.qcq.second_hand.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.qcq.second_hand.config.MessageWebSocketHandler;
import com.qcq.second_hand.config.WebSocketAuthInterceptor;


//创建配置类，开启 WebSocket 支持并注册处理器
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // 注册WebSocket处理器和路径
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 配置消息推送路径，允许跨域（生产环境需限制域名）
        registry.addHandler(messageWebSocketHandler(), "/ws/message")
                .addInterceptors(webSocketAuthInterceptor()) // 身份验证拦截器
                .setAllowedOrigins("*"); // 允许所有域名（生产环境需修改为具体前端域名）
    }

    // 注入自定义WebSocket处理器
    @Bean
    public MessageWebSocketHandler messageWebSocketHandler() {
        return new MessageWebSocketHandler();
    }

    // 注入WebSocket身份验证拦截器（可选，用于验证连接合法性）
    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        return new WebSocketAuthInterceptor();
    }
}
