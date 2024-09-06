# Use a base image with Maven and JDK pre-installed ./accommodation-0.0.1-SNAPSHOT.jar
FROM openjdk:17-oracle

ARG JARFILE=/target/accommodation-0.0.1-SNAPSHOT.jar
COPY ${JARFILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]