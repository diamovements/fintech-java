package org.example;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api")
@Slf4j
public class MainController {
    private final Counter counter;

    public MainController(MeterRegistry meterRegistry) {
        this.counter = meterRegistry.counter("custom_requests_total", "type", "custom_metric");
    }

    @GetMapping("/hello")
    public void customMetric() {
        String requestId = UUID.randomUUID().toString();
        try (var ignore = MDC.putCloseable("requestId", requestId + " My request")) {
            log.info("Structure log");
        }
        counter.increment();
    }

    @GetMapping("/overflow")
    public void triggerStackOverflow() {
        triggerStackOverflow();
    }

    @GetMapping("/memory")
    public void triggerOutOfMemory() {
        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new byte[1024 * 1024]);
        }
    }
}

