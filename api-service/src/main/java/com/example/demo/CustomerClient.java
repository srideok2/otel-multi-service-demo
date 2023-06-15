package com.example.demo;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import io.opentelemetry.instrumentation.annotations.WithSpan;

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

	@WithSpan(value = "CustomerClient.getCustomer")
	Customer getCustomer(@PathVariable("id") long id){
		String url = String.format("%s/customers/%d", baseUrl, id);

		Span span = Span.current();
		span.setAttribute("customer.id", id);

		return restTemplate.getForObject(url, Customer.class);
	}

}
