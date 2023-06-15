package com.example.demo;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import io.opentelemetry.instrumentation.annotations.WithSpan;

@Component
public class AddressClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AddressClient(
            RestTemplate restTemplate,
            @Value("${addressClient.baseUrl}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @WithSpan(value = "AddressClient.getAddressForCustomerId")
    Address getAddressForCustomerId(long id) {

        Span span = Span.current();
        span.setAttribute("customer.id", id);

        return restTemplate.getForObject(String.format("%s/addresses/%d", baseUrl, id), Address.class);
    }

}
