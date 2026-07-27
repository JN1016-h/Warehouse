ARG REGISTRY_MIRROR=m.daocloud.io/docker.io/library
FROM ${REGISTRY_MIRROR}/maven:3.8.8-eclipse-temurin-8 AS build
WORKDIR /app

# China-friendly Maven central mirror (Aliyun)
RUN mkdir -p /root/.m2 && printf '%s\n' \
  '<settings><mirrors><mirror>' \
  '<id>aliyun</id><mirrorOf>*</mirrorOf>' \
  '<url>https://maven.aliyun.com/repository/public</url>' \
  '</mirror></mirrors></settings>' > /root/.m2/settings.xml

COPY pom.xml ./
COPY src ./src

# The repo contains a full node_modules tree under resources which is not needed
# to build/run the backend and makes container layers enormous.
RUN rm -rf src/main/resources/admin/admin/node_modules || true

RUN mvn "-Dmaven.test.skip=true" package

FROM ${REGISTRY_MIRROR}/eclipse-temurin:8-jre
WORKDIR /app

# Apply OS security updates for Trivy base-image CVEs (glibc/util-linux/etc.).
USER root
RUN if command -v apt-get >/dev/null 2>&1; then \
      apt-get update \
      && DEBIAN_FRONTEND=noninteractive apt-get upgrade -y --no-install-recommends \
      && apt-get clean \
      && rm -rf /var/lib/apt/lists/*; \
    elif command -v microdnf >/dev/null 2>&1; then \
      microdnf upgrade -y && microdnf clean all; \
    fi

ENV JAVA_OPTS=""
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
