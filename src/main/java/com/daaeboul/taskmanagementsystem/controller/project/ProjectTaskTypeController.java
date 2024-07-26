package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectTaskType.ProjectTaskTypeNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.service.project.ProjectTaskTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/project-task-types")
public class ProjectTaskTypeController {

    private final ProjectTaskTypeService projectTaskTypeService;

    @Autowired
    public ProjectTaskTypeController(ProjectTaskTypeService projectTaskTypeService) {
        this.projectTaskTypeService = projectTaskTypeService;
    }

    @PostMapping
    public ResponseEntity<ProjectTaskType> createProjectTaskType(@RequestBody ProjectTaskType projectTaskType) {
        ProjectTaskType createdProjectTaskType = projectTaskTypeService.createProjectTaskType(projectTaskType);
        return ResponseEntity.ok(createdProjectTaskType);
    }

    @GetMapping("/{projectId}/{taskTypeId}")
    public ResponseEntity<ProjectTaskType> findProjectTaskTypeById(@PathVariable Long projectId, @PathVariable Long taskTypeId) {
        ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId(projectId, taskTypeId);
        Optional<ProjectTaskType> projectTaskType = projectTaskTypeService.findProjectTaskTypeById(id);
        return projectTaskType.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProjectTaskType>> findAllProjectTaskTypes() {
        List<ProjectTaskType> projectTaskTypes = projectTaskTypeService.findAllProjectTaskTypes();
        return ResponseEntity.ok(projectTaskTypes);
    }

    @GetMapping("/project-name/{projectName}")
    public ResponseEntity<List<ProjectTaskType>> findProjectTaskTypesByProjectName(@PathVariable String projectName) {
        List<ProjectTaskType> projectTaskTypes = projectTaskTypeService.findProjectTaskTypesByProjectName(projectName);
        return ResponseEntity.ok(projectTaskTypes);
    }

    @GetMapping("/task-type-name/{taskTypeName}")
    public ResponseEntity<List<ProjectTaskType>> findProjectTaskTypesByTaskTypeName(@PathVariable String taskTypeName) {
        List<ProjectTaskType> projectTaskTypes = projectTaskTypeService.findProjectTaskTypesByTaskTypeName(taskTypeName);
        return ResponseEntity.ok(projectTaskTypes);
    }

    @GetMapping("/workflow-name/{workflowName}")
    public ResponseEntity<List<ProjectTaskType>> findProjectTaskTypesByWorkflowName(@PathVariable String workflowName) {
        List<ProjectTaskType> projectTaskTypes = projectTaskTypeService.findProjectTaskTypesByWorkflowName(workflowName);
        return ResponseEntity.ok(projectTaskTypes);
    }

    @GetMapping("/projects-using-workflow/{workflowName}")
    public ResponseEntity<List<Project>> findProjectsUsingWorkflowByName(@PathVariable String workflowName) {
        List<Project> projects = projectTaskTypeService.findProjectsUsingWorkflowByName(workflowName);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/task-types-by-project/{projectName}")
    public ResponseEntity<List<TaskType>> findTaskTypesByProjectName(@PathVariable String projectName) {
        List<TaskType> taskTypes = projectTaskTypeService.findTaskTypesByProjectName(projectName);
        return ResponseEntity.ok(taskTypes);
    }

    @GetMapping("/count-task-types-by-project/{projectName}")
    public ResponseEntity<Long> countDistinctTaskTypesByProjectName(@PathVariable String projectName) {
        Long count = projectTaskTypeService.countDistinctTaskTypesByProjectName(projectName);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{projectId}/{taskTypeId}")
    public ResponseEntity<ProjectTaskType> updateProjectTaskType(@PathVariable Long projectId, @PathVariable Long taskTypeId, @RequestBody ProjectTaskType projectTaskTypeDetails) {
        try {
            ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId(projectId, taskTypeId);
            ProjectTaskType existingProjectTaskType = projectTaskTypeService.findProjectTaskTypeById(id)
                    .orElseThrow(() -> new ProjectTaskTypeNotFoundException("Project-TaskType association not found"));

            existingProjectTaskType.setProject(projectTaskTypeDetails.getProject());
            existingProjectTaskType.setTaskType(projectTaskTypeDetails.getTaskType());
            existingProjectTaskType.setWorkflow(projectTaskTypeDetails.getWorkflow());

            ProjectTaskType updatedProjectTaskType = projectTaskTypeService.updateProjectTaskType(existingProjectTaskType);
            return ResponseEntity.ok(updatedProjectTaskType);
        } catch (ProjectTaskTypeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{projectId}/{taskTypeId}")
    public ResponseEntity<Void> deleteProjectTaskType(@PathVariable Long projectId, @PathVariable Long taskTypeId) {
        try {
            ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId(projectId, taskTypeId);
            projectTaskTypeService.deleteProjectTaskType(id);
            return ResponseEntity.noContent().build();
        } catch (ProjectTaskTypeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}