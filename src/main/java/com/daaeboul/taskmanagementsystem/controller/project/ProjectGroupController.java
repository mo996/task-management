package com.daaeboul.taskmanagementsystem.controller.project;



import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectGroup;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.project.ProjectGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/project-groups")
public class ProjectGroupController {

    @Autowired
    private ProjectGroupService projectGroupService;

    @PostMapping
    public ResponseEntity<ProjectGroup> createProjectGroup(@RequestBody ProjectGroup projectGroup) {
        return ResponseEntity.ok(projectGroupService.createProjectGroup(projectGroup));
    }


    @GetMapping("/{projectId}/{groupId}")
    public ResponseEntity<ProjectGroup> findProjectGroupById(@PathVariable Long projectId, @PathVariable Long groupId) {
        ProjectGroup.ProjectGroupId id = new ProjectGroup.ProjectGroupId(projectId, groupId);
        Optional<ProjectGroup> projectGroup = projectGroupService.findProjectGroupById(id);
        return projectGroup.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ProjectGroup>> findAllProjectGroups(Pageable pageable) {
        return ResponseEntity.ok(projectGroupService.findAllProjectGroups(pageable));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<ProjectGroup>> findProjectGroupsByProjectId(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(projectGroupService.findProjectGroupsByProjectId(projectId, pageable));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<ProjectGroup>> findProjectGroupsByGroupId(@PathVariable Long groupId, Pageable pageable) {
        return ResponseEntity.ok(projectGroupService.findProjectGroupsByGroupId(groupId, pageable));
    }

    @GetMapping("/role/{projectRoleId}")
    public ResponseEntity<Page<ProjectGroup>> findProjectGroupsByProjectRoleId(@PathVariable Long projectRoleId, Pageable pageable) {
        return ResponseEntity.ok(projectGroupService.findProjectGroupsByProjectRoleId(projectRoleId, pageable));
    }

    @GetMapping("/project/{projectId}/group/{groupId}")
    public ResponseEntity<ProjectGroup> findProjectGroupByProjectIdAndGroupId(@PathVariable Long projectId, @PathVariable Long groupId) {
        return ResponseEntity.ok(projectGroupService.findProjectGroupByProjectIdAndGroupId(projectId, groupId));
    }

    @GetMapping("/project-name/{projectName}")
    public ResponseEntity<Page<ProjectGroup>> findProjectGroupsByProjectName(@PathVariable String projectName, Pageable pageable) {
        return ResponseEntity.ok(projectGroupService.findProjectGroupsByProjectName(projectName, pageable));
    }

    @GetMapping("/group-name/{groupName}")
    public ResponseEntity<Page<ProjectGroup>> findProjectGroupsByGroupName(@PathVariable String groupName, Pageable pageable) {
        return ResponseEntity.ok(projectGroupService.findProjectGroupsByGroupName(groupName, pageable));
    }

    @PutMapping("/{projectId}/{groupId}")
    public ResponseEntity<ProjectGroup> updateProjectGroup(@PathVariable Long projectId, @PathVariable Long groupId, @RequestBody ProjectGroup projectGroup) {
        ProjectGroup.ProjectGroupId id = new ProjectGroup.ProjectGroupId(projectId, groupId);
        projectGroup.setId(id);
        return ResponseEntity.ok(projectGroupService.updateProjectGroup(projectGroup));
    }

    @DeleteMapping("/{projectId}/{groupId}")
    public ResponseEntity<Void> deleteProjectGroup(@PathVariable Long projectId, @PathVariable Long groupId) {
        ProjectGroup.ProjectGroupId id = new ProjectGroup.ProjectGroupId(projectId, groupId);
        projectGroupService.deleteProjectGroup(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Void> deleteProjectGroupsByProjectId(@PathVariable Long projectId) {
        projectGroupService.deleteProjectGroupsByProjectId(projectId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<Void> deleteProjectGroupsByGroupId(@PathVariable Long groupId) {
        projectGroupService.deleteProjectGroupsByGroupId(groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users-by-project/{projectId}")
    public ResponseEntity<Page<User>> findUsersByProjectId(@PathVariable Long projectId, Pageable pageable) {
        return ResponseEntity.ok(projectGroupService.findUsersByProjectId(projectId, pageable));
    }

    @GetMapping("/projects-by-group/{groupId}")
    public ResponseEntity<Page<Project>> findProjectsByGroupId(@PathVariable Long groupId, Pageable pageable) {
        return ResponseEntity.ok(projectGroupService.findProjectsByGroupId(groupId, pageable));
    }

    @GetMapping("/project/{projectId}/group-name/{groupName}")
    public ResponseEntity<List<ProjectGroup>> findProjectGroupsByProjectIdAndGroupName(@PathVariable Long projectId, @PathVariable String groupName) {
        return ResponseEntity.ok(projectGroupService.findProjectGroupsByProjectIdAndGroupName(projectId, groupName));
    }

    @GetMapping("/user/{userId}/project/{projectId}")
    public ResponseEntity<List<ProjectGroup>> findProjectGroupsByUserAndProject(@PathVariable Long userId, @PathVariable Long projectId) {
        return ResponseEntity.ok(projectGroupService.findProjectGroupsByUserAndProject(userId, projectId));
    }
}