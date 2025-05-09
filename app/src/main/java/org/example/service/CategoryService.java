package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.UniversalDatabase;
import org.example.entity.Category;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UniversalDatabase<Integer, Category> db;

    public Category getCategory(int categoryId) {
        Optional<Category> category = Optional.ofNullable(db.get(categoryId));
        return category.orElseThrow(() -> new IllegalArgumentException(String.valueOf(categoryId)));
    }

    public Collection<Category> getAllCategories() {
        return db.getAll();
    }

    public void deleteCategory(int categoryId) {
        Optional<Category> category = Optional.ofNullable(db.get(categoryId));
        category.orElseThrow(() -> new IllegalArgumentException(String.valueOf(categoryId)));
        db.remove(categoryId);
    }

    public void addCategory(int categoryId, Category category) {
        db.put(categoryId, category);
    }

    public void updateCategory(int categoryId, Category category) {
        db.update(categoryId, category);
    }
}
