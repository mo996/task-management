package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectGroup;
import com.daaeboul.taskmanagementsystem.model.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectGroupRepository extends JpaRepository<ProjectGroup, ProjectGroup.ProjectGroupId> {


    /**
     * Finds all project-group associations for a specific project with pagination and sorting.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination and sorting parameters.
     * @return A page of ProjectGroup entities matching the criteria.
     */
    Page<ProjectGroup> findByProjectId(Long projectId, Pageable pageable);

    /**
     * Finds all project-group associations for a specific group with pagination and sorting.
     *
     * @param groupId  The ID of the group.
     * @param pageable Pagination and sorting parameters.
     * @return A page of ProjectGroup entities matching the criteria.
     */
    Page<ProjectGroup> findByGroupId(Long groupId, Pageable pageable);

    /**
     * Finds all project-group associations with a specific project role with pagination and sorting.
     *
     * @param projectRoleId The ID of the project role.
     * @param pageable      Pagination and sorting parameters.
     * @return A page of ProjectGroup entities matching the criteria.
     */
    Page<ProjectGroup> findByProjectRoleId(Long projectRoleId, Pageable pageable);

    /**
     * Finds a specific project-group association by project and group IDs.
     *
     * @param projectId The ID of the project.
     * @param groupId  The ID of the group.
     * @return The ProjectGroup entity matching the criteria, or null if not found.
     */
    ProjectGroup findByProjectIdAndGroupId(Long projectId, Long groupId);

    /**
     * Finds project-group associations by project name with pagination and sorting.
     *
     * @param projectName The name of the project.
     * @param pageable    Pagination and sorting parameters.
     * @return A page of ProjectGroup entities matching the criteria.
     */
    Page<ProjectGroup> findByProjectProjectName(String projectName, Pageable pageable);

    /**
     * Finds project-group associations by group name with pagination and sorting.
     *
     * @param groupName The name of the group.
     * @param pageable  Pagination and sorting parameters.
     * @return A page of ProjectGroup entities matching the criteria.
     */
    Page<ProjectGroup> findByGroupGroupName(String groupName, Pageable pageable);

    /**
     * Deletes all project-group associations for a specific project.
     *
     * @param projectId The ID of the project.
     */
    @Modifying
    @Transactional
    void deleteByProjectId(Long projectId);

    /**
     * Deletes all project-group associations for a specific group.
     *
     * @param groupId The ID of the group.
     */
    @Modifying
    @Transactional
    void deleteByGroupId(Long groupId);

    /**
     * Gets all users associated with a project through project groups with pagination and sorting.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination and sorting parameters.
     * @return A page of User entities associated with the project.
     */
    @Query("SELECT u FROM ProjectGroup pg JOIN pg.group g JOIN g.users u JOIN pg.project p WHERE p.id = :projectId")
    Page<User> findUsersByProjectId(Long projectId, Pageable pageable);

    /**
     * Gets all projects associated with a group with pagination and sorting.
     *
     * @param groupId  The ID of the group.
     * @param pageable Pagination and sorting parameters.
     * @return A page of Project entities associated with the group.
     */
    @Query("SELECT p FROM ProjectGroup pg JOIN pg.project p WHERE pg.group.id = :groupId")
    Page<Project> findProjectsByGroupId(Long groupId, Pageable pageable);


    /**
     * Finds all project-group associations for a specific project and group name.
     *
     * @param projectId   The ID of the project.
     * @param groupName The name of the group.
     * @return A list of ProjectGroup entities matching the criteria.
     */
    List<ProjectGroup> findByProjectIdAndGroupGroupName(Long projectId, String groupName);


    /**
     * Finds all project groups associated with a specific user and project.
     *
     * @param userId    the ID of the user
     * @param projectId the ID of the project
     * @return a list of ProjectGroup entities
     */
    @Query("SELECT pg FROM ProjectGroup pg JOIN pg.project p JOIN pg.group g JOIN g.users u WHERE u.id = :userId AND p.id = :projectId")
    List<ProjectGroup> findProjectGroupsByUserAndProject(Long userId, Long projectId);

}
