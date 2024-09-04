# Use a base image with Maven and JDK pre-installed
FROM openjdk:17-oracle

ARG JARFILE=/target/*.jar
COPY ./target/accommodation-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]