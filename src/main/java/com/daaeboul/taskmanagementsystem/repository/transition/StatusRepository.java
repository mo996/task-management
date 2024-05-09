package com.daaeboul.taskmanagementsystem.repository.transition;

import com.daaeboul.taskmanagementsystem.model.transition.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    /**
     * Retrieves a Status from the database based on its unique status name.
     *
     * @param statusName The name of the Status to search for.
     * @return An Optional containing the Status if found, otherwise an empty Optional.
     */
    Optional<Status> findByStatusName(String statusName);

    /**
     * Retrieves a page of Status entities whose names contain the specified text (case-insensitive search),
     * applying pagination parameters.
     *
     * @param statusName The text to search within Status names.
     * @param pageable The object containing pagination information (page number, page size, sorting).
     * @return A Page of Status entities matching the search term.
     */
    Page<Status> findByStatusNameContainingIgnoreCase(String statusName, Pageable pageable);

    /**
     * Retrieves a list of Status entities whose descriptions contain the specified text (case-insensitive search).
     *
     * @param descriptionPart The text to search within Status descriptions.
     * @return A List of Status entities matching the search term.
     */
    List<Status> findByDescriptionContainingIgnoreCase(String descriptionPart);

    /**
     * Checks if a Status with the specified status name exists in the database.
     *
     * @param statusName The name of the Status to check for existence.
     * @return True if a Status with the given name exists, otherwise false.
     */
    boolean existsByStatusName(String statusName);

    /**
     * Retrieves a list of Status entities that were created before the specified date and time.
     *
     * @param dateTime The date and time to compare Status creation timestamps against.
     * @return A List of Status entities created before the given date and time.
     */
    List<Status> findByCreatedAtBefore(LocalDateTime dateTime);
}
