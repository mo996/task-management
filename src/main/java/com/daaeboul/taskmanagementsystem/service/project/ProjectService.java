package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.project.DuplicateProjectNameException;
import com.daaeboul.taskmanagementsystem.exceptions.project.project.ProjectNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.project.project.ProjectValidationException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Creates a new Project entity. Performs validation (likely related to the uniqueness of the project name) before saving the entity to the database.
     *
     * @param project The Project object containing the data for the new project.
     * @return The newly created and saved Project object.
     * @throws RuntimeException (Or a more specific exception) if validation fails.
     */
    @Transactional
    public Project createProject(Project project) {
        validateProject(project);
        return projectRepository.save(project);
    }

    /**
     * Retrieves a Project from the database by its ID.
     *
     * @param projectId The ID of the Project to retrieve.
     * @return An Optional containing the Project if found, otherwise an empty Optional.
     */
    public Optional<Project> findProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    /**
     * Retrieves a list of all active Projects (those not marked as deleted).  This method likely relies on a soft-delete mechanism.
     *
     * @return A List of Project entities which are not marked as deleted.
     */
    public List<Project> findAllProjects() {
        return projectRepository.findAllByDeletedAtIsNull();
    }

    /**
     * Retrieves a page of active Projects (those not marked as deleted), applying pagination parameters. This method likely relies on a soft-delete mechanism.
     *
     * @param pageable The object containing pagination information (page number, page size, sorting).
     * @return A Page of active Project entities.
     */
    public Page<Project> findAllProjects(Pageable pageable) {
        return projectRepository.findAllByDeletedAtIsNull(pageable);
    }

    /**
     * Retrieves a list of all Projects that have been marked as deleted.  This method likely relies on a soft-delete mechanism.
     *
     * @return A List of Project entities which are marked as deleted.
     */
    public List<Project> findAllDeletedProjects() {
        return projectRepository.findAllByDeletedAtIsNotNull();
    }

    /**
     * Retrieves a list of active Projects (those not marked as deleted) with a start date before the specified date.
     * This method likely relies on a soft-delete mechanism.
     *
     * @param date The date to compare project start dates against.
     * @return A List of Projects with a 'projectStartDate' earlier than the given date and which are not marked as deleted.
     */
    public List<Project> findProjectsByStartDateBefore(LocalDateTime date) {
        return projectRepository.findByProjectStartDateBeforeAndDeletedAtIsNull(date);
    }

    /**
     * Retrieves a list of active Projects (those not marked as deleted) with an end date after the specified date.  This method likely relies on a soft-delete mechanism.
     *
     * @param date The date to compare project end dates against.
     * @return A List of Projects with a 'projectEndDate' later than the given date and which are not marked as deleted.
     */
    public List<Project> findProjectsByEndDateAfter(LocalDateTime date) {
        return projectRepository.findByProjectEndDateAfterAndDeletedAtIsNull(date);
    }

    /**
     * Retrieves a list of active Projects (those not marked as deleted) with a start date within the specified date range (inclusive). This method likely relies on a soft-delete mechanism.
     *
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @return A List of Projects with a 'projectStartDate' within the specified range and which are not marked as deleted.
     */
    public List<Project> findProjectsByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return projectRepository.findByProjectStartDateBetweenAndDeletedAtIsNull(startDate, endDate);
    }

    /**
     * Retrieves a list of active Projects (those not marked as deleted) whose names contain the specified text (case-insensitivetive search). This method likely relies on a soft-delete mechanism.
     *
     * @param projectName The text to search within project names.
     * @return A List of Projects containing the specified text in their names and which are not marked as deleted.
     */
    public List<Project> findProjectsByNameContaining(String projectName) {
        return projectRepository.findByProjectNameContainingIgnoreCaseAndDeletedAtIsNull(projectName);
    }

    /**
     * Updates an existing Project with new data.
     *
     * @param updatedProject The Project object containing the updated information.
     * @return The updated and saved Project.
     * @throws ProjectNotFoundException if a Project with the provided ID doesn't exist.
     * @throws ProjectValidationException if basic validation fails (e.g., start date being after end date).
     * @throws DuplicateProjectNameException if a Project with the same name (case-insensitive) already exists and it's not this project itself.
     */
    @Transactional
    public Project updateProject(Project updatedProject) {
        Project existingProject = projectRepository.findById(updatedProject.getId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id " + updatedProject.getId()));

        existingProject.setProjectName(updatedProject.getProjectName());
        existingProject.setProjectDescription(updatedProject.getProjectDescription());
        existingProject.setProjectStartDate(updatedProject.getProjectStartDate());
        existingProject.setProjectEndDate(updatedProject.getProjectEndDate());

        validateProject(existingProject);

        return projectRepository.save(existingProject);
    }

    /**
     * Deletes a Project. It's likely that this performs a "soft delete" by setting a `deletedAt` timestamp.
     *
     * @param projectId The ID of the Project to delete.
     * @throws ProjectNotFoundException if a Project with the provided ID doesn't exist.
     */
    @Transactional
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }

    /**
     * Permanently deletes a Project from the database.  Use with caution as this cannot be undone.
     *
     * @param projectId  The ID of the Project to permanently delete.
     * @throws ProjectNotFoundException if a Project with the provided ID doesn't exist.
     */
    @Transactional
    public void hardDeleteProject(Long projectId) {
        projectRepository.hardDeleteById(projectId);
    }

    /**
     * Helper method used for validating specific aspects of Project entities. Includes a check for valid start/end dates and ensures project name uniqueness (case-insensitive).
     *
     * @param project The Project object to validate.
     * @throws ProjectValidationException if the start date is after the end date.
     * @throws DuplicateProjectNameException if a Project with the given name already exists (excluding the current project being updated).
     */
    private void validateProject(Project project) {
        if (project.getProjectStartDate().isAfter(project.getProjectEndDate())) {
            throw new ProjectValidationException("Project start date must be before end date.");
        }

        projectRepository.findByProjectNameContainingIgnoreCaseAndDeletedAtIsNull(project.getProjectName())
                .stream()
                .filter(existingProject -> !existingProject.getId().equals(project.getId()))
                .findAny()
                .ifPresent(existingProject -> {
                    throw new DuplicateProjectNameException("Project name already exists.");
                });
    }
}
