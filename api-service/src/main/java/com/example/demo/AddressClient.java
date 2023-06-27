package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    Address getAddressForCustomerId(long id) {
        logger.info("invoking address client api addresses/{}", id);
        return restTemplate.getForObject(String.format("%s/addresses/%d", baseUrl, id), Address.class);
    }

}
