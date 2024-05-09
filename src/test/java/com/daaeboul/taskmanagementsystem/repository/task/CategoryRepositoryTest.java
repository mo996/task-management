package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.task.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setCategoryName("Software Development");
        category.setDescription("Tasks related to software development projects.");
        entityManager.persist(category);

        category1 = new Category();
        category1.setCategoryName("Software Development2");
        category1.setDescription("Tasks related to software development projects.");

        category2 = new Category();
        category2.setCategoryName("Project Management");
        category2.setDescription("Tasks related to managing projects.");

        category3 = new Category();
        category3.setCategoryName("Bug Tracking");
        category3.setDescription("Tasks related to identifying and fixing bugs.");

        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.persist(category3);

        entityManager.flush();
    }

    @Test
    public void testCreateCategory() {
        Category newCategory = new Category();
        newCategory.setCategoryName("Project Management2");
        newCategory.setDescription("Tasks related to managing projects.");

        Category savedCategory = categoryRepository.save(newCategory);

        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getCategoryName()).isEqualTo("Project Management2");
    }

    @Test
    public void whenFindById_thenReturnCategory() {
        Category foundCategory = categoryRepository.findById(category.getId()).orElse(null);

        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getCategoryName()).isEqualTo("Software Development");
    }

    @Test
    public void whenUpdateCategory_thenUpdated() {
        category.setCategoryName("Updated Category Name");
        categoryRepository.save(category);

        Category updatedCategory = entityManager.find(Category.class, category.getId());
        assertThat(updatedCategory.getCategoryName()).isEqualTo("Updated Category Name");
    }

    @Test
    public void whenDeleteCategory_thenShouldBeRemoved() {
        Category foundCategory = categoryRepository.findById(category.getId()).orElse(null);
        assertThat(foundCategory).isNotNull();

        categoryRepository.delete(foundCategory);

        entityManager.flush();

        Category deletedCategory = categoryRepository.findById(category.getId()).orElse(null);
        assertThat(deletedCategory).isNull();
    }

    @Test
    public void testFindByCategoryNameIgnoreCase() {
        Optional<Category> foundCategory = categoryRepository.findByCategoryNameIgnoreCase("software development");

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getCategoryName()).isEqualTo("Software Development");
    }

    @Test
    public void testFindByCategoryNameContainingIgnoreCase() {
        List<Category> foundCategories = categoryRepository.findByCategoryNameContainingIgnoreCase("manage");

        assertThat(foundCategories).hasSize(1);
        assertThat(foundCategories.get(0).getCategoryName()).isEqualTo("Project Management");
    }

    @Test
    public void testFindAllWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        assertThat(categoryPage.getContent()).hasSize(2);
    }

    @Test
    public void testFindByDescriptionContainingIgnoreCase() {
        List<Category> foundCategories = categoryRepository.findByDescriptionContainingIgnoreCase("project");

        assertThat(foundCategories).hasSize(3);
    }

    @Test
    public void testExistsByCategoryNameIgnoreCase() {
        boolean exists = categoryRepository.existsByCategoryNameIgnoreCase("software development");
        assertThat(exists).isTrue();
    }

}
