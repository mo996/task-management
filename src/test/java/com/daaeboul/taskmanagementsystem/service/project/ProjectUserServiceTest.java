package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectUser.ProjectUserNotFoundException;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import com.daaeboul.taskmanagementsystem.model.project.ProjectUser;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProjectUserServiceTest {

    @Autowired
    private ProjectUserService projectUserService;

    @MockBean
    private ProjectUserRepository projectUserRepository;

    @Test
    void shouldCreateProjectUser() {
        Project project = new Project();
        User user = new User();
        ProjectRole projectRole = new ProjectRole();
        ProjectUser projectUser = new ProjectUser();
        projectUser.setProject(project);
        projectUser.setUser(user);
        projectUser.setProjectRole(projectRole);

        when(projectUserRepository.save(any(ProjectUser.class))).thenReturn(projectUser);

        ProjectUser createdProjectUser = projectUserService.createProjectUser(projectUser);

        assertNotNull(createdProjectUser);
        assertEquals(project, createdProjectUser.getProject());
        assertEquals(user, createdProjectUser.getUser());
        assertEquals(projectRole, createdProjectUser.getProjectRole());
        verify(projectUserRepository).save(projectUser);
    }

    @Test
    void shouldFindProjectUserById() {
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 2L);
        Project project = new Project();
        User user = new User();
        ProjectRole projectRole = new ProjectRole();
        ProjectUser projectUser = new ProjectUser();
        projectUser.setId(id);
        projectUser.setProject(project);
        projectUser.setUser(user);
        projectUser.setProjectRole(projectRole);

        when(projectUserRepository.findById(id)).thenReturn(Optional.of(projectUser));

        Optional<ProjectUser> foundProjectUser = projectUserService.findProjectUserById(id);

        assertTrue(foundProjectUser.isPresent());
        assertEquals(projectUser, foundProjectUser.get());
        verify(projectUserRepository).findById(id);
    }

    @Test
    void shouldFindAllProjectUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectUser> projectUserList = Arrays.asList(new ProjectUser(), new ProjectUser());
        Page<ProjectUser> projectUserPage = new PageImpl<>(projectUserList, pageable, projectUserList.size());

        when(projectUserRepository.findAll(pageable)).thenReturn(projectUserPage);

        Page<ProjectUser> projectUsers = projectUserService.findAllProjectUsers(pageable);

        assertNotNull(projectUsers);
        assertEquals(2, projectUsers.getTotalElements());
        verify(projectUserRepository).findAll(pageable);
    }


    @Test
    void shouldUpdateProjectUser() {
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 2L);
        Project project = new Project();
        User user = new User();
        ProjectRole projectRole = new ProjectRole();
        ProjectUser projectUser = new ProjectUser();
        projectUser.setId(id);
        projectUser.setProject(project);
        projectUser.setUser(user);
        projectUser.setProjectRole(projectRole);

        ProjectRole newProjectRole = new ProjectRole();
        ProjectUser updatedProjectUser = new ProjectUser();
        updatedProjectUser.setId(id);
        updatedProjectUser.setProject(project);
        updatedProjectUser.setUser(user);
        updatedProjectUser.setProjectRole(newProjectRole);

        when(projectUserRepository.findById(id)).thenReturn(Optional.of(projectUser));
        when(projectUserRepository.save(any(ProjectUser.class))).thenReturn(updatedProjectUser);

        ProjectUser result = projectUserService.updateProjectUser(updatedProjectUser);

        assertNotNull(result);
        assertEquals(newProjectRole, result.getProjectRole());
        verify(projectUserRepository).save(any(ProjectUser.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProjectUser() {
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 2L);
        ProjectUser updatedProjectUser = new ProjectUser();
        updatedProjectUser.setId(id);

        when(projectUserRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProjectUserNotFoundException.class, () -> projectUserService.updateProjectUser(updatedProjectUser));
    }

    @Test
    void shouldDeleteProjectUserById() {
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 2L);

        when(projectUserRepository.existsById(id)).thenReturn(true);
        doNothing().when(projectUserRepository).deleteById(id);

        projectUserService.deleteProjectUser(id);

        verify(projectUserRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProjectUser() {
        ProjectUser.ProjectUserId id = new ProjectUser.ProjectUserId(1L, 2L);

        when(projectUserRepository.existsById(id)).thenReturn(false);

        assertThrows(ProjectUserNotFoundException.class, () -> projectUserService.deleteProjectUser(id));
    }


    @Test
    void shouldFindUsersByProjectId() {
        Long projectId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = Arrays.asList(new User(), new User());
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(projectUserRepository.findUsersByProjectId(projectId, pageable)).thenReturn(userPage);

        Page<User> users = projectUserService.findUsersByProjectId(projectId, pageable);

        assertNotNull(users);
        assertEquals(2, users.getTotalElements());
        verify(projectUserRepository).findUsersByProjectId(projectId, pageable);
    }

    @Test
    void shouldFindProjectsByUserId() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projectList = Arrays.asList(new Project(), new Project());
        Page<Project> projectPage = new PageImpl<>(projectList, pageable, projectList.size());

        when(projectUserRepository.findProjectsByUserId(userId, pageable)).thenReturn(projectPage);

        Page<Project> projects = projectUserService.findProjectsByUserId(userId, pageable);

        assertNotNull(projects);
        assertEquals(2, projects.getTotalElements());
        verify(projectUserRepository).findProjectsByUserId(userId, pageable);
    }

    @Test
    void shouldCheckIfProjectUserExists() {
        Long projectId = 1L;
        Long userId = 2L;

        when(projectUserRepository.existsByIdProjectIdAndIdUserId(projectId, userId)).thenReturn(true);

        boolean exists = projectUserService.existsProjectUserByProjectIdAndUserId(projectId, userId);

        assertTrue(exists);
        verify(projectUserRepository).existsByIdProjectIdAndIdUserId(projectId, userId);
    }

    @Test
    void shouldCountProjectUsersByProjectId() {
        Long projectId = 1L;

        when(projectUserRepository.countByIdProjectId(projectId)).thenReturn(5L);

        long count = projectUserService.countProjectUsersByProjectId(projectId);

        assertEquals(5L, count);
        verify(projectUserRepository).countByIdProjectId(projectId);
    }

    @Test
    void shouldCountProjectUsersByUserId() {
        Long userId = 2L;

        when(projectUserRepository.countByIdUserId(userId)).thenReturn(3L);

        long count = projectUserService.countProjectUsersByUserId(userId);

        assertEquals(3L, count);
        verify(projectUserRepository).countByIdUserId(userId);
    }

    @Test
    void shouldFindProjectUsersByProjectId() {
        Long projectId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectUser> projectUserList = Arrays.asList(new ProjectUser(), new ProjectUser());
        Page<ProjectUser> projectUserPage = new PageImpl<>(projectUserList, pageable, projectUserList.size());

        when(projectUserRepository.findByIdProjectId(projectId, pageable)).thenReturn(projectUserPage);

        Page<ProjectUser> projectUsers = projectUserService.findProjectUsersByProjectId(projectId, pageable);

        assertNotNull(projectUsers);
        assertEquals(2, projectUsers.getTotalElements());
        verify(projectUserRepository).findByIdProjectId(projectId, pageable);
    }

    @Test
    void shouldFindProjectUsersByUserId() {
        Long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectUser> projectUserList = Arrays.asList(new ProjectUser(), new ProjectUser());
        Page<ProjectUser> projectUserPage = new PageImpl<>(projectUserList, pageable, projectUserList.size());

        when(projectUserRepository.findByIdUserId(userId, pageable)).thenReturn(projectUserPage);

        Page<ProjectUser> projectUsers = projectUserService.findProjectUsersByUserId(userId, pageable);

        assertNotNull(projectUsers);
        assertEquals(2, projectUsers.getTotalElements());
        verify(projectUserRepository).findByIdUserId(userId, pageable);
    }

    @Test
    void shouldFindProjectUserByProjectIdAndUserId() {
        Long projectId = 1L;
        Long userId = 2L;
        ProjectUser projectUser = new ProjectUser();

        when(projectUserRepository.findByIdProjectIdAndIdUserId(projectId, userId)).thenReturn(projectUser);

        ProjectUser foundProjectUser = projectUserService.findProjectUserByProjectIdAndUserId(projectId, userId);

        assertNotNull(foundProjectUser);
        assertEquals(projectUser, foundProjectUser);
        verify(projectUserRepository).findByIdProjectIdAndIdUserId(projectId, userId);
    }

    @Test
    void shouldFindProjectUsersByProjectNameAndUserId() {
        String projectName = "Test Project";
        Long userId = 2L;
        List<ProjectUser> projectUserList = Arrays.asList(new ProjectUser(), new ProjectUser());

        when(projectUserRepository.findByProjectNameAndUserId(projectName, userId)).thenReturn(projectUserList);

        List<ProjectUser> foundProjectUsers = projectUserService.findProjectUsersByProjectNameAndUserId(projectName, userId);

        assertNotNull(foundProjectUsers);
        assertEquals(2, foundProjectUsers.size());
        verify(projectUserRepository).findByProjectNameAndUserId(projectName, userId);
    }

    @Test
    void shouldFindProjectUsersByProjectIdAndProjectRoleName() {
        Long projectId = 1L;
        String roleName = "Developer";
        List<ProjectUser> projectUserList = Arrays.asList(new ProjectUser(), new ProjectUser());

        when(projectUserRepository.findByProjectIdAndProjectRoleName(projectId, roleName)).thenReturn(projectUserList);

        List<ProjectUser> foundProjectUsers = projectUserService.findProjectUsersByProjectIdAndProjectRoleName(projectId, roleName);

        assertNotNull(foundProjectUsers);
        assertEquals(2, foundProjectUsers.size());
        verify(projectUserRepository).findByProjectIdAndProjectRoleName(projectId, roleName);
    }

    @Test
    void shouldFindProjectUsersByProjectNameAndProjectRoleName() {
        String projectName = "Test Project";
        String roleName = "Developer";
        List<ProjectUser> projectUserList = Arrays.asList(new ProjectUser(), new ProjectUser());

        when(projectUserRepository.findByProjectNameAndProjectRoleName(projectName, roleName)).thenReturn(projectUserList);

        List<ProjectUser> foundProjectUsers = projectUserService.findProjectUsersByProjectNameAndProjectRoleName(projectName, roleName);

        assertNotNull(foundProjectUsers);
        assertEquals(2, foundProjectUsers.size());
        verify(projectUserRepository).findByProjectNameAndProjectRoleName(projectName, roleName);
    }
}
