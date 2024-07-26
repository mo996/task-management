package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.project.ProjectNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long projectId) {
        Optional<Project> project = projectService.findProjectById(projectId);
        return project.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.findAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<Project>> getAllDeletedProjects() {
        List<Project> projects = projectService.findAllDeletedProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/startDateBefore/{date}")
    public ResponseEntity<List<Project>> findProjectsByStartDateBefore(@PathVariable LocalDate date) {
        List<Project> projects = projectService.findProjectsByStartDateBefore(date.atStartOfDay());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/endDateAfter/{date}")
    public ResponseEntity<List<Project>> findProjectsByEndDateAfter(@PathVariable LocalDate date) {
        List<Project> projects = projectService.findProjectsByEndDateAfter(date.atStartOfDay());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/startDateBetween")
    public ResponseEntity<List<Project>> findProjectsByStartDateBetween(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<Project> projects = projectService.findProjectsByStartDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/nameContaining/{name}")
    public ResponseEntity<List<Project>> findProjectsByNameContaining(@PathVariable String name) {
        List<Project> projects = projectService.findProjectsByNameContaining(name);
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(@PathVariable Long projectId, @RequestBody Project projectDetails) {
        try {
            Project existingProject = projectService.findProjectById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException("Project id: "+ projectId));

            existingProject.setProjectName(projectDetails.getProjectName());
            existingProject.setProjectDescription(projectDetails.getProjectDescription());
            existingProject.setProjectStartDate(projectDetails.getProjectStartDate());
            existingProject.setProjectEndDate(projectDetails.getProjectEndDate());
            existingProject.setProjectTaskTypes(projectDetails.getProjectTaskTypes());

            Project updatedProject = projectService.updateProject(existingProject);
            return ResponseEntity.ok(updatedProject);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        try {
            projectService.deleteProject(projectId);
            return ResponseEntity.noContent().build();
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{projectId}/hard")
    public ResponseEntity<Void> hardDeleteProject(@PathVariable Long projectId) {
        try {
            projectService.hardDeleteProject(projectId);
            return ResponseEntity.noContent().build();
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
