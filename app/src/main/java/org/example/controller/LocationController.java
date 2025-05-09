package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.timing.Timing;
import org.example.entity.Location;
import org.example.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Timing
@Slf4j
public class LocationController {

    private final LocationService locationService;
    @DeleteMapping("/{slug}")
    public void deleteLocation(@PathVariable("slug") String slug) {
        log.info("Deleting location: {}", slug);
        locationService.deleteLocation(slug);
    }

    @PostMapping()
    public ResponseEntity<String> addLocation(@RequestBody Location location) {
        log.info("Adding location: {}", location.toString());
        locationService.addLocation(location.getSlug(), location);
        return ResponseEntity.ok("Location has been added successfully");
    }

    @PutMapping("/{slug}")
    public ResponseEntity<String> updateLocation(@PathVariable("slug") String slug, @RequestBody Location location) {
        log.info("Updating location: {}", location.toString());
        locationService.updateLocation(slug, location);
        return ResponseEntity.ok("Location has been updated successfully");
    }

    @GetMapping()
    public ResponseEntity<Collection<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{slug}")
    public Location getLocation(@PathVariable("slug") String slug) {
        return locationService.getLocation(slug);
    }
}
