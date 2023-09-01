kubectl create namespace otel-test
helm install -n otel-test api-service api-service
helm install -n otel-test customer-service customer-service
helm install -n otel-test tempo grafana/tempo --version 0.16.9 -f tempo/values.yaml
helm install -n otel-test grafana grafana/grafana -f grafana/values.yaml --set adminPassword=admin
helm install -n otel-test opentelemetry-collector open-telemetry/opentelemetry-collector --set mode=deployment -f open-telemtry-collector/values.yaml

##grafana --version 6.43.2