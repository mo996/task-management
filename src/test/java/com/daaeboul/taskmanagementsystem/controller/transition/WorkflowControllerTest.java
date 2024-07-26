package com.daaeboul.taskmanagementsystem.controller.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.workflow.DuplicateWorkflowNameException;
import com.daaeboul.taskmanagementsystem.exceptions.transition.workflow.WorkflowNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.service.transition.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkflowController.class)
public class WorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowService workflowService;

    private Workflow mockWorkflow;

    @BeforeEach
    public void setup() {
        mockWorkflow = new Workflow();
        ReflectionTestUtils.setField(mockWorkflow, "id", 1L);
        mockWorkflow.setName("Test Workflow");

        // Ensure the mock returns the workflow when searched by ID
        Mockito.when(workflowService.findWorkflowById(1L)).thenReturn(Optional.of(mockWorkflow));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateWorkflow() throws Exception {
        Mockito.when(workflowService.createWorkflow(any(Workflow.class))).thenReturn(mockWorkflow);

        mockMvc.perform(post("/api/v1/workflows")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Workflow\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Workflow"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateWorkflow_DuplicateName() throws Exception {
        Mockito.when(workflowService.createWorkflow(any(Workflow.class)))
                .thenThrow(new DuplicateWorkflowNameException("Workflow name already exists"));

        mockMvc.perform(post("/api/v1/workflows")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Workflow\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindWorkflowById() throws Exception {
        Mockito.when(workflowService.findWorkflowById(anyLong())).thenReturn(Optional.of(mockWorkflow));

        mockMvc.perform(get("/api/v1/workflows/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Workflow"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindWorkflowById_NotFound() throws Exception {
        Mockito.when(workflowService.findWorkflowById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/workflows/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllWorkflows() throws Exception {
        List<Workflow> workflows = Arrays.asList(mockWorkflow);
        Mockito.when(workflowService.findAllWorkflows()).thenReturn(workflows);

        mockMvc.perform(get("/api/v1/workflows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Workflow"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateWorkflow() throws Exception {
        // Prepare the updated workflow with the expected name
        Workflow updatedWorkflow = new Workflow();
        ReflectionTestUtils.setField(updatedWorkflow, "id", 1L);
        updatedWorkflow.setName("Updated Workflow");

        // Mock the update workflow service to return the updated workflow
        Mockito.when(workflowService.updateWorkflow(any(Workflow.class))).thenReturn(updatedWorkflow);

        // Perform the update request
        mockMvc.perform(put("/api/v1/workflows/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Workflow\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Workflow"));  // Check for the updated name
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateWorkflow_NotFound() throws Exception {
        Mockito.when(workflowService.updateWorkflow(any(Workflow.class)))
                .thenThrow(new WorkflowNotFoundException("Workflow not found"));

        mockMvc.perform(put("/api/v1/workflows/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Workflow\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteWorkflow() throws Exception {
        Mockito.doNothing().when(workflowService).deleteWorkflow(anyLong());

        mockMvc.perform(delete("/api/v1/workflows/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteWorkflow_NotFound() throws Exception {
        Mockito.doThrow(new WorkflowNotFoundException("Workflow not found")).when(workflowService).deleteWorkflow(anyLong());

        mockMvc.perform(delete("/api/v1/workflows/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindWorkflowsWithAtLeastOneStep() throws Exception {
        List<Workflow> workflows = Arrays.asList(mockWorkflow);
        Mockito.when(workflowService.findWorkflowsWithAtLeastOneStep()).thenReturn(workflows);

        mockMvc.perform(get("/api/v1/workflows/with-steps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Workflow"));
    }
}
