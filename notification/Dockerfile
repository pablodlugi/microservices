FROM openjdk:17-alpine
ARG JAR_FILE=target/notification.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]