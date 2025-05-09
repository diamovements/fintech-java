package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.example.timing.Timing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places/categories")
@Timing
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable("id") int categoryId) {
        log.info("Deleting category: {}", categoryId);
        categoryService.deleteCategory(categoryId);
    }

    @PostMapping()
    public ResponseEntity<String> addCategory(@RequestBody Category category) {
        log.info("Adding category: {}", category.toString());
        categoryService.addCategory(category.getId(), category);
        return ResponseEntity.ok("Category has been added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable("id") int categoryId, @RequestBody Category category) {
        log.info("Updating category: {}", category.toString());
        categoryService.updateCategory(categoryId, category);
        return ResponseEntity.ok("Category has been updated successfully");
    }

    @GetMapping()
    public ResponseEntity<Collection<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable("id") int categoryId) {
        return categoryService.getCategory(categoryId);
    }
}
