package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectUser;
import com.daaeboul.taskmanagementsystem.model.project.ProjectUser.ProjectUserId;
import com.daaeboul.taskmanagementsystem.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {


    /**
     * Finds a ProjectUser by its composite primary key, consisting of project ID and user ID.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user.
     * @return The ProjectUser entity if found, otherwise null.
     */
    ProjectUser findByIdProjectIdAndIdUserId(Long projectId, Long userId);

    /**
     * Finds ProjectUsers associated with a specific project, using pagination and sorting.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of ProjectUser entities matching the criteria.
     */
    Page<ProjectUser> findByIdProjectId(Long projectId, Pageable pageable);

    /**
     * Finds ProjectUsers associated with a specific user, using pagination and sorting.
     *
     * @param userId    The ID of the user.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of ProjectUser entities matching the criteria.
     */
    Page<ProjectUser> findByIdUserId(Long userId, Pageable pageable);

    /**
     * Finds ProjectUsers by project name and user ID.
     *
     * @param projectName The name of the project.
     * @param userId      The ID of the user.
     * @return A List of ProjectUser entities matching the criteria.
     */
    @Query("SELECT pu FROM ProjectUser pu JOIN pu.project p WHERE p.projectName = :projectName AND pu.id.userId = :userId")
    List<ProjectUser> findByProjectNameAndUserId(@Param("projectName") String projectName, @Param("userId") Long userId);

    /**
     * Finds ProjectUsers by project ID and project role name.
     *
     * @param projectId   The ID of the project.
     * @param roleName The name of the project role.
     * @return A List of ProjectUser entities matching the criteria.
     */
    @Query("SELECT pu FROM ProjectUser pu JOIN pu.projectRole pr WHERE pu.id.projectId = :projectId AND pr.roleName = :roleName")
    List<ProjectUser> findByProjectIdAndProjectRoleName(@Param("projectId") Long projectId, @Param("roleName") String roleName);

    /**
     * Finds ProjectUsers by project name and project role name.
     *
     * @param projectName The name of the project.
     * @param roleName    The name of the project role.
     * @return A List of ProjectUser entities matching the criteria.
     */
    @Query("SELECT pu FROM ProjectUser pu JOIN pu.project p JOIN pu.projectRole pr WHERE p.projectName = :projectName AND pr.roleName = :roleName")
    List<ProjectUser> findByProjectNameAndProjectRoleName(@Param("projectName") String projectName, @Param("roleName") String roleName);

    /**
     * Retrieves a Page of User entities associated with a specific project.
     *
     * @param projectId The ID of the project.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of User entities associated with the project.
     */
    @Query("SELECT pu.user FROM ProjectUser pu WHERE pu.id.projectId = :projectId")
    Page<User> findUsersByProjectId(@Param("projectId") Long projectId, Pageable pageable);

    /**
     * Retrieves a Page of Project entities associated with a specific user.
     *
     * @param userId    The ID of the user.
     * @param pageable  Pagination and sorting parameters.
     * @return A Page of Project entities associated with the user.
     */
    @Query("SELECT pu.project FROM ProjectUser pu WHERE pu.id.userId = :userId")
    Page<Project> findProjectsByUserId(@Param("userId") Long userId, Pageable pageable);


    /**
     * Checks if a specific project-user association exists based on project ID and user ID.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user.
     * @return True if the association exists, false otherwise.
     */
    boolean existsByIdProjectIdAndIdUserId(Long projectId, Long userId);

    /**
     * Counts the number of ProjectUser associations for a given project ID.
     *
     * @param projectId The ID of the project.
     * @return The count of ProjectUser entities associated with the project.
     */
    long countByIdProjectId(Long projectId);

    /**
     * Counts the number of ProjectUser associations for a given user ID.
     *
     * @param userId The ID of the user.
     * @return The count of ProjectUser entities associated with the user.
     */
    long countByIdUserId(Long userId);
}
