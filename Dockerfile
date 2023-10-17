FROM openjdk:17-ea-33-jdk-slim-buster

WORKDIR /app
COPY ./target/alex-0.0.39.jar /app

CMD ["java", "-jar", "alex-0.0.39.jar"]
