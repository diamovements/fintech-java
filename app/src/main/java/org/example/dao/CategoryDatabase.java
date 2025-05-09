package org.example.dao;

import org.example.entity.Category;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CategoryDatabase implements UniversalDatabase<Integer, Category> {

    private final ConcurrentHashMap<Integer, Category> db = new ConcurrentHashMap<>();
    @Override
    public void put(Integer key, Category value) {
        db.put(key, value);
    }

    @Override
    public Category get(Integer key) {
        return db.get(key);
    }

    @Override
    public void remove(Integer key) {
        db.remove(key);
    }

    @Override
    public Collection<Category> getAll() {
        return db.values();
    }

    @Override
    public void update(Integer key, Category value) {
        db.replace(key, value);
    }
}
