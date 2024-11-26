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
    public ResponseEntity<String> triggerStackOverflow() {
        try {
            causeStackOverflow();
        } catch (StackOverflowError e) {
            log.error("Caught StackOverflowError: {}", e.getMessage());
        }
        return ResponseEntity.ok("Stack overflow error");
    }

    private void causeStackOverflow() {
        causeStackOverflow();
    }

    @GetMapping("/memory")
    public ResponseEntity<String> triggerOutOfMemory() {
        try {
            causeOutOfMemory();
        } catch (OutOfMemoryError e) {
            log.error("Caught OutOfMemoryError: {}", e.getMessage());
        }
        return ResponseEntity.ok("Out of memory error");
    }

    private void causeOutOfMemory() {
        List<Object> list = new ArrayList<>();
        while (true) {
            list.add(new byte[1024 * 1024]);
        }
    }
}

