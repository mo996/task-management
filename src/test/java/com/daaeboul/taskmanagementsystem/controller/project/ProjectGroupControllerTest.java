package com.daaeboul.taskmanagementsystem.controller.project;

import com.daaeboul.taskmanagementsystem.model.project.ProjectGroup;
import com.daaeboul.taskmanagementsystem.model.project.ProjectGroup.ProjectGroupId;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.project.ProjectGroupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectGroupController.class)
public class ProjectGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectGroupService projectGroupService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateProjectGroup() throws Exception {
        ProjectGroup projectGroup = new ProjectGroup();
        ProjectGroupId id = new ProjectGroupId(1L, 1L);
        projectGroup.setId(id);

        Mockito.when(projectGroupService.createProjectGroup(any(ProjectGroup.class))).thenReturn(projectGroup);

        mockMvc.perform(post("/api/v1/project-groups")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"projectId\": 1, \"groupId\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.groupId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupById() throws Exception {
        ProjectGroup projectGroup = new ProjectGroup();
        ProjectGroupId id = new ProjectGroupId(1L, 1L);
        projectGroup.setId(id);

        Mockito.when(projectGroupService.findProjectGroupById(any(ProjectGroupId.class))).thenReturn(Optional.of(projectGroup));

        mockMvc.perform(get("/api/v1/project-groups/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.groupId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupById_NotFound() throws Exception {
        Mockito.when(projectGroupService.findProjectGroupById(any(ProjectGroupId.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/project-groups/1/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllProjectGroups() throws Exception {
        ProjectGroup projectGroup1 = new ProjectGroup();
        ProjectGroup projectGroup2 = new ProjectGroup();
        List<ProjectGroup> projectGroups = Arrays.asList(projectGroup1, projectGroup2);
        Page<ProjectGroup> page = new PageImpl<>(projectGroups);

        Mockito.when(projectGroupService.findAllProjectGroups(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-groups")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupsByProjectId() throws Exception {
        ProjectGroup projectGroup1 = new ProjectGroup();
        ProjectGroup projectGroup2 = new ProjectGroup();
        List<ProjectGroup> projectGroups = Arrays.asList(projectGroup1, projectGroup2);
        Page<ProjectGroup> page = new PageImpl<>(projectGroups);

        Mockito.when(projectGroupService.findProjectGroupsByProjectId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-groups/project/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupsByGroupId() throws Exception {
        ProjectGroup projectGroup1 = new ProjectGroup();
        ProjectGroup projectGroup2 = new ProjectGroup();
        List<ProjectGroup> projectGroups = Arrays.asList(projectGroup1, projectGroup2);
        Page<ProjectGroup> page = new PageImpl<>(projectGroups);

        Mockito.when(projectGroupService.findProjectGroupsByGroupId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-groups/group/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupsByProjectRoleId() throws Exception {
        ProjectGroup projectGroup1 = new ProjectGroup();
        ProjectGroup projectGroup2 = new ProjectGroup();
        List<ProjectGroup> projectGroups = Arrays.asList(projectGroup1, projectGroup2);
        Page<ProjectGroup> page = new PageImpl<>(projectGroups);

        Mockito.when(projectGroupService.findProjectGroupsByProjectRoleId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-groups/role/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupByProjectIdAndGroupId() throws Exception {
        ProjectGroup projectGroup = new ProjectGroup();
        ProjectGroupId id = new ProjectGroupId(1L, 1L);
        projectGroup.setId(id);

        Mockito.when(projectGroupService.findProjectGroupByProjectIdAndGroupId(anyLong(), anyLong())).thenReturn(projectGroup);

        mockMvc.perform(get("/api/v1/project-groups/project/1/group/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.groupId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupsByProjectName() throws Exception {
        ProjectGroup projectGroup1 = new ProjectGroup();
        ProjectGroup projectGroup2 = new ProjectGroup();
        List<ProjectGroup> projectGroups = Arrays.asList(projectGroup1, projectGroup2);
        Page<ProjectGroup> page = new PageImpl<>(projectGroups);

        Mockito.when(projectGroupService.findProjectGroupsByProjectName(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-groups/project-name/Project")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupsByGroupName() throws Exception {
        ProjectGroup projectGroup1 = new ProjectGroup();
        ProjectGroup projectGroup2 = new ProjectGroup();
        List<ProjectGroup> projectGroups = Arrays.asList(projectGroup1, projectGroup2);
        Page<ProjectGroup> page = new PageImpl<>(projectGroups);

        Mockito.when(projectGroupService.findProjectGroupsByGroupName(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-groups/group-name/Group")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateProjectGroup() throws Exception {
        ProjectGroup projectGroup = new ProjectGroup();
        ProjectGroupId id = new ProjectGroupId(1L, 1L);
        projectGroup.setId(id);

        Mockito.when(projectGroupService.updateProjectGroup(any(ProjectGroup.class))).thenReturn(projectGroup);

        mockMvc.perform(put("/api/v1/project-groups/1/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": {\"projectId\": 1, \"groupId\": 1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.projectId").value(1))
                .andExpect(jsonPath("$.id.groupId").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProjectGroup() throws Exception {
        Mockito.doNothing().when(projectGroupService).deleteProjectGroup(any(ProjectGroupId.class));

        mockMvc.perform(delete("/api/v1/project-groups/1/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProjectGroupsByProjectId() throws Exception {
        Mockito.doNothing().when(projectGroupService).deleteProjectGroupsByProjectId(anyLong());

        mockMvc.perform(delete("/api/v1/project-groups/project/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteProjectGroupsByGroupId() throws Exception {
        Mockito.doNothing().when(projectGroupService).deleteProjectGroupsByGroupId(anyLong());

        mockMvc.perform(delete("/api/v1/project-groups/group/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindUsersByProjectId() throws Exception {
        User user1 = new User();
        User user2 = new User();
        List<User> users = Arrays.asList(user1, user2);
        Page<User> page = new PageImpl<>(users);

        Mockito.when(projectGroupService.findUsersByProjectId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-groups/users-by-project/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectsByGroupId() throws Exception {
        Project project1 = new Project();
        Project project2 = new Project();
        List<Project> projects = Arrays.asList(project1, project2);
        Page<Project> page = new PageImpl<>(projects);

        Mockito.when(projectGroupService.findProjectsByGroupId(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/project-groups/projects-by-group/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupsByProjectIdAndGroupName() throws Exception {
        ProjectGroup projectGroup1 = new ProjectGroup();
        ProjectGroup projectGroup2 = new ProjectGroup();
        List<ProjectGroup> projectGroups = Arrays.asList(projectGroup1, projectGroup2);

        Mockito.when(projectGroupService.findProjectGroupsByProjectIdAndGroupName(anyLong(), anyString())).thenReturn(projectGroups);

        mockMvc.perform(get("/api/v1/project-groups/project/1/group-name/Group"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindProjectGroupsByUserAndProject() throws Exception {
        ProjectGroup projectGroup1 = new ProjectGroup();
        ProjectGroup projectGroup2 = new ProjectGroup();
        List<ProjectGroup> projectGroups = Arrays.asList(projectGroup1, projectGroup2);

        Mockito.when(projectGroupService.findProjectGroupsByUserAndProject(anyLong(), anyLong())).thenReturn(projectGroups);

        mockMvc.perform(get("/api/v1/project-groups/user/1/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
