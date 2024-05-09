package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.project.DuplicateProjectNameException;
import com.daaeboul.taskmanagementsystem.exceptions.project.project.ProjectNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @MockBean
    private ProjectRepository projectRepository;

    @Test
    void shouldCreateProject() {
        Project newProject = new Project();
        newProject.setProjectName("New Project");
        newProject.setProjectStartDate(LocalDateTime.now().minusDays(5));
        newProject.setProjectEndDate(LocalDateTime.now().plusDays(5));
        when(projectRepository.save(any(Project.class))).thenReturn(newProject);

        Project createdProject = projectService.createProject(newProject);

        assertNotNull(createdProject);
        assertEquals("New Project", createdProject.getProjectName());
        verify(projectRepository).save(newProject);
    }

    @Test
    void shouldThrowExceptionWhenProjectNameIsDuplicate() {
        // Existing project (simulating one found in the database)
        Project existingProject = new Project();
        existingProject.setProjectName("Existing Project");
        existingProject.setProjectStartDate(LocalDateTime.now().minusDays(10));
        existingProject.setProjectEndDate(LocalDateTime.now());
        ReflectionTestUtils.setField(existingProject, "id", 1L);

        // New project (attempting to create this should cause the exception)
        Project newProject = new Project();
        newProject.setProjectName("Existing Project");  // Same name to trigger the exception
        newProject.setProjectStartDate(LocalDateTime.now().minusDays(5));
        newProject.setProjectEndDate(LocalDateTime.now().plusDays(5));

        // Simulate finding an existing project with the same name
        when(projectRepository.findByProjectNameContainingIgnoreCaseAndDeletedAtIsNull("Existing Project"))
                .thenReturn(List.of(existingProject));

        // Attempt to create a new project with a duplicate name should throw exception
        assertThrows(DuplicateProjectNameException.class, () -> projectService.createProject(newProject));
    }



    @Test
    void shouldFindProjectById() {
        Long projectId = 1L;
        Project expectedProject = new Project();
        expectedProject.setProjectName("Existing Project");
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(expectedProject));

        Optional<Project> foundProject = projectService.findProjectById(projectId);

        assertTrue(foundProject.isPresent());
        assertEquals(expectedProject, foundProject.get());
    }

    @Test
    void shouldUpdateProjectSuccessfully() {
        Project existingProject = new Project();
        ReflectionTestUtils.setField(existingProject, "id", 1L);
        existingProject.setProjectName("Original Project Name");
        existingProject.setProjectStartDate(LocalDateTime.now().minusDays(5));
        existingProject.setProjectEndDate(LocalDateTime.now().plusDays(5));

        Project updatedProject = new Project();
        ReflectionTestUtils.setField(updatedProject, "id", 1L);
        updatedProject.setProjectName("Updated Project Name");
        updatedProject.setProjectStartDate(LocalDateTime.now().minusDays(3));  // Ensure this is not null
        updatedProject.setProjectEndDate(LocalDateTime.now().plusDays(7));  // Ensure end date is valid

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        Project result = projectService.updateProject(updatedProject);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Project Name", result.getProjectName());
        verify(projectRepository).save(any(Project.class));
    }


    @Test
    void shouldDeleteProjectSuccessfully() {
        Long projectId = 1L;
        doNothing().when(projectRepository).deleteById(projectId);

        projectService.deleteProject(projectId);

        verify(projectRepository).deleteById(projectId);
    }

    @Test
    void shouldThrowProjectNotFoundExceptionOnDeleteWhenProjectDoesNotExist() {
        Long projectId = 999L;
        doThrow(new ProjectNotFoundException("Project not found")).when(projectRepository).deleteById(projectId);

        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(projectId));
    }

    @Test
    void findAllProjectsTest() {
        List<Project> expectedProjects = Arrays.asList(new Project(), new Project());
        when(projectRepository.findAllByDeletedAtIsNull()).thenReturn(expectedProjects);

        List<Project> projects = projectService.findAllProjects();

        assertNotNull(projects);
        assertEquals(2, projects.size());
    }

    @Test
    void findAllProjectsPageableTest() {
        Pageable pageable = PageRequest.of(0, 1);
        List<Project> projectList = List.of(new Project());
        Page<Project> projectPage = new PageImpl<>(projectList, pageable, projectList.size());
        when(projectRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(projectPage);

        Page<Project> projects = projectService.findAllProjects(pageable);

        assertNotNull(projects);
        assertEquals(1, projects.getTotalElements());
    }

    @Test
    void findAllDeletedProjectsTest() {
        List<Project> expectedProjects = Arrays.asList(new Project(), new Project());
        when(projectRepository.findAllByDeletedAtIsNotNull()).thenReturn(expectedProjects);

        List<Project> projects = projectService.findAllDeletedProjects();

        assertNotNull(projects);
        assertEquals(2, projects.size());
    }

    @Test
    void shouldHardDeleteProjectSuccessfully() {
        Long projectId = 1L;
        doNothing().when(projectRepository).hardDeleteById(projectId);

        projectService.hardDeleteProject(projectId);

        verify(projectRepository).hardDeleteById(projectId);
    }

    @Test
    void findProjectsByStartDateBeforeTest() {
        LocalDateTime date = LocalDateTime.now();
        List<Project> expectedProjects = List.of(new Project());
        when(projectRepository.findByProjectStartDateBeforeAndDeletedAtIsNull(date)).thenReturn(expectedProjects);

        List<Project> projects = projectService.findProjectsByStartDateBefore(date);

        assertNotNull(projects);
        assertEquals(expectedProjects.size(), projects.size());
        verify(projectRepository).findByProjectStartDateBeforeAndDeletedAtIsNull(date);
    }

    @Test
    void findProjectsByEndDateAfterTest() {
        LocalDateTime date = LocalDateTime.now();
        List<Project> expectedProjects = List.of(new Project());
        when(projectRepository.findByProjectEndDateAfterAndDeletedAtIsNull(date)).thenReturn(expectedProjects);

        List<Project> projects = projectService.findProjectsByEndDateAfter(date);

        assertNotNull(projects);
        assertEquals(expectedProjects.size(), projects.size());
        verify(projectRepository).findByProjectEndDateAfterAndDeletedAtIsNull(date);
    }

    @Test
    void findProjectsByStartDateBetweenTest() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(10);
        LocalDateTime endDate = LocalDateTime.now();
        List<Project> expectedProjects = List.of(new Project());
        when(projectRepository.findByProjectStartDateBetweenAndDeletedAtIsNull(startDate, endDate)).thenReturn(expectedProjects);

        List<Project> projects = projectService.findProjectsByStartDateBetween(startDate, endDate);

        assertNotNull(projects);
        assertEquals(expectedProjects.size(), projects.size());
        verify(projectRepository).findByProjectStartDateBetweenAndDeletedAtIsNull(startDate, endDate);
    }

    @Test
    void findProjectsByNameContainingTest() {
        String name = "Test Project";
        List<Project> expectedProjects = List.of(new Project());
        when(projectRepository.findByProjectNameContainingIgnoreCaseAndDeletedAtIsNull(name)).thenReturn(expectedProjects);

        List<Project> projects = projectService.findProjectsByNameContaining(name);

        assertNotNull(projects);
        assertEquals(expectedProjects.size(), projects.size());
        verify(projectRepository).findByProjectNameContainingIgnoreCaseAndDeletedAtIsNull(name);
    }
}
