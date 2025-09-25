package generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        // 数据库配置
        String url = "jdbc:mysql://localhost:3306/second_hand_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "196888mfk";

        // 项目配置
        String outputDir = "D:/Project/Fosu-secondHand/src/main/java";
        String xmlOutputDir = "D:/Project/Fosu-secondHand/src/main/java/com/qcq/second_hand/mapper/xml";
        String parentPackage = "com.qcq.second_hand";

        // 检查并创建目录
        checkAndCreateDirs(outputDir, xmlOutputDir);

        try {
            FastAutoGenerator.create(url, username, password)
                    .globalConfig(builder -> {
                        builder.author("YOYO")
                                .outputDir(outputDir)
                                .disableOpenDir()
                                .commentDate("yyyy-MM-dd");
                    })
                    .packageConfig(builder -> {
                        builder.parent(parentPackage)
                                .entity("entity")
                                .service("service")
                                .serviceImpl("service.impl") // 修正：小写i
                                .mapper("mapper")
                                .controller("controller")
                                .pathInfo(Collections.singletonMap(OutputFile.xml, xmlOutputDir));
                    })
                    .strategyConfig(builder -> {
                        builder.addInclude("orders") // 生成orders表相关代码
                                .addTablePrefix("t_", "sys_")
                                .entityBuilder()
                                .enableLombok()
                                .enableTableFieldAnnotation()
                                .columnNaming(NamingStrategy.underline_to_camel) // 下划线转驼峰
                                .naming(NamingStrategy.underline_to_camel) // 实体类命名规则
                                .controllerBuilder()
                                .enableRestStyle()
                                .serviceBuilder()
                                .formatServiceFileName("%sService")
                                .formatServiceImplFileName("%sServiceImpl")
                                .mapperBuilder()
                                .enableMapperAnnotation(); // 启用@Mapper注解
                    })
                    .templateEngine(new FreemarkerTemplateEngine())
                    .execute();

            System.out.println("✅ 代码生成成功！");
            System.out.println("📁 实体类位置: " + outputDir + "/com/qcq/second_hand/entity/");
            System.out.println("📁 Mapper位置: " + outputDir + "/com/qcq/second_hand/mapper/");
            System.out.println("📁 XML位置: " + xmlOutputDir);

        } catch (Exception e) {
            System.err.println("❌ 代码生成过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 检查并创建目录的方法
    private static void checkAndCreateDirs(String... dirs) {
        for (String dir : dirs) {
            File directory = new File(dir);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("✅ 创建目录: " + dir);
                } else {
                    System.out.println("❌ 无法创建目录: " + dir);
                }
            }
        }
    }
}