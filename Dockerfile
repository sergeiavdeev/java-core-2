#FROM bellsoft/liberica-openjdk-debian:11.0.12-7 as builder
FROM maven:3.8.4-eclipse-temurin-17 as builder
WORKDIR /app

COPY pom.xml .
COPY chat-server chat-server
COPY chat-client chat-client
COPY chat-commons chat-commons

RUN mvn clean package



FROM openjdk:17
LABEL maintainer="Serg Avdeev"
VOLUME /tmp
WORKDIR /app
#
COPY --from=builder /app/chat-server/target/*dependencies.jar /app/
RUN mkdir /app/log
RUN mkdir /app/config
ENV JAVA_TOOL_OPTIONS=""

EXPOSE 8181
ENTRYPOINT ["sh", "-c", "java  ${JAVA_TOOL_OPTIONS} -jar chat-server-1.0-SNAPSHOT-jar-with-dependencies.jar"]