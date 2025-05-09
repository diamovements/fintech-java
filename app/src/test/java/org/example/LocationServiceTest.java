package org.example;

import org.example.dao.LocationDatabase;
import org.example.dao.UniversalDatabase;
import org.example.entity.Location;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import org.example.service.LocationService;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

public class LocationServiceTest {

    @Test
    void getLocationByIdTest_shouldReturnLocation() {
        UniversalDatabase<String, Location> locationDbMock = mock(LocationDatabase.class);
        LocationService locationService = new LocationService(locationDbMock);
        Location location = new Location("msk", "Москва");

        when(locationDbMock.get("msk")).thenReturn(location);

        assertEquals(location, locationService.getLocation("msk"));
    }

    @Test
    void getAllLocationsTest_shouldReturnAllLocations() {
        UniversalDatabase<String, Location> locationDbMock = mock(LocationDatabase.class);
        LocationService locationService = new LocationService(locationDbMock);
        Location location1 = new Location("msk", "Москва");
        Location location2 = new Location("ekb", "Екатеринбург");

        when(locationDbMock.getAll()).thenReturn(List.of(new Location[]{location1, location2}));

        assertEquals(2, locationService.getAllLocations().size());
    }

    @Test
    void deleteLocationTest_shouldDeleteLocation() {
        UniversalDatabase<String, Location> locationDbMock = mock(LocationDatabase.class);
        LocationService locationService = new LocationService(locationDbMock);
        Location location1 = new Location("msk", "Москва");
        Location location2 = new Location("ekb", "Екатеринбург");

        when(locationDbMock.get("msk")).thenReturn(location1);
        doNothing().when(locationDbMock).remove("msk");
        when(locationDbMock.getAll()).thenReturn(Collections.singletonList(location2));
        locationService.deleteLocation("msk");

        assertEquals(1, locationService.getAllLocations().size());
        verify(locationDbMock).remove("msk");
    }

    @Test
    void updateLocationTest_shouldUpdateLocation() {
        UniversalDatabase<String, Location> locationDbMock = mock(LocationDatabase.class);
        LocationService locationService = new LocationService(locationDbMock);
        Location originalLocation = new Location("msk", "Москва");
        Location updatedLocation = new Location("msk", "Московская область");

        when(locationDbMock.get("msk")).thenReturn(originalLocation);
        doNothing().when(locationDbMock).update("msk", updatedLocation);
        locationService.updateLocation("msk", updatedLocation);
        when(locationDbMock.get("msk")).thenReturn(updatedLocation);

        assertEquals("Московская область", locationService.getLocation("msk").getName());
        verify(locationDbMock).update("msk", updatedLocation);
    }

    @Test
    void addLocationTest_shouldAddLocation() {
        UniversalDatabase<String, Location> locationDbMock = mock(LocationDatabase.class);
        LocationService locationService = new LocationService(locationDbMock);
        Location location = new Location("msk", "Москва");

        doNothing().when(locationDbMock).put("msk", location);
        locationService.addLocation("msk", location);

        verify(locationDbMock).put("msk", location);
    }

    @Test
    void getLocationByIdTest_shouldThrowException() {
        UniversalDatabase<String, Location> locationDbMock = mock(LocationDatabase.class);
        LocationService locationService = new LocationService(locationDbMock);

        assertThrows(IllegalArgumentException.class, () -> locationService.getLocation("ast"));
    }

    @Test
    void deleteLocationTest_shouldThrowException() {
        UniversalDatabase<String, Location> locationDbMock = mock(LocationDatabase.class);
        LocationService locationService = new LocationService(locationDbMock);

        assertThrows(IllegalArgumentException.class, () -> locationService.deleteLocation("ast"));
    }
}
