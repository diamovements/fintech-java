package org.example.dao;

import org.example.entity.Event;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventDatabase implements UniversalDatabase<Integer, Event> {

    private final ConcurrentHashMap<Integer, Event> db = new ConcurrentHashMap<>();
    @Override
    public void put(Integer key, Event value) {
        db.put(key, value);
    }

    @Override
    public Event get(Integer key) {
        return db.get(key);
    }

    @Override
    public void remove(Integer key) {
        db.remove(key);
    }

    @Override
    public Collection<Event> getAll() {
        return db.values();
    }

    @Override
    public void update(Integer key, Event value) {
        db.replace(key, value);
    }
}
