FROM adoptopenjdk/openjdk11:alpine-jre

ARG AGENT_FILE=opentelemetry-javaagent-all.jar
COPY ${AGENT_FILE} opentelemetry-javaagent-all.jar
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://opentelemetry-collector:4317
ENV OTEL_RESOURCE_ATTRIBUTES=service.name=customer-service,service.version=1.0
ENV OTEL_TRACES_SAMPLER="parentbased_always_off"
EXPOSE 8081
ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent-all.jar", "-jar", "/application.jar"]
