FROM openjdk:15-ea-21-oracle
ENV BOT_PK=/data/privatekey.pem
ARG JAR_FILE=build/libs/\*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=docker"]
