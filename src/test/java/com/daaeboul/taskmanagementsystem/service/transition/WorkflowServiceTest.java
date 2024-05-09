package com.daaeboul.taskmanagementsystem.service.transition;

import com.daaeboul.taskmanagementsystem.exceptions.transition.workflow.DuplicateWorkflowNameException;
import com.daaeboul.taskmanagementsystem.exceptions.transition.workflow.WorkflowNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.repository.transition.WorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private WorkflowRepository workflowRepository;

    @InjectMocks
    private WorkflowService workflowService;

    private Workflow workflow1;
    private Workflow workflow2;
    private Workflow workflow3;

    @BeforeEach
    void setUp() {
        workflow1 = new Workflow();
        workflow1.setName("Workflow 1");
        workflow1.setDescription("Description for Workflow 1");

        workflow2 = new Workflow();
        workflow2.setName("Workflow 2");
        workflow2.setDescription("Description for Workflow 2");

        workflow3 = new Workflow();
        workflow3.setName("Workflow 3");
        workflow3.setDescription("Description for Workflow 3");

        ReflectionTestUtils.setField(workflow1, "id", 1L);
        ReflectionTestUtils.setField(workflow2, "id", 2L);
        ReflectionTestUtils.setField(workflow3, "id", 3L);
    }

    @Test
    void createWorkflow_shouldCreateWorkflowSuccessfully() {
        given(workflowRepository.existsByName(workflow1.getName())).willReturn(false);
        given(workflowRepository.save(workflow1)).willReturn(workflow1);

        Workflow createdWorkflow = workflowService.createWorkflow(workflow1);

        assertThat(createdWorkflow).isEqualTo(workflow1);
        verify(workflowRepository).save(workflow1);
    }

    @Test
    void createWorkflow_shouldThrowExceptionForDuplicateWorkflowName() {
        given(workflowRepository.existsByName(workflow1.getName())).willReturn(true);

        assertThatThrownBy(() -> workflowService.createWorkflow(workflow1))
                .isInstanceOf(DuplicateWorkflowNameException.class)
                .hasMessageContaining("Workflow name already exists");
        verify(workflowRepository, never()).save(any());
    }

    @Test
    void findWorkflowById_shouldReturnWorkflowIfFound() {
        given(workflowRepository.findById(workflow1.getId())).willReturn(Optional.of(workflow1));

        Optional<Workflow> foundWorkflow = workflowService.findWorkflowById(workflow1.getId());

        assertThat(foundWorkflow).isPresent().contains(workflow1);
    }

    @Test
    void findWorkflowById_shouldReturnEmptyOptionalIfNotFound() {
        given(workflowRepository.findById(100L)).willReturn(Optional.empty());

        Optional<Workflow> foundWorkflow = workflowService.findWorkflowById(100L);

        assertThat(foundWorkflow).isEmpty();
    }

    @Test
    void findAllWorkflows_shouldReturnAllWorkflowsIncludingDeleted() {
        given(workflowRepository.findAllIncludingDeleted()).willReturn(List.of(workflow1, workflow2, workflow3));

        List<Workflow> allWorkflows = workflowService.findAllWorkflows();

        assertThat(allWorkflows).containsExactly(workflow1, workflow2, workflow3);
    }

    @Test
    void findAllWorkflows_withPageable_shouldReturnPagedWorkflowsIncludingDeleted() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Workflow> pagedWorkflows = new PageImpl<>(List.of(workflow1, workflow2), pageable, 3);
        given(workflowRepository.findAll(pageable)).willReturn(pagedWorkflows);

        Page<Workflow> result = workflowService.findAllWorkflows(pageable);

        assertThat(result).isEqualTo(pagedWorkflows);
    }

    @Test
    void findWorkflowsByNameContaining_shouldReturnMatchingWorkflowsIncludingDeleted() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Workflow> pagedWorkflows = new PageImpl<>(List.of(workflow1, workflow2), pageable, 3);
        given(workflowRepository.findAllByNameContainingIgnoreCase("flow 1", pageable)).willReturn(pagedWorkflows);

        Page<Workflow> result = workflowService.findWorkflowsByNameContaining("flow 1", pageable);

        assertThat(result).isEqualTo(pagedWorkflows);
    }

    @Test
    void findWorkflowsByName_returnsWorkflowsWithExactNameIncludingDeleted() {
        when(workflowRepository.findByName("Workflow 1")).thenReturn(List.of(workflow1));

        List<Workflow> foundWorkflows = workflowService.findWorkflowsByName("Workflow 1");

        assertThat(foundWorkflows).containsExactly(workflow1);

        verify(workflowRepository).findByName("Workflow 1");
    }

    @Test
    void updateWorkflow_shouldUpdateWorkflowSuccessfully() {
        Workflow updatedWorkflow = new Workflow();
        updatedWorkflow.setName("Updated Workflow");
        updatedWorkflow.setDescription("Updated description");
        ReflectionTestUtils.setField(updatedWorkflow, "id", workflow1.getId());

        given(workflowRepository.findById(workflow1.getId())).willReturn(Optional.of(workflow1));
        given(workflowRepository.save(any(Workflow.class))).willReturn(updatedWorkflow);

        Workflow result = workflowService.updateWorkflow(updatedWorkflow);

        assertThat(result).isEqualTo(updatedWorkflow);
        verify(workflowRepository).save(any(Workflow.class));
    }

    @Test
    void updateWorkflow_shouldThrowExceptionIfWorkflowNotFound() {
        Workflow updatedWorkflow = new Workflow();
        updatedWorkflow.setName("Updated Workflow");
        updatedWorkflow.setDescription("Updated description");
        ReflectionTestUtils.setField(updatedWorkflow, "id", 100L);

        given(workflowRepository.findById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> workflowService.updateWorkflow(updatedWorkflow))
                .isInstanceOf(WorkflowNotFoundException.class)
                .hasMessageContaining("Workflow not found");
        verify(workflowRepository, never()).save(any());
    }

    @Test
    void deleteWorkflow_shouldDeleteWorkflowSuccessfully() {
        given(workflowRepository.existsById(workflow1.getId())).willReturn(true);

        workflowService.deleteWorkflow(workflow1.getId());

        verify(workflowRepository).deleteById(workflow1.getId());
    }

    @Test
    void deleteWorkflow_shouldThrowExceptionIfWorkflowNotFound() {
        given(workflowRepository.existsById(100L)).willReturn(false);

        assertThatThrownBy(() -> workflowService.deleteWorkflow(100L))
                .isInstanceOf(WorkflowNotFoundException.class)
                .hasMessageContaining("Workflow not found");
        verify(workflowRepository, never()).deleteById(any());
    }

    @Test
    void findWorkflowsWithAtLeastOneStep_shouldReturnWorkflowsWithSteps() {
        given(workflowRepository.findWithAtLeastOneStep()).willReturn(List.of(workflow1, workflow2));

        List<Workflow> result = workflowService.findWorkflowsWithAtLeastOneStep();

        assertThat(result).containsExactly(workflow1, workflow2);
    }
}