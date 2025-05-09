package org.example.command;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Category;
import org.example.init.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
public class FetchCategoriesCommand implements Command{
    private final RestTemplate restTemplate;
    private final Publisher<Category> categoryPublisher;

    @Value("${spring.url.category}")
    private final String categoryURL;

    public FetchCategoriesCommand(RestTemplate restTemplate, String categoryURL, Publisher<Category> categoryPublisher) {
        this.restTemplate = restTemplate;
        this.categoryURL = categoryURL;
        this.categoryPublisher = categoryPublisher;
    }

    @Override
    public void execute() {
        try {
            log.info("Fetching categories...");
            Optional<Category[]> categories = Optional.ofNullable(restTemplate.getForObject(categoryURL, Category[].class));
            if (categories.isPresent()) {
                log.info("Fetched categories: {}", Arrays.stream(categories.get()).collect(Collectors.toList()));
                for (Category c : categories.get()) {
                    categoryPublisher.publish(c);
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
}
