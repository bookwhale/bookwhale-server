FROM openjdk:11
EXPOSE 9090
COPY chat-1.0.jar app-chat.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "-Duser.timezone=Asia/Seoul", "app-chat.jar"]
