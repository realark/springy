package com.example.springy;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final Tracer tracer;

    public HelloController(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("springy");
    }

    @GetMapping("/hello")
    public String hello() {
        Span span = tracer.spanBuilder("bt-hello-endpoint").startSpan();
        try {
            span.setAttribute(AttributeKey.stringKey("braintrust.name"), "hello-request");
            span.setAttribute(AttributeKey.stringKey("braintrust.input"), "GET /hello");
            span.setAttribute(AttributeKey.stringKey("braintrust.output"), "Hello, World!");
            return "Hello, World!";
        } finally {
            span.end();
        }
    }
}
