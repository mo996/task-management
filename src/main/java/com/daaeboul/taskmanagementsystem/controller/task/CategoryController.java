package com.daaeboul.taskmanagementsystem.controller.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.category.CategoryNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.service.task.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> findCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.findCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{categoryName}")
    public ResponseEntity<Category> findCategoryByName(@PathVariable String categoryName) {
        Optional<Category> category = categoryService.findCategoryByName(categoryName);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Category>> findAllCategories() {
        List<Category> categories = categoryService.findAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Category>> findAllCategories(Pageable pageable) {
        Page<Category> categories = categoryService.findAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/name-containing/{namePart}")
    public ResponseEntity<List<Category>> findCategoriesByNameContaining(@PathVariable String namePart) {
        List<Category> categories = categoryService.findCategoriesByNameContaining(namePart);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/description-containing/{descriptionPart}")
    public ResponseEntity<List<Category>> findCategoriesByDescriptionContaining(@PathVariable String descriptionPart) {
        List<Category> categories = categoryService.findCategoriesByDescriptionContaining(descriptionPart);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Optional<Category> optionalCategory = categoryService.findCategoryById(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category existingCategory = optionalCategory.get();
        existingCategory.setCategoryName(categoryDetails.getCategoryName());
        existingCategory.setDescription(categoryDetails.getDescription());

        Category updatedCategory = categoryService.updateCategory(existingCategory);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}