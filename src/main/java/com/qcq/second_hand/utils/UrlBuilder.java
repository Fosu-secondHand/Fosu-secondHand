package com.qcq.second_hand.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * URL 构建工具类
 * 统一处理图片、文件等资源的路径转换
 */
@Component
public class UrlBuilder {

    @Value("${app.server.base-url:}")
    private String baseUrl;

    /**
     * 将相对路径转换为完整 URL
     * @param relativePath 相对路径，如 /api/uploads/xxx.png
     * @return 完整 URL，如 https://your-domain.com/api/uploads/xxx.png
     */
    public String buildFullUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return relativePath;
        }

        // 如果已经是完整 URL，直接返回
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }

        // 如果配置了 baseUrl，拼接完整 URL
        if (baseUrl != null && !baseUrl.isEmpty()) {
            // 确保 baseUrl 不以 / 结尾
            String cleanBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            // 确保 relativePath 以 / 开头
            String cleanPath = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
            return cleanBaseUrl + cleanPath;
        }

        // 如果没有配置 baseUrl，返回相对路径
        return relativePath;
    }

    /**
     * 从完整 URL 中提取相对路径
     * @param fullUrl 完整 URL
     * @return 相对路径
     */
    public String extractRelativePath(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) {
            return fullUrl;
        }

        // 如果不是完整 URL，直接返回
        if (!fullUrl.startsWith("http://") && !fullUrl.startsWith("https://")) {
            return fullUrl;
        }

        try {
            // 提取 /uploads/ 或 /covers/ 部分
            int uploadsIndex = fullUrl.indexOf("/uploads/");
            int coversIndex = fullUrl.indexOf("/covers/");

            if (uploadsIndex >= 0) {
                return fullUrl.substring(uploadsIndex);
            } else if (coversIndex >= 0) {
                return fullUrl.substring(coversIndex);
            }

            // 如果都没找到，尝试提取 /api 之后的部分
            int apiIndex = fullUrl.indexOf("/api");
            if (apiIndex >= 0) {
                return fullUrl.substring(apiIndex);
            }

        } catch (Exception e) {
            // 解析失败，返回原值
        }

        return fullUrl;
    }
}
