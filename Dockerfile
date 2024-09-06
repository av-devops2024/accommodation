# Use a base image with Maven and JDK pre-installed ./accommodation-0.0.1-SNAPSHOT.jar
FROM openjdk:17-oracle

ARG JARFILE=/target/accommodation-0.0.1-SNAPSHOT.jar
COPY /target/accommodation-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]