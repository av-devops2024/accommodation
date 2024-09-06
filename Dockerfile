# Use a base image with Maven and JDK pre-installed
FROM openjdk:17-oracle
WORKDIR /target
ARG JARFILE=/target/*.jar
COPY ./accommodation-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]