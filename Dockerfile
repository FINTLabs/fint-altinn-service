FROM gradle:9.8.1-jdk25 AS builder
USER root
COPY . .
RUN gradle --no-daemon build

FROM gcr.io/distroless/java25
ENV JAVA_TOOL_OPTIONS=-XX:+ExitOnOutOfMemoryError
COPY --from=builder /home/gradle/build/libs/fint-altinn-service-*.jar /data/app.jar
CMD ["/data/app.jar"]
