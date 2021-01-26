FROM openjdk:8-jdk-alpine
ENV BOT_PK=/data/privatekey.pem
ARG JAR_FILE=build/libs/\*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=docker"]
