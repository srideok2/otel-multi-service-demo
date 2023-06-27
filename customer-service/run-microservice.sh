#!/bin/bash

mvn clean package -Dmaven.test.skip=true
AGENT_FILE=opentelemetry-javaagent-all.jar

if [ ! -f "${AGENT_FILE}" ]; then
  curl -L https://github.com/aws-observability/aws-otel-java-instrumentation/releases/download/v1.24.0/aws-opentelemetry-agent.jar --output ${AGENT_FILE}
fi

#export OTEL_TRACES_EXPORTER=logging
#export OTEL_METRICS_EXPORTER=logging
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:5555
export OTEL_RESOURCE_ATTRIBUTES=service.name=customer-service,service.version=1.0


java -javaagent:./${AGENT_FILE} -jar target/customer-service-1.0.0-SNAPSHOT.jar
#java -Dotel.javaagent.enabled=true -Dotel.javaagent.jarPath=./${AGENT_FILE} -Dotel.javaagent.configuration=./otel-config.yaml -jar target/customer-service-1.0.0-SNAPSHOT.jar
#java -jar target/customer-service-1.0.0-SNAPSHOT.jar
