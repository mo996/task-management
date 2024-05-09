package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.category.CategoryNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.task.category.DuplicateCategoryNameException;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.repository.task.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new category.
     *
     * @param category The category to create.
     * @return The created category.
     * @throws DuplicateCategoryNameException If a category with the same name already exists (case-insensitive).
     */
    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByCategoryNameIgnoreCase(category.getCategoryName())) {
            throw new DuplicateCategoryNameException("Category name already exists: " + category.getCategoryName());
        }
        return categoryRepository.save(category);
    }

    /**
     * Finds a category by its ID.
     *
     * @param id The ID of the category.
     * @return An Optional containing the category if found, otherwise empty.
     */
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Finds a category by its name (case-insensitive).
     *
     * @param categoryName The name of the category to search for.
     * @return An Optional containing the category if found, otherwise empty.
     */
    public Optional<Category> findCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryNameIgnoreCase(categoryName);
    }

    /**
     * Finds all categories.
     *
     * @return A list of all categories.
     */
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Finds all categories with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of category entities.
     */
    public Page<Category> findAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    /**
     * Finds categories whose names contain the given string (case-insensitive).
     *
     * @param namePart The part of the category name to search for.
     * @return A list of category entities that match the given name part.
     */
    public List<Category> findCategoriesByNameContaining(String namePart) {
        return categoryRepository.findByCategoryNameContainingIgnoreCase(namePart);
    }

    /**
     * Finds categories by a part of their description (case-insensitive).
     *
     * @param descriptionPart The part of the description to search for.
     * @return A list of category entities whose descriptions contain the given part.
     */
    public List<Category> findCategoriesByDescriptionContaining(String descriptionPart) {
        return categoryRepository.findByDescriptionContainingIgnoreCase(descriptionPart);
    }

    /**
     * Updates a category.
     *
     * @param updatedCategory The category with updated information.
     * @return The updated category.
     * @throws CategoryNotFoundException If the category is not found.
     * @throws DuplicateCategoryNameException If a category with the same name already exists (case-insensitive).
     */
    @Transactional
    public Category updateCategory(Category updatedCategory) {
        Category existingCategory = categoryRepository.findById(updatedCategory.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + updatedCategory.getId()));

        if (categoryRepository.existsByCategoryNameIgnoreCase(updatedCategory.getCategoryName()) &&
                !existingCategory.getCategoryName().equalsIgnoreCase(updatedCategory.getCategoryName())) {
            throw new DuplicateCategoryNameException("Category name already exists: " + updatedCategory.getCategoryName());
        }

        existingCategory.setCategoryName(updatedCategory.getCategoryName());
        existingCategory.setDescription(updatedCategory.getDescription());

        return categoryRepository.save(existingCategory);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     * @throws CategoryNotFoundException If the category is not found.
     */
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
