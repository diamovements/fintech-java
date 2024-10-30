package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.UniversalDatabase;
import org.example.entity.Location;
import org.example.memento.LocationHistory;
import org.example.memento.LocationSnapshot;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final UniversalDatabase<String, Location> db;
    private final LocationHistory history = new LocationHistory();

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
        history.save(new LocationSnapshot(location.get()));
        db.remove(slug);
    }

    public void updateLocation(String slug, Location location) {
        Optional<Location> existingLocation = Optional.ofNullable(db.get(slug));
        existingLocation.ifPresent(l -> history.save(new LocationSnapshot(l)));
        db.update(slug, location);
    }

    public Location undoLastChange(String slug) {
        LocationSnapshot lastSnapshot = history.lastSnapshot();
        Location restoredLocation = lastSnapshot.restore();
        db.update(slug, restoredLocation);
        return restoredLocation;
    }
}
