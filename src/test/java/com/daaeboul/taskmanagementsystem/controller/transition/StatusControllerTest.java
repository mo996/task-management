package com.daaeboul.taskmanagementsystem.controller.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.status.DuplicateStatusNameException;
import com.daaeboul.taskmanagementsystem.exceptions.transition.status.StatusNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.service.transition.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatusController.class)
public class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatusService statusService;

    private Status mockStatus;

    @BeforeEach
    public void setup() {
        mockStatus = new Status();
        mockStatus.setStatusName("In Progress");

        Mockito.when(statusService.createStatus(any(Status.class))).thenReturn(mockStatus);
        Mockito.when(statusService.findStatusById(anyLong())).thenReturn(Optional.of(mockStatus));
        Mockito.when(statusService.updateStatus(any(Status.class))).thenReturn(mockStatus);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateStatus() throws Exception {
        mockMvc.perform(post("/api/v1/statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusName\": \"In Progress\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("In Progress"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateStatus_DuplicateName() throws Exception {
        Mockito.when(statusService.createStatus(any(Status.class)))
                .thenThrow(new DuplicateStatusNameException("Status name already exists: In Progress"));

        mockMvc.perform(post("/api/v1/statuses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusName\": \"In Progress\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindStatusById() throws Exception {
        mockMvc.perform(get("/api/v1/statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("In Progress"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindStatusById_NotFound() throws Exception {
        Mockito.when(statusService.findStatusById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/statuses/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllStatuses() throws Exception {
        Status status1 = new Status();
        status1.setStatusName("In Progress");

        Status status2 = new Status();
        status2.setStatusName("Completed");

        List<Status> statuses = Arrays.asList(status1, status2);

        Mockito.when(statusService.findAllStatuses()).thenReturn(statuses);

        mockMvc.perform(get("/api/v1/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statusName").value("In Progress"))
                .andExpect(jsonPath("$[1].statusName").value("Completed"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateStatus() throws Exception {
        mockMvc.perform(put("/api/v1/statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusName\": \"Updated In Progress\", \"description\": \"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("Updated In Progress"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateStatus_NotFound() throws Exception {
        Mockito.when(statusService.findStatusById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusName\": \"Updated In Progress\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateStatus_DuplicateName() throws Exception {
        Mockito.when(statusService.updateStatus(any(Status.class)))
                .thenThrow(new DuplicateStatusNameException("Status name already exists: In Progress"));

        mockMvc.perform(put("/api/v1/statuses/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusName\": \"In Progress\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteStatus() throws Exception {
        mockMvc.perform(delete("/api/v1/statuses/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteStatus_NotFound() throws Exception {
        Mockito.doThrow(new StatusNotFoundException("Status not found")).when(statusService).deleteStatus(anyLong());

        mockMvc.perform(delete("/api/v1/statuses/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindStatusesCreatedBefore() throws Exception {
        Status status1 = new Status();
        status1.setStatusName("In Progress");

        Status status2 = new Status();
        status2.setStatusName("Completed");

        List<Status> statuses = Arrays.asList(status1, status2);

        Mockito.when(statusService.findStatusesCreatedBefore(any(LocalDateTime.class))).thenReturn(statuses);

        mockMvc.perform(get("/api/v1/statuses/created-before")
                        .param("dateTime", "2023-01-01T00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statusName").value("In Progress"))
                .andExpect(jsonPath("$[1].statusName").value("Completed"));
    }
}
