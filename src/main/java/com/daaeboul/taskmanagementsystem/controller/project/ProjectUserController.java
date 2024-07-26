package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectUser.ProjectUserNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectUser;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.project.ProjectUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/project-users")
public class ProjectUserController {

    private final ProjectUserService projectUserService;

    @Autowired
    public ProjectUserController(ProjectUserService projectUserService) {
        this.projectUserService = projectUserService;
    }

    @PostMapping
    public ResponseEntity<ProjectUser> createProjectUser(@RequestBody ProjectUser projectUser) {
        ProjectUser createdProjectUser = projectUserService.createProjectUser(projectUser);
        return ResponseEntity.ok(createdProjectUser);
    }

    @GetMapping("/{projectId}/{userId}")
    public ResponseEntity<ProjectUser> findProjectUserById(@PathVariable Long projectId, @PathVariable Long userId) {
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(projectId, userId);
        Optional<ProjectUser> projectUser = projectUserService.findProjectUserById(id);
        return projectUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ProjectUser>> findAllProjectUsers(Pageable pageable) {
        Page<ProjectUser> projectUsers = projectUserService.findAllProjectUsers(pageable);
        return ResponseEntity.ok(projectUsers);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<ProjectUser>> findProjectUsersByProjectId(@PathVariable Long projectId, Pageable pageable) {
        Page<ProjectUser> projectUsers = projectUserService.findProjectUsersByProjectId(projectId, pageable);
        return ResponseEntity.ok(projectUsers);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProjectUser>> findProjectUsersByUserId(@PathVariable Long userId, Pageable pageable) {
        Page<ProjectUser> projectUsers = projectUserService.findProjectUsersByUserId(userId, pageable);
        return ResponseEntity.ok(projectUsers);
    }

    @GetMapping("/project/{projectId}/user/{userId}")
    public ResponseEntity<ProjectUser> findProjectUserByProjectIdAndUserId(@PathVariable Long projectId, @PathVariable Long userId) {
        ProjectUser projectUser = projectUserService.findProjectUserByProjectIdAndUserId(projectId, userId);
        return ResponseEntity.ok(projectUser);
    }

    @GetMapping("/project-name/{projectName}/user/{userId}")
    public ResponseEntity<List<ProjectUser>> findProjectUsersByProjectNameAndUserId(@PathVariable String projectName, @PathVariable Long userId) {
        List<ProjectUser> projectUsers = projectUserService.findProjectUsersByProjectNameAndUserId(projectName, userId);
        return ResponseEntity.ok(projectUsers);
    }

    @GetMapping("/project/{projectId}/role-name/{roleName}")
    public ResponseEntity<List<ProjectUser>> findProjectUsersByProjectIdAndProjectRoleName(@PathVariable Long projectId, @PathVariable String roleName) {
        List<ProjectUser> projectUsers = projectUserService.findProjectUsersByProjectIdAndProjectRoleName(projectId, roleName);
        return ResponseEntity.ok(projectUsers);
    }

    @GetMapping("/project-name/{projectName}/role-name/{roleName}")
    public ResponseEntity<List<ProjectUser>> findProjectUsersByProjectNameAndProjectRoleName(@PathVariable String projectName, @PathVariable String roleName) {
        List<ProjectUser> projectUsers = projectUserService.findProjectUsersByProjectNameAndProjectRoleName(projectName, roleName);
        return ResponseEntity.ok(projectUsers);
    }

    @PutMapping("/{projectId}/{userId}")
    public ResponseEntity<ProjectUser> updateProjectUser(@PathVariable Long projectId, @PathVariable Long userId, @RequestBody ProjectUser projectUserDetails) {
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(projectId, userId);
        projectUserDetails.setId(id);
        try {
            ProjectUser updatedProjectUser = projectUserService.updateProjectUser(projectUserDetails);
            return ResponseEntity.ok(updatedProjectUser);
        } catch (ProjectUserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{projectId}/{userId}")
    public ResponseEntity<Void> deleteProjectUser(@PathVariable Long projectId, @PathVariable Long userId) {
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(projectId, userId);
        try {
            projectUserService.deleteProjectUser(id);
            return ResponseEntity.noContent().build();
        } catch (ProjectUserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users-by-project/{projectId}")
    public ResponseEntity<Page<User>> findUsersByProjectId(@PathVariable Long projectId, Pageable pageable) {
        Page<User> users = projectUserService.findUsersByProjectId(projectId, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/projects-by-user/{userId}")
    public ResponseEntity<Page<Project>> findProjectsByUserId(@PathVariable Long userId, Pageable pageable) {
        Page<Project> projects = projectUserService.findProjectsByUserId(userId, pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/exists/project/{projectId}/user/{userId}")
    public ResponseEntity<Boolean> existsProjectUserByProjectIdAndUserId(@PathVariable Long projectId, @PathVariable Long userId) {
        boolean exists = projectUserService.existsProjectUserByProjectIdAndUserId(projectId, userId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count/project/{projectId}")
    public ResponseEntity<Long> countProjectUsersByProjectId(@PathVariable Long projectId) {
        long count = projectUserService.countProjectUsersByProjectId(projectId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> countProjectUsersByUserId(@PathVariable Long userId) {
        long count = projectUserService.countProjectUsersByUserId(userId);
        return ResponseEntity.ok(count);
    }
}