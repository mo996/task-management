package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.repository.BaseSoftDeletableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends BaseSoftDeletableRepository<Project, Long> {

    /**
     * Retrieves a list of all active projects (those that have not been marked as deleted).
     *
     * @return A List of Projects where the 'deletedAt' field is null.
     */
    List<Project> findAllByDeletedAtIsNull();

    /**
     * Retrieves a page of active projects (those that have not been marked as deleted), applying pagination parameters.
     *
     * @param pageable The object containing pagination information (page number, page size, sorting).
     * @return A Page of Projects where the 'deletedAt' field is null.
     */
    Page<Project> findAllByDeletedAtIsNull(Pageable pageable);

    /**
     * Retrieves a list of projects that have been marked as deleted.
     *
     * @return A List of Projects where the 'deletedAt' field is not null.
     */
    List<Project> findAllByDeletedAtIsNotNull();

    /**
     * Retrieves a list of active projects that started before a specified date.
     *
     * @param date The date to compare project start dates against.
     * @return A List of Projects with a 'projectStartDate' earlier than the given date and where 'deletedAt' is null.
     */
    List<Project> findByProjectStartDateBeforeAndDeletedAtIsNull(LocalDateTime date);

    /**
     * Retrieves a list of active projects that end after a specified date.
     *
     * @param date The date to compare project end dates against.
     * @return A List of Projects with a 'projectEndDate' later than the given date and where 'deletedAt' is null.
     */
    List<Project> findByProjectEndDateAfterAndDeletedAtIsNull(LocalDateTime date);

    /**
     * Retrieves a list of active projects that have a start date within a specified date range (inclusive).
     *
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @return A List of Projects with a 'projectStartDate' within the specified range and where 'deletedAt' is null.
     */
    List<Project> findByProjectStartDateBetweenAndDeletedAtIsNull(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves a list of active projects whose names contain the specified text (case-insensitive search).
     *
     * @param projectNamePart The text to search within project names.
     * @return A List of Projects with names matching the search term and where 'deletedAt' is null.
     */
    List<Project> findByProjectNameContainingIgnoreCaseAndDeletedAtIsNull(String projectNamePart);
}
