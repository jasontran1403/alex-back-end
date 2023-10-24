FROM openjdk:17-ea-33-jdk-slim-buster

WORKDIR /app
COPY ./target/alex-0.0.55.jar /app
COPY src/main/resources/assets /app/src/main/resources/assets

CMD ["java", "-jar", "alex-0.0.55.jar"]
