package com.example.demo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AddressClient {

    private static final Logger logger = LoggerFactory.getLogger(CustomerClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AddressClient(
            RestTemplate restTemplate,
            @Value("${addressClient.baseUrl}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @WithSpan(value="getAddressForCustomerId", kind= SpanKind.CLIENT)
    Address getAddressForCustomerId(long id, OpenTelemetry openTelemetry, Context context) {
        logger.info("invoking address client api addresses/{}", id);

        restTemplate.getInterceptors().add((HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            HttpHeaders headers = request.getHeaders();
            // Inject the W3C context headers
            openTelemetry.getPropagators().getTextMapPropagator()
                    .inject(context, headers, HttpHeaders::set);

            return execution.execute(request, body);
        });

        return restTemplate.getForObject(String.format("%s/addresses/%d", baseUrl, id), Address.class);
    }

    Address getAddressForCustomerId(long id) {
        logger.info("invoking address client api addresses/{}", id);
        RestTemplate restTemplate2 = new RestTemplate();
        return restTemplate2.getForObject(String.format("%s/addresses/%d", baseUrl, id), Address.class);
    }

}
