package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectTaskType.ProjectTaskTypeNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectTaskTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProjectTaskTypeServiceTest {

    @Autowired
    private ProjectTaskTypeService projectTaskTypeService;

    @MockBean
    private ProjectTaskTypeRepository projectTaskTypeRepository;

    @Test
    void shouldCreateProjectTaskType() {
        // Sample data
        Project project = new Project();
        TaskType taskType = new TaskType();
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setProject(project);
        projectTaskType.setTaskType(taskType);

        when(projectTaskTypeRepository.save(any(ProjectTaskType.class))).thenReturn(projectTaskType);

        ProjectTaskType createdProjectTaskType = projectTaskTypeService.createProjectTaskType(projectTaskType);

        assertNotNull(createdProjectTaskType);
        assertEquals(project, createdProjectTaskType.getProject());
        assertEquals(taskType, createdProjectTaskType.getTaskType());
        verify(projectTaskTypeRepository).save(projectTaskType);
    }

    @Test
    void shouldFindProjectTaskTypeById() {
        Project project = new Project();
        TaskType taskType = new TaskType();
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setProject(project);
        projectTaskType.setTaskType(taskType);
        ProjectTaskType.ProjectTaskTypeId id = projectTaskType.getId();

        when(projectTaskTypeRepository.findById(id)).thenReturn(Optional.of(projectTaskType));

        Optional<ProjectTaskType> foundProjectTaskType = projectTaskTypeService.findProjectTaskTypeById(id);

        assertTrue(foundProjectTaskType.isPresent());
        assertEquals(projectTaskType, foundProjectTaskType.get());
        verify(projectTaskTypeRepository).findById(id);
    }

    @Test
    void shouldFindAllProjectTaskTypes() {
        List<ProjectTaskType> projectTaskTypes = Arrays.asList(new ProjectTaskType(), new ProjectTaskType());

        when(projectTaskTypeRepository.findAll()).thenReturn(projectTaskTypes);

        List<ProjectTaskType> foundProjectTaskTypes = projectTaskTypeService.findAllProjectTaskTypes();

        assertNotNull(foundProjectTaskTypes);
        assertEquals(2, foundProjectTaskTypes.size());
        verify(projectTaskTypeRepository).findAll();
    }

    @Test
    void shouldFindProjectTaskTypesByProjectName() {
        String projectName = "Test Project";
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setProject(new Project());
        projectTaskType.getProject().setProjectName(projectName);

        when(projectTaskTypeRepository.findByProjectName(projectName)).thenReturn(Arrays.asList(projectTaskType));

        List<ProjectTaskType> foundProjectTaskTypes = projectTaskTypeService.findProjectTaskTypesByProjectName(projectName);

        assertNotNull(foundProjectTaskTypes);
        assertEquals(1, foundProjectTaskTypes.size());
        assertEquals(projectTaskType, foundProjectTaskTypes.get(0));
        verify(projectTaskTypeRepository).findByProjectName(projectName);
    }

    @Test
    void shouldFindProjectTaskTypesByTaskTypeName() {
        String taskTypeName = "Task Type 1";
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setTaskType(new TaskType());
        projectTaskType.getTaskType().setTaskTypeName(taskTypeName);

        when(projectTaskTypeRepository.findByTaskTypeName(taskTypeName)).thenReturn(Arrays.asList(projectTaskType));

        List<ProjectTaskType> foundProjectTaskTypes = projectTaskTypeService.findProjectTaskTypesByTaskTypeName(taskTypeName);

        assertNotNull(foundProjectTaskTypes);
        assertEquals(1, foundProjectTaskTypes.size());
        assertEquals(projectTaskType, foundProjectTaskTypes.get(0));
        verify(projectTaskTypeRepository).findByTaskTypeName(taskTypeName);
    }

    @Test
    void shouldFindProjectTaskTypesByWorkflowName() {
        String workflowName = "Workflow 1";
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setWorkflow(new Workflow());
        projectTaskType.getWorkflow().setName(workflowName);

        when(projectTaskTypeRepository.findByWorkflowName(workflowName)).thenReturn(Arrays.asList(projectTaskType));

        List<ProjectTaskType> foundProjectTaskTypes = projectTaskTypeService.findProjectTaskTypesByWorkflowName(workflowName);

        assertNotNull(foundProjectTaskTypes);
        assertEquals(1, foundProjectTaskTypes.size());
        assertEquals(projectTaskType, foundProjectTaskTypes.get(0));
        verify(projectTaskTypeRepository).findByWorkflowName(workflowName);
    }

    @Test
    void shouldFindProjectsUsingWorkflowByName() {
        String workflowName = "Workflow 1";
        Project project = new Project();
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setProject(project);
        projectTaskType.setWorkflow(new Workflow());
        projectTaskType.getWorkflow().setName(workflowName);

        when(projectTaskTypeRepository.findProjectsUsingWorkflowByName(workflowName)).thenReturn(Arrays.asList(project));

        List<Project> foundProjects = projectTaskTypeService.findProjectsUsingWorkflowByName(workflowName);

        assertNotNull(foundProjects);
        assertEquals(1, foundProjects.size());
        assertEquals(project, foundProjects.get(0));
        verify(projectTaskTypeRepository).findProjectsUsingWorkflowByName(workflowName);
    }

    @Test
    void shouldFindTaskTypesByProjectName() {
        String projectName = "Test Project";
        TaskType taskType = new TaskType();
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setProject(new Project());
        projectTaskType.getProject().setProjectName(projectName);
        projectTaskType.setTaskType(taskType);

        when(projectTaskTypeRepository.findTaskTypesByProjectName(projectName)).thenReturn(Arrays.asList(taskType));

        List<TaskType> foundTaskTypes = projectTaskTypeService.findTaskTypesByProjectName(projectName);

        assertNotNull(foundTaskTypes);
        assertEquals(1, foundTaskTypes.size());
        assertEquals(taskType, foundTaskTypes.get(0));
        verify(projectTaskTypeRepository).findTaskTypesByProjectName(projectName);
    }

    @Test
    void shouldCountDistinctTaskTypesByProjectName() {
        String projectName = "Test Project";
        Long expectedCount = 2L;

        when(projectTaskTypeRepository.countDistinctTaskTypesByProjectName(projectName)).thenReturn(expectedCount);

        Long count = projectTaskTypeService.countDistinctTaskTypesByProjectName(projectName);

        assertEquals(expectedCount, count);
        verify(projectTaskTypeRepository).countDistinctTaskTypesByProjectName(projectName);
    }

    @Test
    void shouldUpdateProjectTaskType() {
        Project project = new Project();
        TaskType taskType = new TaskType();
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setProject(project);
        projectTaskType.setTaskType(taskType);
        ProjectTaskType.ProjectTaskTypeId id = projectTaskType.getId();

        Workflow updatedWorkflow = new Workflow();
        updatedWorkflow.setName("Updated Workflow");
        projectTaskType.setWorkflow(updatedWorkflow);

        when(projectTaskTypeRepository.findById(projectTaskType.getId())).thenReturn(Optional.of(projectTaskType));
        when(projectTaskTypeRepository.save(projectTaskType)).thenReturn(projectTaskType);

        ProjectTaskType result = projectTaskTypeService.updateProjectTaskType(projectTaskType);

        assertNotNull(result);
        assertEquals(projectTaskType, result);
        assertEquals(updatedWorkflow, result.getWorkflow());
        verify(projectTaskTypeRepository).save(projectTaskType);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProjectTaskType() {
        ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId();
        ProjectTaskType updatedProjectTaskType = new ProjectTaskType();
        updatedProjectTaskType.setId(id);

        when(projectTaskTypeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProjectTaskTypeNotFoundException.class, () -> projectTaskTypeService.updateProjectTaskType(updatedProjectTaskType));
    }

    @Test
    void shouldDeleteProjectTaskTypeById() {
        ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId();

        when(projectTaskTypeRepository.existsById(id)).thenReturn(true);
        doNothing().when(projectTaskTypeRepository).deleteById(id);

        projectTaskTypeService.deleteProjectTaskType(id);

        verify(projectTaskTypeRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProjectTaskType() {
        ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId();

        when(projectTaskTypeRepository.existsById(id)).thenReturn(false);

        assertThrows(ProjectTaskTypeNotFoundException.class, () -> projectTaskTypeService.deleteProjectTaskType(id));
    }
}