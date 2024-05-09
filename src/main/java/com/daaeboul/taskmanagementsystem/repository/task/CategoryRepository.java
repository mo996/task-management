package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.task.Category;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name (case-insensitive).
     *
     * @param categoryName The name of the category to search for.
     * @return An Optional containing the Category entity if found, otherwise empty.
     */
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    /**
     * Finds categories whose names contain the given string (case-insensitive).
     * This is useful for searching or filtering categories.
     *
     * @param namePart The part of the category name to search for.
     * @return A list of Category entities that match the given name part.
     */
    List<Category> findByCategoryNameContainingIgnoreCase(String namePart);

    /**
     * Finds all categories with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of Category entities.
     */
    @NonNull Page<Category> findAll(@NonNull Pageable pageable);

    /**
     * Finds categories by a part of their description (case-insensitive).
     *
     * @param descriptionPart The part of the description to search for.
     * @return A list of Category entities whose descriptions contain the given part.
     */
    List<Category> findByDescriptionContainingIgnoreCase(String descriptionPart);

    /**
     * Checks if a category exists with the given name (case-insensitive).
     * This is useful for validation during category creation or updating.
     *
     * @param categoryName The name of the category to check.
     * @return True if a category with the given name exists, false otherwise.
     */
    boolean existsByCategoryNameIgnoreCase(String categoryName);

}
