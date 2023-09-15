package com.example.demo;

import com.example.demo.otel.OtelConfiguration;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.util.Arrays;
import java.util.List;

@RestController
public class Controller {

  private CustomerClient customerClient;

  private AddressClient addressClient;

  private Logger logger = LoggerFactory.getLogger(Controller.class);

  private List<Integer> customerIdList = Arrays.asList(1, 4);

  private OpenTelemetry openTelemetry = OtelConfiguration.initializeOpenTelemetry();

  private Tracer tracer = openTelemetry.getTracer("api-service", "1.0");

  @Autowired
  public Controller(CustomerClient customerClient, AddressClient addressClient) {
    this.customerClient = customerClient;
    this.addressClient = addressClient;
  }

  @GetMapping(path = "customers/{id}")
  public CustomerAndAddress getCustomerWithAddress(@PathVariable("id") long customerId){

    logger.info("customers/{} API invoked..", customerId);
    boolean traceEnabled = false;

    for (Integer id : customerIdList) {
      if (id == customerId)
        traceEnabled = true;
    }

    if(traceEnabled)
    {
      Span span = tracer.spanBuilder("getCustomerWithAddress").startSpan();
      span.setAttribute("customer.id", customerId);
      span.setAttribute(SemanticAttributes.HTTP_METHOD, "GET");

      try (Scope scope = span.makeCurrent()){
        Customer customer = customerClient.getCustomer(customerId, openTelemetry, Context.current());
        Address address = addressClient.getAddressForCustomerId(customerId, openTelemetry, Context.current());
        return new CustomerAndAddress(customer, address);
      } finally {
        span.end();
      }
    } else {
      Customer customer = customerClient.getCustomer(customerId);
      Address address = addressClient.getAddressForCustomerId(customerId);
      return new CustomerAndAddress(customer, address);
    }
  }
}
