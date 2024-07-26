package com.daaeboul.taskmanagementsystem.controller.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.workflowStep.WorkflowStepNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep.WorkflowStepId;
import com.daaeboul.taskmanagementsystem.service.transition.WorkflowStepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

@WebMvcTest(WorkflowStepController.class)
public class WorkflowStepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowStepService workflowStepService;

    private WorkflowStep mockWorkflowStep;
    private WorkflowStepId mockWorkflowStepId;

    @BeforeEach
    public void setup() {
        mockWorkflowStepId = new WorkflowStepId();
        mockWorkflowStepId.setWorkflowId(1L);
        mockWorkflowStepId.setStatusId(1L);

        mockWorkflowStep = new WorkflowStep();
        ReflectionTestUtils.setField(mockWorkflowStep, "id", mockWorkflowStepId);
        mockWorkflowStep.setSequenceNumber(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testCreateWorkflowStep() throws Exception {
        Mockito.when(workflowStepService.createWorkflowStep(any(WorkflowStep.class))).thenReturn(mockWorkflowStep);

        mockMvc.perform(post("/api/v1/workflow-steps")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sequenceNumber\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequenceNumber").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindWorkflowStepById() throws Exception {
        Mockito.when(workflowStepService.findWorkflowStepById(any(WorkflowStepId.class))).thenReturn(Optional.of(mockWorkflowStep));

        mockMvc.perform(get("/api/v1/workflow-steps/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequenceNumber").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindWorkflowStepById_NotFound() throws Exception {
        Mockito.when(workflowStepService.findWorkflowStepById(any(WorkflowStepId.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/workflow-steps/1/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllWorkflowStepsByWorkflowId() throws Exception {
        List<WorkflowStep> workflowSteps = Arrays.asList(mockWorkflowStep);
        Mockito.when(workflowStepService.findAllWorkflowStepsByWorkflowId(anyLong())).thenReturn(workflowSteps);

        mockMvc.perform(get("/api/v1/workflow-steps/workflow/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sequenceNumber").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindAllWorkflowStepsByWorkflowIdWithPagination() throws Exception {
        Page<WorkflowStep> page = new PageImpl<>(Arrays.asList(mockWorkflowStep), PageRequest.of(0, 1), 1);
        Mockito.when(workflowStepService.findAllWorkflowStepsByWorkflowId(anyLong(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/workflow-steps/workflow/1/page")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sequenceNumber").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindWorkflowStepsByWorkflowIdAndStatusName() throws Exception {
        List<WorkflowStep> workflowSteps = Arrays.asList(mockWorkflowStep);
        Mockito.when(workflowStepService.findWorkflowStepsByWorkflowIdAndStatusName(anyLong(), anyString())).thenReturn(workflowSteps);

        mockMvc.perform(get("/api/v1/workflow-steps/workflow/1/status/TestStatus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sequenceNumber").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testFindWorkflowStepsByWorkflowNameAndStatusName() throws Exception {
        List<WorkflowStep> workflowSteps = Arrays.asList(mockWorkflowStep);
        Mockito.when(workflowStepService.findWorkflowStepsByWorkflowNameAndStatusName(anyString(), anyString())).thenReturn(workflowSteps);

        mockMvc.perform(get("/api/v1/workflow-steps/workflow-name/TestWorkflow/status/TestStatus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sequenceNumber").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateWorkflowStep() throws Exception {
        // Mock the existing workflow step retrieval
        Mockito.when(workflowStepService.findWorkflowStepById(any(WorkflowStepId.class))).thenReturn(Optional.of(mockWorkflowStep));

        // Mock the update workflow step service to return the updated workflow step
        WorkflowStep updatedWorkflowStep = new WorkflowStep();
        ReflectionTestUtils.setField(updatedWorkflowStep, "id", mockWorkflowStepId);
        updatedWorkflowStep.setSequenceNumber(2);
        Mockito.when(workflowStepService.updateWorkflowStep(any(WorkflowStep.class))).thenReturn(updatedWorkflowStep);

        // Perform the update request
        mockMvc.perform(put("/api/v1/workflow-steps/1/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sequenceNumber\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequenceNumber").value(2));  // Check for the updated sequence number
    }


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUpdateWorkflowStep_NotFound() throws Exception {
        Mockito.when(workflowStepService.updateWorkflowStep(any(WorkflowStep.class)))
                .thenThrow(new WorkflowStepNotFoundException("Workflow step not found"));

        mockMvc.perform(put("/api/v1/workflow-steps/1/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sequenceNumber\": 2}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteWorkflowStep() throws Exception {
        Mockito.doNothing().when(workflowStepService).deleteWorkflowStep(any(WorkflowStepId.class));

        mockMvc.perform(delete("/api/v1/workflow-steps/1/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testDeleteWorkflowStep_NotFound() throws Exception {
        Mockito.doThrow(new WorkflowStepNotFoundException("Workflow step not found")).when(workflowStepService).deleteWorkflowStep(any(WorkflowStepId.class));

        mockMvc.perform(delete("/api/v1/workflow-steps/1/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
