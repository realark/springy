package com.example.springy;

import dev.braintrust.BraintrustUtils;
import dev.braintrust.trace.BraintrustContext;
import dev.braintrust.trace.BraintrustTracing;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class HelloController {

    private final Tracer tracer;

    public HelloController(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("springy");
    }

    @GetMapping("/hello")
    public String hello() {
        var overrideProjectName = "btx";
        Context featureContext = BraintrustContext.setParentInBaggage(Context.current(), "project_name", overrideProjectName);
        Span span = tracer.spanBuilder("bt-hello-endpoint")
                .setParent(featureContext)
                .startSpan();
        try (var scope = featureContext.with(span).makeCurrent()) {
            setBraintrustParentIfPresent(span, Context.current());
            span.setAttribute(AttributeKey.stringKey("braintrust.name"), "hello-request");
            span.setAttribute(AttributeKey.stringKey("braintrust.input"), "GET /hello");
            span.setAttribute(AttributeKey.stringKey("braintrust.output"), "Hello, World!");
            return "Hello, World!";
        } finally {
            span.end();
        }
    }

    /**
     * If context has a braintrust parent in its baggage, set it as an attribute on span
     */
    private static void setBraintrustParentIfPresent(Span span, Context ctx) {
        Baggage baggage = Baggage.fromContext(ctx);
        String parentValue = baggage.getEntryValue(BraintrustTracing.PARENT_KEY);
        if (parentValue != null) {
            span.setAttribute(BraintrustTracing.PARENT_KEY, parentValue);
        }
    }
}
