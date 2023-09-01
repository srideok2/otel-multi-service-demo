helm uninstall api-service -n otel-test
helm uninstall customer-service -n otel-test
helm uninstall tempo -n otel-test
helm uninstall grafana -n otel-test
helm uninstall opentelemetry-collector -n otel-test