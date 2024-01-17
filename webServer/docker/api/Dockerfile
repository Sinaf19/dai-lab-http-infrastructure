FROM openjdk:latest
LABEL authors="quentinsurdez"

COPY /target/api-1.0-SNAPSHOT-jar-with-dependencies.jar /api_app.jar

EXPOSE 9292
ENTRYPOINT ["java", "-jar", "/api_app.jar"]