package com.qcq.second_hand.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Value("${app.cover.permanent-path}")
    private String permanentCoverDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 封面路径映射：/covers/** → 封面文件夹
        registry.addResourceHandler("/covers/**")
                .addResourceLocations("file:" + permanentCoverDir)
                .setCachePeriod(3600); // 缓存 1 小时（优化播放体验，减少重复请求）
    }
}