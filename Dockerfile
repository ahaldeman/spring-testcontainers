FROM openjdk:11
ADD build/libs/spring-testcontainers.jar spring-testcontainers.jar
ENTRYPOINT ["java", "-jar","spring-testcontainers.jar"]
EXPOSE 8080
