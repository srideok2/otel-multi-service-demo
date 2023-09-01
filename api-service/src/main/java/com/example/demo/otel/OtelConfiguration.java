package com.example.demo.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static io.opentelemetry.semconv.resource.attributes.ResourceAttributes.*;

public final class OtelConfiguration {

    private static Logger logger = LoggerFactory.getLogger(OtelConfiguration.class);

    /**
     * Initialize OpenTelemetry.
     *
     * @return a ready-to-use {@link OpenTelemetry} instance.
     */

    public static OpenTelemetry initializeOpenTelemetry() {
        logger.info("Initializing OpenTelemetry SDK for manual instrumentation...");

        // Create and configure the OpenTelemetry SDK
        OtlpGrpcSpanExporter otlpGrpcSpanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://opentelemetry-collector:5555")
                .build();

        // Include required service.name resource attribute on all spans and metrics
        Resource resource = Resource.getDefault()
                .merge(Resource.builder()
                        .put(SERVICE_NAME, "api-service")
                        .put(SERVICE_VERSION, "1.0")
                        .put(HOST_NAME, System.getenv("HOSTNAME"))
                        .build());

        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(otlpGrpcSpanExporter).build())
                .setResource(resource)
                .build();

        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                //.setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                // B3 headers - Single compact header injection
                .setPropagators(ContextPropagators.create(B3Propagator.injectingSingleHeader()))
                // B3 headers - multiple header injection - separate headers for traceid and span id
                .setPropagators(ContextPropagators.create(B3Propagator.injectingMultiHeaders()))
                .buildAndRegisterGlobal();

        return openTelemetry;
    }
}
