package org.example.memento;

import lombok.Getter;
import org.example.entity.Category;

import java.time.LocalDateTime;

@Getter
public class CategorySnapshot {
    private final int id;
    private final String name;
    private final LocalDateTime timestamp;

    public CategorySnapshot(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.timestamp = LocalDateTime.now();
    }

    public Category restore() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        return category;
    }

}
