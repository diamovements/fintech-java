package org.example;

import org.example.dao.CategoryDatabase;
import org.example.dao.UniversalDatabase;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;


public class CategoryServiceTest {

    @Test
    void getCategoryByIdTest_shouldReturnCategory() {
        UniversalDatabase<Integer, Category> categoryDbMock = mock(CategoryDatabase.class);
        CategoryService categoryService = new CategoryService(categoryDbMock);
        Category category = new Category(1, "inn", "Отели");

        when(categoryDbMock.get(1)).thenReturn(category);

        assertEquals(category, categoryService.getCategory(1));
    }

    @Test
    void getAllCategoriesTest_shouldReturnAllCategories() {
        UniversalDatabase<Integer, Category> categoryDbMock = mock(CategoryDatabase.class);
        CategoryService categoryService = new CategoryService(categoryDbMock);
        Category category1 = new Category(1, "inn", "Отели");
        Category category2 = new Category(2, "food", "Еда");

        when(categoryDbMock.getAll()).thenReturn(List.of(new Category[]{category1, category2}));

        assertEquals(2, categoryService.getAllCategories().size());
    }

    @Test
    void deleteCategoryTest_shouldDeleteCategory() {
        UniversalDatabase<Integer, Category> categoryDbMock = mock(CategoryDatabase.class);
        CategoryService categoryService = new CategoryService(categoryDbMock);
        Category category1 = new Category(1, "inn", "Отели");
        Category category2 = new Category(2, "food", "Еда");

        when(categoryDbMock.get(1)).thenReturn(category1);
        doNothing().when(categoryDbMock).remove(1);
        when(categoryDbMock.getAll()).thenReturn(Collections.singletonList(category2));
        categoryService.deleteCategory(1);

        assertEquals(1, categoryService.getAllCategories().size());
        verify(categoryDbMock).remove(1);
    }

    @Test
    void updateCategoryTest_shouldUpdateCategory() {
        UniversalDatabase<Integer, Category> categoryDbMock = mock(CategoryDatabase.class);
        CategoryService categoryService = new CategoryService(categoryDbMock);
        Category originalCategory = new Category(1, "inn", "Отели");
        Category updatedCategory = new Category(1, "inn", "Хостелы");

        when(categoryDbMock.get(1)).thenReturn(originalCategory);
        doNothing().when(categoryDbMock).update(1, updatedCategory);
        categoryService.updateCategory(1, updatedCategory);
        when(categoryDbMock.get(1)).thenReturn(updatedCategory);

        assertEquals("Хостелы", categoryService.getCategory(1).getName());
        verify(categoryDbMock).update(1, updatedCategory);
    }

    @Test
    void addCategoryTest_shouldAddCategory() {
        UniversalDatabase<Integer, Category> categoryDbMock = mock(CategoryDatabase.class);
        CategoryService categoryService = new CategoryService(categoryDbMock);
        Category category = new Category(1, "inn", "Отели");

        doNothing().when(categoryDbMock).put(1, category);
        categoryService.addCategory(1, category);

        verify(categoryDbMock).put(1, category);
    }

    @Test
    void getCategoryByIdTest_shouldThrowException() {
        UniversalDatabase<Integer, Category> categoryDbMock = mock(CategoryDatabase.class);
        CategoryService categoryService = new CategoryService(categoryDbMock);

        assertThrows(IllegalArgumentException.class, () -> categoryService.getCategory(133));
    }

    @Test
    void deleteCategoryTest_shouldThrowException() {
        UniversalDatabase<Integer, Category> categoryDbMock = mock(CategoryDatabase.class);
        CategoryService categoryService = new CategoryService(categoryDbMock);

        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(133));
    }
}
