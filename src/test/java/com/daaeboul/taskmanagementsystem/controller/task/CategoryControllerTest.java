package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.category.CategoryNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.service.task.CategoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateCategory() throws Exception {
        Category category = new Category();
        category.setCategoryName("Category A");

        Mockito.when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/api/v1/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\": \"Category A\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Category A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindCategoryById() throws Exception {
        Category category = new Category();
        category.setCategoryName("Category A");

        Mockito.when(categoryService.findCategoryById(anyLong())).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Category A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindCategoryById_NotFound() throws Exception {
        Mockito.when(categoryService.findCategoryById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindCategoryByName() throws Exception {
        Category category = new Category();
        category.setCategoryName("Category A");

        Mockito.when(categoryService.findCategoryByName(anyString())).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/v1/categories/name/Category A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Category A"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindCategoryByName_NotFound() throws Exception {
        Mockito.when(categoryService.findCategoryByName(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/categories/name/Category A"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllCategories() throws Exception {
        Category category1 = new Category();
        category1.setCategoryName("Category A");

        Category category2 = new Category();
        category2.setCategoryName("Category B");

        List<Category> categories = Arrays.asList(category1, category2);

        Mockito.when(categoryService.findAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Category A"))
                .andExpect(jsonPath("$[1].categoryName").value("Category B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllCategoriesPaged() throws Exception {
        Category category1 = new Category();
        category1.setCategoryName("Category A");

        Category category2 = new Category();
        category2.setCategoryName("Category B");

        List<Category> categories = Arrays.asList(category1, category2);
        Page<Category> pagedCategories = new PageImpl<>(categories);

        Mockito.when(categoryService.findAllCategories(any(Pageable.class))).thenReturn(pagedCategories);

        mockMvc.perform(get("/api/v1/categories/paged")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].categoryName").value("Category A"))
                .andExpect(jsonPath("$.content[1].categoryName").value("Category B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindCategoriesByNameContaining() throws Exception {
        Category category1 = new Category();
        category1.setCategoryName("Category A");

        Category category2 = new Category();
        category2.setCategoryName("Category B");

        List<Category> categories = Arrays.asList(category1, category2);

        Mockito.when(categoryService.findCategoriesByNameContaining(anyString())).thenReturn(categories);

        mockMvc.perform(get("/api/v1/categories/name-containing/Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Category A"))
                .andExpect(jsonPath("$[1].categoryName").value("Category B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindCategoriesByDescriptionContaining() throws Exception {
        Category category1 = new Category();
        category1.setDescription("Description A");

        Category category2 = new Category();
        category2.setDescription("Description B");

        List<Category> categories = Arrays.asList(category1, category2);

        Mockito.when(categoryService.findCategoriesByDescriptionContaining(anyString())).thenReturn(categories);

        mockMvc.perform(get("/api/v1/categories/description-containing/Description"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Description A"))
                .andExpect(jsonPath("$[1].description").value("Description B"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateCategory() throws Exception {
        Category existingCategory = new Category();
        existingCategory.setCategoryName("Existing Category");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Updated Category");

        Mockito.when(categoryService.findCategoryById(anyLong())).thenReturn(Optional.of(existingCategory));
        Mockito.when(categoryService.updateCategory(any(Category.class))).thenReturn(updatedCategory);

        mockMvc.perform(put("/api/v1/categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\": \"Updated Category\", \"description\": \"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Updated Category"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateCategory_NotFound() throws Exception {
        Mockito.when(categoryService.findCategoryById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\": \"Updated Category\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(delete("/api/v1/categories/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteCategory_NotFound() throws Exception {
        Mockito.doThrow(new CategoryNotFoundException("Category not found")).when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(delete("/api/v1/categories/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}