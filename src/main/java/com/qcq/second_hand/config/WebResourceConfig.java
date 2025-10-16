package com.qcq.second_hand.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.path:D:/Project/Fosu-secondHand/uploads}")
    private String uploadPath;

    @Value("${app.cover.permanent-path}")
    private String permanentCoverDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 现有的封面路径映射
        if (permanentCoverDir != null && !permanentCoverDir.isEmpty()) {
            String normalizedCoverDir = Paths.get(permanentCoverDir).normalize().toString();
            registry.addResourceHandler("/covers/**")
                    .addResourceLocations("file:" + normalizedCoverDir + "/");
        }

        // 添加上传文件路径映射
        if (uploadPath != null && !uploadPath.isEmpty()) {
            String normalizedUploadPath = Paths.get(uploadPath).normalize().toString();
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + normalizedUploadPath + "/");
        }
    }
}
