# For Java 8, try this
FROM openjdk:8-jdk-alpine

ARG JAR_FILE=target/football-league.jar

# cd /opt/app
WORKDIR /opt/app

COPY ${JAR_FILE} football-league.jar
EXPOSE 8085

ENTRYPOINT ["java","-jar","football-league.jar"]

