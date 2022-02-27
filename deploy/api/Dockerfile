FROM openjdk:11
EXPOSE 8080
EXPOSE 9090
COPY api-1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "-Duser.timezone=Asia/Seoul", "app.jar"]
