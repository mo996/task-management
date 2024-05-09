package com.daaeboul.taskmanagementsystem.service.project;

import com.daaeboul.taskmanagementsystem.exceptions.project.projectGroup.ProjectGroupNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectGroup;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import com.daaeboul.taskmanagementsystem.model.user.User;
import org.junit.jupiter.api.Test;
import com.daaeboul.taskmanagementsystem.repository.project.ProjectGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProjectGroupServiceTest {

    @Autowired
    private ProjectGroupService projectGroupService;

    @MockBean
    private ProjectGroupRepository projectGroupRepository;
    @Test
    void shouldCreateProjectGroup() {
        Project project = new Project();
        Group group = new Group();
        ProjectGroup projectGroup = new ProjectGroup();
        projectGroup.setProject(project);
        projectGroup.setGroup(group);

        when(projectGroupRepository.save(any(ProjectGroup.class))).thenReturn(projectGroup);

        ProjectGroup createdProjectGroup = projectGroupService.createProjectGroup(projectGroup);

        assertNotNull(createdProjectGroup);
        assertEquals(project, createdProjectGroup.getProject());
        assertEquals(group, createdProjectGroup.getGroup());
        verify(projectGroupRepository).save(projectGroup);
    }

    @Test
    void shouldFindProjectGroupById() {
        Project project = new Project();
        Group group = new Group();
        ProjectGroup projectGroup = new ProjectGroup();
        projectGroup.setProject(project);
        projectGroup.setGroup(group);
        ProjectGroup.ProjectGroupId id = projectGroup.getId();

        when(projectGroupRepository.findById(id)).thenReturn(Optional.of(projectGroup));

        Optional<ProjectGroup> foundProjectGroup = projectGroupService.findProjectGroupById(id);

        assertTrue(foundProjectGroup.isPresent());
        assertEquals(projectGroup, foundProjectGroup.get());
        verify(projectGroupRepository).findById(id);
    }

    @Test
    void shouldFindAllProjectGroups() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectGroup> projectGroupList = Arrays.asList(new ProjectGroup(), new ProjectGroup());
        Page<ProjectGroup> projectGroupPage = new PageImpl<>(projectGroupList, pageable, projectGroupList.size());

        when(projectGroupRepository.findAll(pageable)).thenReturn(projectGroupPage);

        Page<ProjectGroup> projectGroups = projectGroupService.findAllProjectGroups(pageable);

        assertNotNull(projectGroups);
        assertEquals(2, projectGroups.getTotalElements());
        verify(projectGroupRepository).findAll(pageable);
    }

    @Test
    void shouldFindProjectGroupsByProjectId() {
        Long projectId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectGroup> projectGroupList = Arrays.asList(new ProjectGroup(), new ProjectGroup());
        Page<ProjectGroup> projectGroupPage = new PageImpl<>(projectGroupList, pageable, projectGroupList.size());

        when(projectGroupRepository.findByProjectId(projectId, pageable)).thenReturn(projectGroupPage);

        Page<ProjectGroup> projectGroups = projectGroupService.findProjectGroupsByProjectId(projectId, pageable);

        assertNotNull(projectGroups);
        assertEquals(2, projectGroups.getTotalElements());
        verify(projectGroupRepository).findByProjectId(projectId, pageable);
    }

    @Test
    void shouldFindProjectGroupsByGroupId() {
        Long groupId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectGroup> projectGroupList = Arrays.asList(new ProjectGroup(), new ProjectGroup());
        Page<ProjectGroup> projectGroupPage = new PageImpl<>(projectGroupList, pageable, projectGroupList.size());

        when(projectGroupRepository.findByGroupId(groupId, pageable)).thenReturn(projectGroupPage);

        Page<ProjectGroup> projectGroups = projectGroupService.findProjectGroupsByGroupId(groupId, pageable);

        assertNotNull(projectGroups);
        assertEquals(2, projectGroups.getTotalElements());
        verify(projectGroupRepository).findByGroupId(groupId, pageable);
    }

    @Test
    void shouldFindProjectGroupsByProjectRoleId() {
        Long projectRoleId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectGroup> projectGroupList = Arrays.asList(new ProjectGroup(), new ProjectGroup());
        Page<ProjectGroup> projectGroupPage = new PageImpl<>(projectGroupList, pageable, projectGroupList.size());

        when(projectGroupRepository.findByProjectRoleId(projectRoleId, pageable)).thenReturn(projectGroupPage);

        Page<ProjectGroup> projectGroups = projectGroupService.findProjectGroupsByProjectRoleId(projectRoleId, pageable);

        assertNotNull(projectGroups);
        assertEquals(2, projectGroups.getTotalElements());
        verify(projectGroupRepository).findByProjectRoleId(projectRoleId, pageable);
    }

    @Test
    void shouldFindProjectGroupByProjectIdAndGroupId() {
        Long projectId = 1L;
        Long groupId = 3L;
        Project project = new Project();
        Group group = new Group();
        ProjectGroup projectGroup = new ProjectGroup();
        projectGroup.setProject(project);
        projectGroup.setGroup(group);

        when(projectGroupRepository.findByProjectIdAndGroupId(projectId, groupId)).thenReturn(projectGroup);

        ProjectGroup foundProjectGroup = projectGroupService.findProjectGroupByProjectIdAndGroupId(projectId, groupId);

        assertNotNull(foundProjectGroup);
        assertEquals(project, foundProjectGroup.getProject());
        assertEquals(group, foundProjectGroup.getGroup());
        verify(projectGroupRepository).findByProjectIdAndGroupId(projectId, groupId);
    }

    @Test
    void shouldDeleteProjectGroupsByGroupId() {
        Long groupId = 1L;

        doNothing().when(projectGroupRepository).deleteByGroupId(groupId);

        projectGroupService.deleteProjectGroupsByGroupId(groupId);

        verify(projectGroupRepository).deleteByGroupId(groupId);
    }

    @Test
    void shouldFindProjectGroupsByProjectIdAndGroupName() {
        Long projectId = 1L;
        String groupName = "Test Group";
        List<ProjectGroup> expectedProjectGroups = Arrays.asList(new ProjectGroup(), new ProjectGroup());

        when(projectGroupRepository.findByProjectIdAndGroupGroupName(projectId, groupName)).thenReturn(expectedProjectGroups);

        List<ProjectGroup> projectGroups = projectGroupService.findProjectGroupsByProjectIdAndGroupName(projectId, groupName);

        assertNotNull(projectGroups);
        assertEquals(expectedProjectGroups.size(), projectGroups.size());
        verify(projectGroupRepository).findByProjectIdAndGroupGroupName(projectId, groupName);
    }

    @Test
    void shouldDeleteProjectGroupsByProjectId() {
        Long projectId = 1L;

        doNothing().when(projectGroupRepository).deleteByProjectId(projectId);

        projectGroupService.deleteProjectGroupsByProjectId(projectId);

        verify(projectGroupRepository).deleteByProjectId(projectId);
    }

    @Test
    void shouldFindProjectGroupsByGroupName() {
        String groupName = "Test Group";
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectGroup> projectGroupList = Arrays.asList(new ProjectGroup(), new ProjectGroup());
        Page<ProjectGroup> projectGroupPage = new PageImpl<>(projectGroupList, pageable, projectGroupList.size());

        when(projectGroupRepository.findByGroupGroupName(groupName, pageable)).thenReturn(projectGroupPage);

        Page<ProjectGroup> projectGroups = projectGroupService.findProjectGroupsByGroupName(groupName, pageable);

        assertNotNull(projectGroups);
        assertEquals(2, projectGroups.getTotalElements());
        verify(projectGroupRepository).findByGroupGroupName(groupName, pageable);
    }

    @Test
    void shouldFindProjectGroupsByProjectName() {
        String projectName = "Test Project";
        Pageable pageable = PageRequest.of(0, 10);
        List<ProjectGroup> projectGroupList = Arrays.asList(new ProjectGroup(), new ProjectGroup());
        Page<ProjectGroup> projectGroupPage = new PageImpl<>(projectGroupList, pageable, projectGroupList.size());

        when(projectGroupRepository.findByProjectProjectName(projectName, pageable)).thenReturn(projectGroupPage);

        Page<ProjectGroup> projectGroups = projectGroupService.findProjectGroupsByProjectName(projectName, pageable);

        assertNotNull(projectGroups);
        assertEquals(2, projectGroups.getTotalElements());
        verify(projectGroupRepository).findByProjectProjectName(projectName, pageable);
    }

    @Test
    public void shouldUpdateProjectGroup() {
        Project project = new Project();
        Group group = new Group();
        ProjectGroup projectGroup = new ProjectGroup();
        projectGroup.setProject(project);
        projectGroup.setGroup(group);
        ProjectGroup.ProjectGroupId id = projectGroup.getId();

        ProjectRole updatedRole = new ProjectRole();
        updatedRole.setRoleName("Updated Role");

        projectGroup.setProjectRole(updatedRole);

        when(projectGroupRepository.findById(projectGroup.getId())).thenReturn(Optional.of(projectGroup));

        when(projectGroupRepository.save(projectGroup)).thenReturn(projectGroup);

        ProjectGroup result = projectGroupService.updateProjectGroup(projectGroup);

        assertNotNull(result);
        assertEquals(projectGroup, result);
        assertEquals(updatedRole, result.getProjectRole());
        verify(projectGroupRepository).save(projectGroup);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProjectGroup() {
        ProjectGroup.ProjectGroupId id = new ProjectGroup.ProjectGroupId();
        ProjectGroup updatedProjectGroup = new ProjectGroup();
        updatedProjectGroup.setId(id);

        when(projectGroupRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProjectGroupNotFoundException.class, () -> projectGroupService.updateProjectGroup(updatedProjectGroup));
    }

    @Test
    void shouldDeleteProjectGroupById() {
        ProjectGroup.ProjectGroupId id = new ProjectGroup.ProjectGroupId();

        when(projectGroupRepository.existsById(id)).thenReturn(true);
        doNothing().when(projectGroupRepository).deleteById(id);

        projectGroupService.deleteProjectGroup(id);

        verify(projectGroupRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProjectGroup() {
        ProjectGroup.ProjectGroupId id = new ProjectGroup.ProjectGroupId();

        when(projectGroupRepository.existsById(id)).thenReturn(false);

        assertThrows(ProjectGroupNotFoundException.class, () -> projectGroupService.deleteProjectGroup(id));
    }

    @Test
    void shouldFindUsersByProjectId() {
        Long projectId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = Arrays.asList(new User(), new User());
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(projectGroupRepository.findUsersByProjectId(projectId, pageable)).thenReturn(userPage);

        Page<User> users = projectGroupService.findUsersByProjectId(projectId, pageable);

        assertNotNull(users);
        assertEquals(2, users.getTotalElements());
        verify(projectGroupRepository).findUsersByProjectId(projectId, pageable);
    }

    @Test
    void shouldFindProjectsByGroupId() {
        Long groupId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projectList = Arrays.asList(new Project(), new Project());
        Page<Project> projectPage = new PageImpl<>(projectList, pageable, projectList.size());

        when(projectGroupRepository.findProjectsByGroupId(groupId, pageable)).thenReturn(projectPage);

        Page<Project> projects = projectGroupService.findProjectsByGroupId(groupId, pageable);

        assertNotNull(projects);
        assertEquals(2, projects.getTotalElements());
        verify(projectGroupRepository).findProjectsByGroupId(groupId, pageable);
    }

    @Test
    void shouldFindProjectGroupsByUserAndProject() {
        Long userId = 1L;
        Long projectId = 2L;
        List<ProjectGroup> expectedProjectGroups = Arrays.asList(new ProjectGroup(), new ProjectGroup());

        when(projectGroupRepository.findProjectGroupsByUserAndProject(userId, projectId)).thenReturn(expectedProjectGroups);

        List<ProjectGroup> projectGroups = projectGroupService.findProjectGroupsByUserAndProject(userId, projectId);

        assertNotNull(projectGroups);
        assertEquals(expectedProjectGroups.size(), projectGroups.size());
        verify(projectGroupRepository).findProjectGroupsByUserAndProject(userId, projectId);
    }

}
