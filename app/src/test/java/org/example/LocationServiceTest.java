package org.example;

import org.example.dao.LocationDatabase;
import org.example.dao.UniversalDatabase;
import org.example.entity.Category;
import org.example.entity.Location;
import org.example.service.LocationService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocationServiceTest {
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        UniversalDatabase<String, Location> locationDB = new LocationDatabase();
        locationService = new LocationService(locationDB);
    }

    @Test
    void getLocationById_shouldReturnLocation() {
        Location location = new Location("msk", "Москва");
        locationService.addLocation(location.getSlug(), location);
        assertEquals(location, locationService.getLocation("msk"));
    }

    @Test
    void getAllLocations_shouldReturnAllLocations() {
        Location location1 = new Location("msk", "Москва");
        Location location2 = new Location("ekb", "Екатеринбург");
        locationService.addLocation(location1.getSlug(), location1);
        locationService.addLocation(location2.getSlug(), location2);
        assertEquals(2, locationService.getAllLocations().size());
    }

    @Test
    void deleteLocation_shouldDeleteLocation() {
        Location location1 = new Location("msk", "Москва");
        Location location2 = new Location("ekb", "Екатеринбург");
        locationService.addLocation(location1.getSlug(), location1);
        locationService.addLocation(location2.getSlug(), location2);
        locationService.deleteLocation("ekb");
        assertEquals(1, locationService.getAllLocations().size());
    }

    @Test
    void updateLocation_shouldUpdateLocation() {
        Location location = new Location("msk", "Москва");
        locationService.addLocation(location.getSlug(), location);
        locationService.updateLocation("msk", new Location("msk", "Московская область"));
        assertEquals("Московская область", locationService.getLocation("msk").getName());
    }

    @Test
    void getLocationById_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> locationService.getLocation("ast"));
    }

    @Test
    void deleteLocation_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> locationService.deleteLocation("ast"));
    }

    @Test
    void undoLastChange_shouldUndoLastChange() {
        Location location = new Location("msk", "Москва");
        locationService.addLocation(location.getSlug(), location);
        locationService.updateLocation("msk", new Location("msk", "Московская область"));
        assertEquals("Москва", locationService.undoLastChange("msk").getName());
    }

    @Test
    void undoLastChange_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> locationService.undoLastChange("ekb"));
    }
}
