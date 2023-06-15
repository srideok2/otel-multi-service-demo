package com.example.demo;

import io.opentelemetry.api.trace.SpanKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.annotations.WithSpan;

@RestController
public class Controller {

  private CustomerClient customerClient;

  private AddressClient addressClient;

  private Logger logger = LoggerFactory.getLogger(Controller.class);

  @Value("otel.traces.api.version")
  private String tracesApiVersion;

  private final Tracer tracer = GlobalOpenTelemetry
          .getTracer("com.example.demo.Controller",
                  tracesApiVersion);

  @Autowired
  public Controller(CustomerClient customerClient, AddressClient addressClient) {
    this.customerClient = customerClient;
    this.addressClient = addressClient;
  }

  @WithSpan(value = "Controller.getCustomerWithAddress")
  @GetMapping(path = "customers/{id}")
  public CustomerAndAddress getCustomerWithAddress(@PathVariable("id") long customerId){

    Span span = Span.current();
    span.setAttribute("customer.id", customerId);

    Customer customer = customerClient.getCustomer(customerId);
    Address address = addressClient.getAddressForCustomerId(customerId);
    return new CustomerAndAddress(customer, address);
  }

}
