package org.example;

import org.example.entity.Category;
import org.example.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void getCategoryByIdTest_shouldReturnCategory() throws Exception {
        Category category = new Category(1, "food", "Еда");
        Mockito.when(categoryService.getCategory(anyInt())).thenReturn(category);

        mockMvc.perform(get("/api/v1/places/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

    @Test
    void getAllCategoriesTest_shouldReturnAllCategories() throws Exception {
        Category category1 = new Category(1, "food", "Еда");
        Category category2 = new Category(2, "inn", "Отели");
        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(new Category[]{category1, category2}));

        mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(category1.getId()))
                .andExpect(jsonPath("$[0].name").value(category1.getName()))
                .andExpect(jsonPath("$[1].id").value(category2.getId()))
                .andExpect(jsonPath("$[1].name").value(category2.getName()));
    }

    @Test
    void addCategoryTest_shouldAddCategory() throws Exception {
        Mockito.doNothing().when(categoryService).addCategory(anyInt(), any(Category.class));

        mockMvc.perform(post("/api/v1/places/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"slug\":\"food\",\"name\":\"Еда\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category has been added successfully"));
    }

    @Test
    void updateCategoryTest_shouldUpdateCategory() throws Exception {
        Mockito.doNothing().when(categoryService).updateCategory(anyInt(), any(Category.class));

        mockMvc.perform(put("/api/v1/places/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"slug\":\"hotels\",\"name\":\"Отели\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category has been updated successfully"));
    }

    @Test
    void deleteCategoryTest_shouldDeleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory(anyInt());

        mockMvc.perform(delete("/api/v1/places/categories/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategoryTest_shouldThrowException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Category not found")).when(categoryService).deleteCategory(anyInt());

        mockMvc.perform(delete("/api/v1/places/categories/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategoryByIdTest_shouldThrowException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Category not found")).when(categoryService).getCategory(anyInt());

        mockMvc.perform(get("/api/v1/places/categories/1"))
                .andExpect(status().isBadRequest());
    }
}
