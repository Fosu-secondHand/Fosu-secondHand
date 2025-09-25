package com.qcq.second_hand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@MapperScan("com.qcq.second_hand.mapper")// 扫描所有 mapper
public class SecondHandApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecondHandApplication.class, args);
    }

    @Bean
    public CommandLineRunner urlPrinter(Environment env) {
        return args -> {
            String port = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "");

            System.out.println("========================================");
            System.out.println("后端API服务已启动!");
            System.out.println("访问地址: http://localhost:" + port + contextPath);
            System.out.println("商品列表API: http://localhost:" + port + contextPath + "/products/list");
            System.out.println("用户API: http://localhost:" + port + contextPath + "/users");
            System.out.println("========================================");
        };
    }
}
