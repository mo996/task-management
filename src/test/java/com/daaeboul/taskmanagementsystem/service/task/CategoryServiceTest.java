package com.daaeboul.taskmanagementsystem.service.task;


import com.daaeboul.taskmanagementsystem.exceptions.task.category.CategoryNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.task.category.DuplicateCategoryNameException;
import com.daaeboul.taskmanagementsystem.model.task.Category;
import com.daaeboul.taskmanagementsystem.repository.task.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setCategoryName("Software Development");
        category1.setDescription("Tasks related to software development projects.");

        category2 = new Category();
        category2.setCategoryName("Project Management");
        category2.setDescription("Tasks related to managing projects.");

        category3 = new Category();
        category3.setCategoryName("Bug Tracking");
        category3.setDescription("Tasks related to identifying and fixing bugs.");

        ReflectionTestUtils.setField(category1, "id", 1L);
        ReflectionTestUtils.setField(category2, "id", 2L);
        ReflectionTestUtils.setField(category3, "id", 3L);
    }

    @Test
    void createCategory_shouldCreateCategorySuccessfully() {
        given(categoryRepository.existsByCategoryNameIgnoreCase(category1.getCategoryName())).willReturn(false);
        given(categoryRepository.save(category1)).willReturn(category1);

        Category createdCategory = categoryService.createCategory(category1);

        assertThat(createdCategory).isEqualTo(category1);
        verify(categoryRepository).save(category1);
    }

    @Test
    void createCategory_shouldThrowExceptionForDuplicateCategoryName() {
        given(categoryRepository.existsByCategoryNameIgnoreCase(category1.getCategoryName())).willReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(category1))
                .isInstanceOf(DuplicateCategoryNameException.class)
                .hasMessageContaining("Category name already exists");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void findCategoryById_shouldReturnCategoryIfFound() {
        given(categoryRepository.findById(category1.getId())).willReturn(Optional.of(category1));

        Optional<Category> foundCategory = categoryService.findCategoryById(category1.getId());

        assertThat(foundCategory).isPresent().contains(category1);
    }

    @Test
    void findCategoryById_shouldReturnEmptyOptionalIfNotFound() {
        given(categoryRepository.findById(100L)).willReturn(Optional.empty());

        Optional<Category> foundCategory = categoryService.findCategoryById(100L);

        assertThat(foundCategory).isEmpty();
    }

    @Test
    void findCategoryByName_shouldReturnCategoryIfFound() {
        given(categoryRepository.findByCategoryNameIgnoreCase(category1.getCategoryName())).willReturn(Optional.of(category1));

        Optional<Category> foundCategory = categoryService.findCategoryByName(category1.getCategoryName());

        assertThat(foundCategory).isPresent().contains(category1);
    }

    @Test
    void findCategoryByName_shouldReturnEmptyOptionalIfNotFound() {
        given(categoryRepository.findByCategoryNameIgnoreCase("Nonexistent")).willReturn(Optional.empty());

        Optional<Category> foundCategory = categoryService.findCategoryByName("Nonexistent");

        assertThat(foundCategory).isEmpty();
    }

    @Test
    void findAllCategories_shouldReturnAllCategories() {
        given(categoryRepository.findAll()).willReturn(List.of(category1, category2, category3));

        List<Category> allCategories = categoryService.findAllCategories();

        assertThat(allCategories).containsExactly(category1, category2, category3);
    }

    @Test
    void findAllCategories_withPageable_shouldReturnPagedCategories() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Category> pagedCategories = new PageImpl<>(List.of(category1, category2), pageable, 3);
        given(categoryRepository.findAll(pageable)).willReturn(pagedCategories);

        Page<Category> result = categoryService.findAllCategories(pageable);

        assertThat(result).isEqualTo(pagedCategories);
    }

    @Test
    void findCategoriesByNameContaining_shouldReturnMatchingCategories() {
        given(categoryRepository.findByCategoryNameContainingIgnoreCase("manage")).willReturn(List.of(category2));

        List<Category> result = categoryService.findCategoriesByNameContaining("manage");

        assertThat(result).containsExactly(category2);
    }

    @Test
    void findCategoriesByDescriptionContaining_shouldReturnMatchingCategories() {
        given(categoryRepository.findByDescriptionContainingIgnoreCase("project")).willReturn(List.of(category1, category2));

        List<Category> result = categoryService.findCategoriesByDescriptionContaining("project");

        assertThat(result).containsExactly(category1, category2);
    }

    @Test
    void updateCategory_shouldUpdateCategorySuccessfully() {
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Updated Category");
        updatedCategory.setDescription("Updated description");
        ReflectionTestUtils.setField(updatedCategory, "id", category1.getId());

        given(categoryRepository.findById(category1.getId())).willReturn(Optional.of(category1));
        given(categoryRepository.existsByCategoryNameIgnoreCase(updatedCategory.getCategoryName())).willReturn(false); // No duplicate name
        given(categoryRepository.save(any(Category.class))).willReturn(updatedCategory);

        Category result = categoryService.updateCategory(updatedCategory);

        assertThat(result).isEqualTo(updatedCategory);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_shouldThrowExceptionIfCategoryNotFound() {
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Updated Category");
        updatedCategory.setDescription("Updated description");
        ReflectionTestUtils.setField(updatedCategory, "id", 100L);

        given(categoryRepository.findById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(updatedCategory))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_shouldThrowExceptionForDuplicateCategoryName() {
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName(category2.getCategoryName()); // Duplicate name
        updatedCategory.setDescription("Updated description");
        ReflectionTestUtils.setField(updatedCategory, "id", category1.getId());

        given(categoryRepository.findById(category1.getId())).willReturn(Optional.of(category1));
        given(categoryRepository.existsByCategoryNameIgnoreCase(updatedCategory.getCategoryName())).willReturn(true);

        assertThatThrownBy(() -> categoryService.updateCategory(updatedCategory))
                .isInstanceOf(DuplicateCategoryNameException.class)
                .hasMessageContaining("Category name already exists");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_shouldDeleteCategorySuccessfully() {
        given(categoryRepository.existsById(category1.getId())).willReturn(true);

        categoryService.deleteCategory(category1.getId());

        verify(categoryRepository).deleteById(category1.getId());
    }

    @Test
    void deleteCategory_shouldThrowExceptionIfCategoryNotFound() {
        given(categoryRepository.existsById(100L)).willReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory(100L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found");
        verify(categoryRepository, never()).deleteById(any());
    }
}
