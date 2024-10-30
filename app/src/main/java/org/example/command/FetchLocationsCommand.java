package org.example.command;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Location;
import org.example.init.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class FetchLocationsCommand implements Command{
    private final Publisher<Location> locationPublisher;
    private final RestTemplate restTemplate;
    @Value("${spring.url.location}")
    private final String locationURL;

    public FetchLocationsCommand(RestTemplate restTemplate, String locationURL, Publisher<Location> locationPublisher) {
        this.restTemplate = restTemplate;
        this.locationURL = locationURL;
        this.locationPublisher = locationPublisher;
    }

    @Override
    public void execute() {
        try {
            log.info("Fetching places...");
            Optional<Location[]> locations = Optional.ofNullable(restTemplate.getForObject(locationURL, Location[].class));
            if (locations.isPresent()) {
                log.info("Fetched locations: {}", Arrays.stream(locations.get()).collect(Collectors.toList()));
                for (Location l : locations.get()) {
                    locationPublisher.publish(l);
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
    }
}
