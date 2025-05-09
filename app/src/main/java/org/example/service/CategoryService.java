package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.History;
import org.example.dao.UniversalDatabase;
import org.example.entity.Category;
import org.example.memento.CategorySnapshot;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UniversalDatabase<Integer, Category> db;
    private final History<CategorySnapshot> history = new History<>();


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
        history.save(new CategorySnapshot(category.get()));
        db.remove(categoryId);
    }

    public void addCategory(int categoryId, Category category) {
        db.put(categoryId, category);
    }

    public void updateCategory(int categoryId, Category category) {
        Optional<Category> existingCategory = Optional.ofNullable(db.get(categoryId));
        existingCategory.ifPresent(c -> history.save(new CategorySnapshot(c)));
        db.update(categoryId, category);
    }

    public Category undoLastChange(int categoryId) {
        CategorySnapshot lastSnapshot = history.lastSnapshot().orElseThrow(() -> new IllegalArgumentException("No changes to undo"));
        Category restoredCategory = lastSnapshot.restore();
        db.update(categoryId, restoredCategory);
        return restoredCategory;
    }
}
