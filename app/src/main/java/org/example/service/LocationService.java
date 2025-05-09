package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.UniversalDatabase;
import org.example.entity.Location;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final UniversalDatabase<String, Location> db;

    public Location getLocation(String slug) {
        Optional<Location> location = Optional.ofNullable(db.get(slug));
        return location.orElseThrow(() -> new IllegalArgumentException("Location with slug: " + slug + " doesn't exist"));
    }

    public Collection<Location> getAllLocations() {
        return db.getAll();
    }

    public void addLocation(String slug, Location location) {
        db.put(slug, location);
    }

    public void deleteLocation(String slug) {
        Optional<Location> location = Optional.ofNullable(db.get(slug));
        location.orElseThrow(() -> new IllegalArgumentException("Location with slug: " + slug + " doesn't exist"));
        db.remove(slug);
    }

    public void updateLocation(String slug, Location location) {
        db.update(slug, location);
    }
}
