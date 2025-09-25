// src/main/java/com/qcq/second_hand/config/WebMvcConfig.java
package com.qcq.second_hand.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加token拦截器，排除公开接口
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/wechat/login",
                        "/user/login",
                        "/products/list",
                        "/products/search",
                        "/products/detail",
                        "/wechat/updateUserInfo"
                );
    }
}
