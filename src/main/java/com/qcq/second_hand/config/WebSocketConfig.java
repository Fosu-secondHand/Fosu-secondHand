package com.qcq.second_hand.config;

import com.qcq.second_hand.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.qcq.second_hand.config.MessageWebSocketHandler;
import com.qcq.second_hand.config.WebSocketAuthInterceptor;


//创建配置类，开启 WebSocket 支持并注册处理器
// 修改 WebSocketConfig.java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private TokenService tokenService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageWebSocketHandler(), "/ws/message")
                .addInterceptors(webSocketAuthInterceptor())
                .setAllowedOrigins("*");
    }

    @Bean
    public MessageWebSocketHandler messageWebSocketHandler() {
        return new MessageWebSocketHandler();
    }

    @Bean
    public WebSocketAuthInterceptor webSocketAuthInterceptor() {
        WebSocketAuthInterceptor interceptor = new WebSocketAuthInterceptor();
        interceptor.setTokenService(tokenService);  // 确保设置TokenService
        return interceptor;
    }
}


