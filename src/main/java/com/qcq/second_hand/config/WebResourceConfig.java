package com.qcq.second_hand.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件目录 - 使用相对路径（自动适配 Windows/Linux）
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");

        // 映射封面文件目录 - 使用相对路径
        registry.addResourceHandler("/covers/**")
                .addResourceLocations("file:./covers/");
    }
}
