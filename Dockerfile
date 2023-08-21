#javabuild11
FROM maven:3.6.1-jdk-11-slim AS javabuild
COPY . .
RUN mvn clean
RUN mvn -e -B -DskipTests package
#gcsfuse
FROM golang:1.17.8-bullseye AS gcsfuse
ENV GOPATH /go
RUN go install github.com/googlecloudplatform/gcsfuse@v0.40.0

# main
FROM tomcat:8.5.63-jdk8-openjdk-slim-buster

COPY --from=gcsfuse /go/bin/gcsfuse /usr/local/bin
RUN apt-get -y update && apt-get -y install ca-certificates fuse

RUN apt-get update && apt-get install -y fontconfig

ENV CATALINA_OUT="/dev/stdout"

ENV TZ=Asia/Kolkata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

COPY --from=javabuild target/*.jar /app/hariyali.jar

ENTRYPOINT ["java", "-jar", "/app/hariyali.jar"]