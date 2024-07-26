package com.daaeboul.taskmanagementsystem.controller.privileges;



import com.daaeboul.taskmanagementsystem.config.SecurityConfig;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.service.privileges.GroupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
@Import(SecurityConfig.class)
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateGroup() throws Exception {
        Group group = new Group();
        group.setGroupName("Test Group");
        group.setDescription("Test Description");

        when(groupService.createGroup(any(Group.class))).thenReturn(group);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"groupName\": \"Test Group\", \"description\": \"Test Description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.groupName").value("Test Group"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetGroupById() throws Exception {
        Group group = new Group();
        ReflectionTestUtils.setField(group, "id", 1L);
        group.setGroupName("Test Group");
        group.setDescription("Test Description");

        when(groupService.findGroupById(1L)).thenReturn(Optional.of(group));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("Test Group"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetGroupByIdNotFound() throws Exception {
        when(groupService.findGroupById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/groups/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateGroup() throws Exception {
        Group group = new Group();
        ReflectionTestUtils.setField(group, "id", 1L);
        group.setGroupName("Updated Group");
        group.setDescription("Updated Description");

        when(groupService.updateGroup(any(Group.class))).thenReturn(Optional.of(group));
        when(groupService.findGroupById(1L)).thenReturn(Optional.of(group)); // Add this line

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/groups/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"groupName\": \"Updated Group\", \"description\": \"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("Updated Group"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteGroup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/groups/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(groupService).deleteGroup(1L);
    }
}