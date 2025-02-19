FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY target/blog-server-1.0-SNAPSHOT.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]