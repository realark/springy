package com.example.springy;

import dev.braintrust.Braintrust;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Braintrust braintrust() {
        return Braintrust.get();
    }

    @Bean
    public OpenTelemetry openTelemetry(Braintrust braintrust) {
        return braintrust.openTelemetryCreate();
    }
}
