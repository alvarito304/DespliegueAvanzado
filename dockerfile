FROM gradle:jdk21-alpine AS build

WORKDIR /app

COPY . /app
ARG DOCKER_HOST_ARG=tcp://host.docker.internal:2375
ENV DOCKER_HOST={$DOCKER_HOST_ARG}
RUN ./gradlew build
RUN ./gradlew javadoc


FROM eclipse-temurin:21-jre-alpine AS run
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/my-app.jar
COPY --from=build /app/build/docs /app/doc
COPY --from=build /app/build/jacoco /app/jacoco
COPY --from=build /app/build/reports/tests /app/tests
ENTRYPOINT ["java"]
CMD ["-jar", "/app/my-app.jar"]