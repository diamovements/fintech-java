package org.example.dao;

import org.example.entity.Location;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocationDatabase implements UniversalDatabase<String, Location> {

    private final ConcurrentHashMap<String, Location> db = new ConcurrentHashMap<>();
    @Override
    public void put(String key, Location value) {
        db.put(key, value);
    }

    @Override
    public Location get(String key) {
        return db.get(key);
    }

    @Override
    public void remove(String key) {
        db.remove(key);
    }

    @Override
    public Collection<Location> getAll() {
        return db.values();
    }

    @Override
    public void update(String key, Location value) {
        db.replace(key, value);
    }
}
