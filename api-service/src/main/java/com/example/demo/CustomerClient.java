package com.example.demo;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.SpanContext;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerClient {

	private static final Logger logger = LoggerFactory.getLogger(CustomerClient.class);
	private RestTemplate restTemplate;
	private String baseUrl;

	public CustomerClient(
			RestTemplate restTemplate,
			@Value("${customerClient.baseUrl}") String baseUrl) {
		this.restTemplate = restTemplate;
		this.baseUrl = baseUrl;
	}

	@WithSpan(value="getCustomer", kind= SpanKind.CLIENT)
	Customer getCustomer(@PathVariable("id") long id, OpenTelemetry openTelemetry, Context context){
		String url = String.format("%s/customers/%d", baseUrl, id);
		logger.info("invoking customer client api customer/{}", id);

		restTemplate.getInterceptors().add((HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
			HttpHeaders headers = request.getHeaders();
			// Inject the W3C context headers
			openTelemetry.getPropagators().getTextMapPropagator()
					.inject(context, headers, HttpHeaders::set);

			return execution.execute(request, body);
		});

		return restTemplate.getForObject(url, Customer.class);
	}

	Customer getCustomer(@PathVariable("id") long id){
		String url = String.format("%s/customers/%d", baseUrl, id);
		logger.info("invoking customer client api customer/{}", id);

		RestTemplate restTemplate2 = new RestTemplate();
		return restTemplate2.getForObject(url, Customer.class);
	}

}
