FROM maven:3.9.11-eclipse-temurin-17 AS build

ARG SERVICE_MODULE
WORKDIR /workspace

COPY pom.xml ./
COPY shared shared
COPY services services

RUN mvn -pl "${SERVICE_MODULE}" -am -DskipTests package \
    && cp "${SERVICE_MODULE}/target/$(basename "${SERVICE_MODULE}")-0.1.0.jar" /tmp/app.jar

FROM eclipse-temurin:17-jre

ARG MAIN_CLASS
ENV MAIN_CLASS=${MAIN_CLASS}
COPY --from=build /tmp/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -cp /app/app.jar \"$MAIN_CLASS\""]
