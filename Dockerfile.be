FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/*.jar
COPY back/target/tarobot-1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=${PORT:-8080}"]