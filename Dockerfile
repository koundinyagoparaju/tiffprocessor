FROM openjdk:19-jdk-slim as builder

WORKDIR /build

RUN apt-get update -y && apt-get install -y maven

ADD ./src /build/src

ADD ./pom.xml /build/pom.xml

RUN cd /build && mvn clean install

FROM openjdk:19-jdk-slim as runner

WORKDIR /
COPY --from=builder /build/target/tiffprocessor-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT exec java -jar /app.jar




