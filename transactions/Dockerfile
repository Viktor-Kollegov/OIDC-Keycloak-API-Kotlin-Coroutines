FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/transactions-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]