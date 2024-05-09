package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectUser.ProjectUserNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectUser;
import com.daaeboul.taskmanagementsystem.model.project.ProjectUser.ProjectUserId;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectUserService {

    private final ProjectUserRepository projectUserRepository;

    @Autowired
    public ProjectUserService(ProjectUserRepository projectUserRepository) {
        this.projectUserRepository = projectUserRepository;
    }

    /**
     * Creates a new project-user association.
     *
     * @param projectUser The ProjectUser entity to create.
     * @return The saved ProjectUser entity.
     */
    @Transactional
    public ProjectUser createProjectUser(ProjectUser projectUser) {
        return projectUserRepository.save(projectUser);
    }

    /**
     * Finds a project-user association by its composite ID.
     *
     * @param projectUserId The composite ID of the project-user association.
     * @return An Optional containing the ProjectUser entity if found, otherwise empty.
     */
    public Optional<ProjectUser> findProjectUserById(ProjectUserId projectUserId) {
        return projectUserRepository.findById(projectUserId);
    }

    /**
     * Finds all project-user associations with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of ProjectUser entities.
     */
    public Page<ProjectUser> findAllProjectUsers(Pageable pageable) {
        return projectUserRepository.findAll(pageable);
    }

    /**
     * Finds project-user associations by project ID with pagination and sorting.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of ProjectUser entities matching the criteria.
     */
    public Page<ProjectUser> findProjectUsersByProjectId(Long projectId, Pageable pageable) {
        return projectUserRepository.findByIdProjectId(projectId, pageable);
    }

    /**
     * Finds project-user associations by user ID with pagination and sorting.
     *
     * @param userId  The ID of the user.
     * @param pageable Pagination and sorting parameters.
     * @return A Page of ProjectUser entities matching the criteria.
     */
    public Page<ProjectUser> findProjectUsersByUserId(Long userId, Pageable pageable) {
        return projectUserRepository.findByIdUserId(userId, pageable);
    }

    /**
     * Finds a project-user association by project ID and user ID.
     *
     * @param projectId The ID of the project.
     * @param userId  The ID of the user.
     * @return The ProjectUser entity matching the criteria, or null if not found.
     */
    public ProjectUser findProjectUserByProjectIdAndUserId(Long projectId, Long userId) {
        return projectUserRepository.findByIdProjectIdAndIdUserId(projectId, userId);
    }

    /**
     * Finds project-user associations by project name and user ID.
     *
     * @param projectName The name of the project.
     * @param userId      The ID of the user.
     * @return A list of ProjectUser entities matching the criteria.
     */
    public List<ProjectUser> findProjectUsersByProjectNameAndUserId(String projectName, Long userId) {
        return projectUserRepository.findByProjectNameAndUserId(projectName, userId);
    }

    /**
     * Finds project-user associations by project ID and project role name.
     *
     * @param projectId   The ID of the project.
     * @param roleName The name of the project role.
     * @return A list of ProjectUser entities matching the criteria.
     */
    public List<ProjectUser> findProjectUsersByProjectIdAndProjectRoleName(Long projectId, String roleName) {
        return projectUserRepository.findByProjectIdAndProjectRoleName(projectId, roleName);
    }

    /**
     * Finds project-user associations by project name and project role name.
     *
     * @param projectName The name of the project.
     * @param roleName    The name of the project role.
     * @return A list of ProjectUser entities matching the criteria.
     */
    public List<ProjectUser> findProjectUsersByProjectNameAndProjectRoleName(String projectName, String roleName) {
        return projectUserRepository.findByProjectNameAndProjectRoleName(projectName, roleName);
    }

    /**
     * Updates a project-user association.
     *
     * @param updatedProjectUser The ProjectUser entity with updated information.
     * @return The updated ProjectUser entity.
     * @throws ProjectUserNotFoundException If the project-user association is not found.
     */
    @Transactional
    public ProjectUser updateProjectUser(ProjectUser updatedProjectUser) {
        ProjectUser existingProjectUser = projectUserRepository.findById(updatedProjectUser.getId())
                .orElseThrow(() -> new ProjectUserNotFoundException("Project-User association not found"));

        existingProjectUser.setProjectRole(updatedProjectUser.getProjectRole());

        return projectUserRepository.save(existingProjectUser);
    }

    /**
     * Deletes a project-user association by its composite ID.
     *
     * @param projectUserId The composite ID of the project-user association to delete.
     * @throws ProjectUserNotFoundException If the project-user association is not found.
     */
    @Transactional
    public void deleteProjectUser(ProjectUserId projectUserId) {
        if (!projectUserRepository.existsById(projectUserId)) {
            throw new ProjectUserNotFoundException("Project-User association not found");
        }
        projectUserRepository.deleteById(projectUserId);
    }

    /**
     * Gets all users associated with a project through project-user associations with pagination and sorting.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination and sorting parameters.
     * @return A page of User entities associated with the project.
     */
    public Page<User> findUsersByProjectId(Long projectId, Pageable pageable) {
        return projectUserRepository.findUsersByProjectId(projectId, pageable);
    }

    /**
     * Gets all projects associated with a user with pagination and sorting.
     *
     * @param userId  The ID of the user.
     * @param pageable Pagination and sorting parameters.
     * @return A page of Project entities associated with the user.
     */
    public Page<Project> findProjectsByUserId(Long userId, Pageable pageable) {
        return projectUserRepository.findProjectsByUserId(userId, pageable);
    }

    /**
     * Checks if a specific project-user association exists based on project ID and user ID.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user.
     * @return True if the association exists, false otherwise.
     */
    public boolean existsProjectUserByProjectIdAndUserId(Long projectId, Long userId) {
        return projectUserRepository.existsByIdProjectIdAndIdUserId(projectId, userId);
    }

    /**
     * Counts the number of ProjectUser associations for a given project ID.
     *
     * @param projectId The ID of the project.
     * @return The count of ProjectUser entities associated with the project.
     */
    public long countProjectUsersByProjectId(Long projectId) {
        return projectUserRepository.countByIdProjectId(projectId);
    }

    /**
     * Counts the number of ProjectUser associations for a given user ID.
     *
     * @param userId The ID of the user.
     * @return The count of ProjectUser entities associated with the user.
     */
    public long countProjectUsersByUserId(Long userId) {
        return projectUserRepository.countByIdUserId(userId);
    }
}
