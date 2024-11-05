package org.example;

import org.example.dao.CategoryDatabase;
import org.example.dao.UniversalDatabase;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class CategoryServiceTest {

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        UniversalDatabase<Integer, Category> categoryDB = new CategoryDatabase();
        categoryService = new CategoryService(categoryDB);
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        Category category = new Category(1, "food", "Еда");
        categoryService.addCategory(category.getId(), category);
        assertEquals(category, categoryService.getCategory(1));
    }

    @Test
    void getAllCategories_shouldReturnAllCategories() {
        Category category1 = new Category(1, "food", "Еда");
        Category category2 = new Category(2, "inn", "Отели");
        categoryService.addCategory(category1.getId(), category1);
        categoryService.addCategory(category2.getId(), category2);
        assertEquals(2, categoryService.getAllCategories().size());
    }

    @Test
    void deleteCategory_shouldRemoveCategory() {
        Category category1 = new Category(1, "food", "Еда");
        Category category2 = new Category(2, "inn", "Отели");
        categoryService.addCategory(category1.getId(), category1);
        categoryService.addCategory(category2.getId(), category2);
        categoryService.deleteCategory(1);
        assertEquals(1, categoryService.getAllCategories().size());
    }

    @Test
    void updateCategory_shouldUpdateCategory() {
        Category category1 = new Category(1, "food", "Еда");
        categoryService.addCategory(category1.getId(), category1);
        categoryService.updateCategory(1, new Category(1, "inn", "Отели"));
        assertEquals("Отели", categoryService.getCategory(1).getName());
    }

    @Test
    void getCategoryById_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> categoryService.getCategory(133));
    }

    @Test
    void deleteCategory_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(133));
    }

    @Test
    void undoLastChange_shouldUndoLastChange() {
        Category category1 = new Category(1, "food", "Еда");
        categoryService.addCategory(category1.getId(), category1);
        categoryService.updateCategory(1, new Category(1, "inn", "Отели"));
        assertEquals("Еда", categoryService.undoLastChange(1).getName());
    }

    @Test
    void undoLastChange_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> categoryService.undoLastChange(133));
    }
}
