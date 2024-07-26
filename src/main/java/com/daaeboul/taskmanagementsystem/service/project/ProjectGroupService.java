package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectGroup.ProjectGroupNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectGroup;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


@Service
public class ProjectGroupService {

    private final ProjectGroupRepository projectGroupRepository;

    @Autowired
    public ProjectGroupService(ProjectGroupRepository projectGroupRepository) {
        this.projectGroupRepository = projectGroupRepository;
    }

    /**
     * Creates a new project-group association.
     *
     * @param projectGroup The ProjectGroup entity to create.
     * @return The saved ProjectGroup entity.
     */
    @Transactional
    public ProjectGroup createProjectGroup(ProjectGroup projectGroup) {
        return projectGroupRepository.save(projectGroup);
    }
    /**
     * Finds a project-group association by its composite ID.
     *
     * @param projectGroupId The composite ID of the project-group association.
     * @return An Optional containing the ProjectGroup entity if found, otherwise empty.
     */
    public Optional<ProjectGroup> findProjectGroupById(ProjectGroup.ProjectGroupId projectGroupId) {
        return projectGroupRepository.findById(projectGroupId);
    }

    /**
     * Finds all project-group associations with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters.
     * @return A Page of ProjectGroup entities.
     */
    public Page<ProjectGroup> findAllProjectGroups(Pageable pageable) {
        return projectGroupRepository.findAll(pageable);
    }

    /**
     * Finds project-group associations by project ID with pagination and sorting.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of ProjectGroup entities matching the criteria.
     */
    public Page<ProjectGroup> findProjectGroupsByProjectId(Long projectId, Pageable pageable) {
        return projectGroupRepository.findByProjectId(projectId, pageable);
    }

    /**
     * Finds project-group associations by group ID with pagination and sorting.
     *
     * @param groupId  The ID of the group.
     * @param pageable Pagination and sorting parameters.
     * @return A Page of ProjectGroup entities matching the criteria.
     */
    public Page<ProjectGroup> findProjectGroupsByGroupId(Long groupId, Pageable pageable) {
        return projectGroupRepository.findByGroupId(groupId, pageable);
    }

    /**
     * Finds project-group associations by project role ID with pagination and sorting.
     *
     * @param projectRoleId The ID of the project role.
     * @param pageable      Pagination and sorting parameters.
     * @return A Page of ProjectGroup entities matching the criteria.
     */
    public Page<ProjectGroup> findProjectGroupsByProjectRoleId(Long projectRoleId, Pageable pageable) {
        return projectGroupRepository.findByProjectRoleId(projectRoleId, pageable);
    }

    /**
     * Finds a project-group association by project and group IDs.
     *
     * @param projectId The ID of the project.
     * @param groupId  The ID of the group.
     * @return The ProjectGroup entity matching the criteria, or null if not found.
     */
    public ProjectGroup findProjectGroupByProjectIdAndGroupId(Long projectId, Long groupId) {
        return projectGroupRepository.findByProjectIdAndGroupId(projectId, groupId);
    }

    /**
     * Finds project-group associations by project name with pagination and sorting.
     *
     * @param projectName The name of the project.
     * @param pageable    Pagination and sorting parameters.
     * @return A Page of ProjectGroup entities matching the criteria.
     */
    public Page<ProjectGroup> findProjectGroupsByProjectName(String projectName, Pageable pageable) {
        return projectGroupRepository.findByProjectProjectName(projectName, pageable);
    }

    /**
     * Finds project-group associations by group name with pagination and sorting.
     *
     * @param groupName The name of the group.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of ProjectGroup entities matching the criteria.
     */
    public Page<ProjectGroup> findProjectGroupsByGroupName(String groupName, Pageable pageable) {
        return projectGroupRepository.findByGroupGroupName(groupName, pageable);
    }

    /**
     * Updates a project-group association.
     *
     * @param updatedProjectGroup The ProjectGroup entity with updated information.
     * @return The updated ProjectGroup entity.
     * @throws ProjectGroupNotFoundException If the project-group association is not found.
     */
    @Transactional
    public ProjectGroup updateProjectGroup(ProjectGroup updatedProjectGroup) {
        ProjectGroup existingProjectGroup = projectGroupRepository.findById(updatedProjectGroup.getId())
                .orElseThrow(() -> new ProjectGroupNotFoundException("Project-Group association not found"));

        existingProjectGroup.setGroup(updatedProjectGroup.getGroup());
        existingProjectGroup.setProjectRole(updatedProjectGroup.getProjectRole());

        return projectGroupRepository.save(existingProjectGroup);
    }

    /**
     * Deletes a project-group association by its composite ID.
     *
     * @param projectGroupId The composite ID of the project-group association to delete.
     * @throws ProjectGroupNotFoundException If the project-group association is not found.
     */
    @Transactional
    public void deleteProjectGroup(ProjectGroup.ProjectGroupId projectGroupId) {
        if (!projectGroupRepository.existsById(projectGroupId)) {
            throw new ProjectGroupNotFoundException("Project-Group association not found");
        }
        projectGroupRepository.deleteById(projectGroupId);
    }

    /**
     * Deletes all project-group associations for a specific project.
     *
     * @param projectId The ID of the project.
     */
    @Transactional
    public void deleteProjectGroupsByProjectId(Long projectId) {
        projectGroupRepository.deleteByProjectId(projectId);
    }

    /**
     * Deletes all project-group associations for a specific group.
     *
     * @param groupId The ID of the group.
     */
    @Transactional
    public void deleteProjectGroupsByGroupId(Long groupId) {
        projectGroupRepository.deleteByGroupId(groupId);
    }

    /**
     * Gets all users associated with a project through project groups with pagination and sorting.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination and sorting parameters.
     * @return A page of User entities associated with the project.
     */
    public Page<User> findUsersByProjectId(Long projectId, Pageable pageable) {
        return projectGroupRepository.findUsersByProjectId(projectId, pageable);
    }

    /**
     * Gets all projects associated with a group with pagination and sorting.
     *
     * @param groupId  The ID of the group.
     * @param pageable Pagination and sorting parameters.
     * @return A page of Project entities associated with the group.
     */
    public Page<Project> findProjectsByGroupId(Long groupId, Pageable pageable) {
        return projectGroupRepository.findProjectsByGroupId(groupId, pageable);
    }

    /**
     * Finds all project-group associations for a specific project and group name.
     *
     * @param projectId   The ID of the project.
     * @param groupName The name of the group.
     * @return A list of ProjectGroup entities matching the criteria.
     */
    public List<ProjectGroup> findProjectGroupsByProjectIdAndGroupName(Long projectId, String groupName) {
        return projectGroupRepository.findByProjectIdAndGroupGroupName(projectId, groupName);
    }

    /**
     * Finds all project groups associated with a specific user and project.
     *
     * @param userId    the ID of the user
     * @param projectId the ID of the project
     * @return a list of ProjectGroup entities
     */
    public List<ProjectGroup> findProjectGroupsByUserAndProject(Long userId, Long projectId) {
        return projectGroupRepository.findProjectGroupsByUserAndProject(userId, projectId);
    }
}