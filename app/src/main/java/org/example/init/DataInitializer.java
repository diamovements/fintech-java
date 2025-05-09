package org.example.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.CategoryDatabase;
import org.example.dao.LocationDatabase;
import org.example.dao.UniversalDatabase;
import org.example.timing.Timing;
import org.example.entity.Category;
import org.example.entity.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UniversalDatabase<String, Location> locationDatabase;
    private final UniversalDatabase<Integer, Category> categoryDatabase;

    @Value("${spring.url.category}")
    private String categoryURL;

    @Value("${spring.url.location}")
    private String locationURL;

    @Bean
    @Timing
    public CommandLineRunner doInit(RestTemplate restTemplate) {
        return args -> {
            log.info("\u001B[34m" + "Starting initialization...");
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
            log.info("\u001B[34m" + "Finished initialization.");
        };
    }
}
