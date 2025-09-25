package com.qcq.second_hand.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("二手交易平台 API 文档")
                        .version("1.0")
                        .description("为前端提供的完整 API 接口文档")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com")));
    }
}
