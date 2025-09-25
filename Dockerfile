# 使用适合 Java 应用的基础镜像
FROM openjdk:11-jre-slim

# 在容器内设置工作目录
WORKDIR /app

# 将 Maven 打包后的 jar 文件复制到容器中
# 假设你的项目打包后生成的文件名为 second-hand-0.0.1-SNAPSHOT.jar
COPY target/second-hand-0.0.1-SNAPSHOT.jar app.jar

# 声明容器运行时监听的端口，应该与 application.yml 中的 server.port 一致
EXPOSE 8090

# 定义容器启动后执行的命令
ENTRYPOINT ["java", "-jar", "app.jar"]
