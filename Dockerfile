FROM maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

# The repo contains a full node_modules tree under resources which is not needed
# to build/run the backend and makes container layers enormous.
RUN rm -rf src/main/resources/admin/admin/node_modules || true

RUN mvn "-Dmaven.test.skip=true" package


FROM eclipse-temurin:8-jre
WORKDIR /app

ENV JAVA_OPTS=""
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
