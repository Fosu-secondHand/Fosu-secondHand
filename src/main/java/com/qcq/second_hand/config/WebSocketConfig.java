// WebSocketConfig.java - 整合后的配置
package com.qcq.second_hand.config;

import com.qcq.second_hand.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker  // 启用STOMP消息代理
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    @Autowired
    private TokenService tokenService;

    // 原有的 WebSocket 处理器注册
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageWebSocketHandler(), "/ws/message")
                .addInterceptors(webSocketAuthInterceptor())
                .setAllowedOrigins("*");
    }

    // STOMP 端点注册
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")  // 与原有端点保持一致
                .addInterceptors(webSocketAuthInterceptor())  // 添加认证拦截器
                .withSockJS();
    }

    // 配置消息代理
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/user");  // 启用简单消息代理
        registry.setApplicationDestinationPrefixes("/app");  // 设置应用目的地前缀
        registry.setUserDestinationPrefix("/user");  // 设置用户目的地前缀
    }

    @Bean
    public MessageWebSocketHandler messageWebSocketHandler() {
        return new MessageWebSocketHandler();
    }

    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        WebSocketAuthInterceptor interceptor = new WebSocketAuthInterceptor();
        interceptor.setTokenService(tokenService);
        return interceptor;
    }
}
