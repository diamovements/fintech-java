package org.example.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.UniversalDatabase;
import org.example.timing.Timing;
import org.example.entity.Category;
import org.example.entity.Location;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class DataInitializer {

    private final UniversalDatabase<String, Location> locationDatabase;
    private final UniversalDatabase<Integer, Category> categoryDatabase;
    private final RestTemplate restTemplate;

    @Value("${spring.url.category}")
    private String categoryURL;

    @Value("${spring.url.location}")
    private String locationURL;

    @Value("${spring.concurrency.duration}")
    private Duration initSchedule;

    private final ExecutorService executorService;

    private final ScheduledExecutorService scheduledExecutorService;

    public DataInitializer(UniversalDatabase<String, Location> locationDatabase, UniversalDatabase<Integer, Category> categoryDatabase, RestTemplate restTemplate, @Qualifier("myFixedThreadPool") ExecutorService executorService, @Qualifier("myScheduledThreadPool") ScheduledExecutorService scheduledExecutorService) {
        this.locationDatabase = locationDatabase;
        this.categoryDatabase = categoryDatabase;
        this.restTemplate = restTemplate;
        this.executorService = executorService;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @EventListener(ApplicationStartedEvent.class)
    @Timing
    public void onStart() {
        log.info("\u001B[34m" + "App has started. Scheduling initialization...");
        scheduledExecutorService.scheduleAtFixedRate(this::doInit, 0, initSchedule.getSeconds(), TimeUnit.SECONDS);
    }

    public void doInit() {
        long startTime = System.nanoTime();
        log.info("\u001B[34m" + "Started initialization...");

        Future<?> futureCategories = executorService.submit(this::fetchCategories);
        Future<?> futureLocations = executorService.submit(this::fetchLocations);

        try {
            futureCategories.get();
            futureLocations.get();
            log.info("\u001B[34m" + "Initialization completed.");

        } catch (Exception e) {
            log.error("Error initializing: {}", e.getMessage());
        }
        long endTime = System.nanoTime();
        log.info("\u001B[34m" + "Initialization took {} ms. ", Duration.ofNanos(endTime - startTime).toMillis());
    }

    private void fetchCategories() {
        try {
            log.info("Fetching categories...");
            Optional<Category[]> categories = Optional.ofNullable(restTemplate.getForObject(categoryURL, Category[].class));
            if (categories.isPresent()) {
                log.info("Fetched categories: {}", Arrays.stream(categories.get()).collect(Collectors.toList()));
                for (Category c : categories.get()) {
                    categoryDatabase.put(c.getId(), c);
                }
            }
            else {
                log.warn("No categories found at URL: {}", categoryURL);
            }
        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage());
        }
        log.info("Done fetching categories.");
    }

    private void fetchLocations() {
        try {
            log.info("Fetching places...");
            Optional<Location[]> locations = Optional.ofNullable(restTemplate.getForObject(locationURL, Location[].class));
            if (locations.isPresent()) {
                log.info("Fetched locations: {}", Arrays.stream(locations.get()).collect(Collectors.toList()));
                for (Location l : locations.get()) {
                    locationDatabase.put(l.getSlug(), l);
                }
            }
            else {
                log.warn("No locations found at URL: {}", locationURL);
            }
        } catch (Exception e) {
            log.error("Error fetching locations: {}", e.getMessage());
        }
        log.info("Done fetching locations.");
    }
}