package org.example.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.command.Command;
import org.example.command.FetchCategoriesCommand;
import org.example.command.FetchLocationsCommand;
import org.example.dao.CategoryDatabase;
import org.example.dao.LocationDatabase;
import org.example.timing.Timing;
import org.example.entity.Category;
import org.example.entity.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final LocationDatabase locationDatabase;
    private final CategoryDatabase categoryDatabase;
    private final Publisher<Category> categoryPublisher = new PublisherImpl<>();
    private final Publisher<Location> locationPublisher = new PublisherImpl<>();
    @Value("${spring.url.category}")
    private String categoryURL;

    @Value("${spring.url.location}")
    private String locationURL;

    @Bean
    @Timing
    public CommandLineRunner doInit(RestTemplate restTemplate) {
        categoryPublisher.subscribe(categoryDatabase);
        locationPublisher.subscribe(locationDatabase);

        return args -> {
            log.info("\u001B[34m" + "Starting initialization...");
            Command categoriesCommand = new FetchCategoriesCommand(restTemplate, categoryURL, categoryPublisher);
            categoriesCommand.execute();

            Command locationsCommand = new FetchLocationsCommand(restTemplate, locationURL, locationPublisher);
            locationsCommand.execute();


            log.info("\u001B[34m" + "Finished initialization.");
        };
    }
}
