FROM openjdk:11-jdk
LABEL maintainer=nils@gis-ops.com

RUN apt-get update -qq && apt-get install -qq -y maven

WORKDIR /pgr_server

COPY src ./src
COPY pom.xml .

RUN mvn dependency:resolve

EXPOSE 8080
CMD ["mvn", "spring-boot:run"]
