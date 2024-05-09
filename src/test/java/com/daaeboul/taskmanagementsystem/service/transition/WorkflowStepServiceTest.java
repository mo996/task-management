package com.daaeboul.taskmanagementsystem.service.transition;


import com.daaeboul.taskmanagementsystem.exceptions.transition.workflowStep.WorkflowStepNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.repository.transition.WorkflowStepRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowStepServiceTest {

    @InjectMocks
    private WorkflowStepService workflowStepService;

    @Mock
    private WorkflowStepRepository workflowStepRepository;

    private Workflow workflow;
    private Status status1;
    private Status status2;
    private WorkflowStep step1;
    private WorkflowStep step2;

    @BeforeEach
    void setUp() {
        workflow = new Workflow();
        workflow.setName("Test Workflow");
        ReflectionTestUtils.setField(workflow, "id", 1L);

        status1 = new Status("Status 1", "Description 1");
        status2 = new Status("Status 2", "Description 2");
        ReflectionTestUtils.setField(status1, "id", 1L);
        ReflectionTestUtils.setField(status2, "id", 2L);

        step1 = createWorkflowStep(workflow, status1, 1);
        step2 = createWorkflowStep(workflow, status2, 2);
        ReflectionTestUtils.setField(step1.getId(), "workflowId", 1L);
        ReflectionTestUtils.setField(step1.getId(), "statusId", 1L);
        ReflectionTestUtils.setField(step2.getId(), "workflowId", 1L);
        ReflectionTestUtils.setField(step2.getId(), "statusId", 2L);
    }

    private WorkflowStep createWorkflowStep(Workflow workflow, Status status, int sequenceNumber) {
        WorkflowStep step = new WorkflowStep();
        WorkflowStep.WorkflowStepId workflowStepId = new WorkflowStep.WorkflowStepId();
        workflowStepId.setWorkflowId(workflow.getId());
        workflowStepId.setStatusId(status.getId());
        step.setId(workflowStepId);
        step.setWorkflow(workflow);
        step.setStatus(status);
        step.setSequenceNumber(sequenceNumber);
        return step;
    }

    @Test
    void createWorkflowStep_shouldCreateWorkflowStepSuccessfully() {
        when(workflowStepRepository.save(any(WorkflowStep.class))).thenReturn(step1);

        WorkflowStep createdStep = workflowStepService.createWorkflowStep(step1);

        assertThat(createdStep).isEqualTo(step1);
        verify(workflowStepRepository).save(step1);
    }

    @Test
    void findWorkflowStepById_shouldReturnWorkflowStepIfFound() {
        when(workflowStepRepository.findById(step1.getId())).thenReturn(Optional.of(step1));

        Optional<WorkflowStep> foundStep = workflowStepService.findWorkflowStepById(step1.getId());

        assertThat(foundStep).isPresent().contains(step1);
    }

    @Test
    void findWorkflowStepById_shouldReturnEmptyOptionalIfNotFound() {
        WorkflowStep.WorkflowStepId nonExistentId = new WorkflowStep.WorkflowStepId();
        nonExistentId.setWorkflowId(999L);
        nonExistentId.setStatusId(999L);
        when(workflowStepRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<WorkflowStep> foundStep = workflowStepService.findWorkflowStepById(nonExistentId);

        assertThat(foundStep).isEmpty();
    }

    @Test
    void findWorkflowStepsByWorkflowIdAndStatusName_returnsWorkflowStepsWithMatchingCriteria() {
        when(workflowStepRepository.findByWorkflowIdAndStatusName(workflow.getId(), "Status 1"))
                .thenReturn(List.of(step1));

        List<WorkflowStep> foundSteps = workflowStepService.findWorkflowStepsByWorkflowIdAndStatusName(workflow.getId(), "Status 1");

        assertThat(foundSteps).hasSize(1);
        assertThat(foundSteps.get(0)).isEqualTo(step1);
    }

    @Test
    void findWorkflowStepsByWorkflowIdAndStatusName_returnsEmptyListForNonMatchingCriteria() {
        when(workflowStepRepository.findByWorkflowIdAndStatusName(workflow.getId(), "Nonexistent Status"))
                .thenReturn(List.of());

        List<WorkflowStep> foundSteps = workflowStepService.findWorkflowStepsByWorkflowIdAndStatusName(workflow.getId(), "Nonexistent Status");

        assertThat(foundSteps).isEmpty();
    }

    @Test
    void findWorkflowStepsByWorkflowNameAndStatusName_returnsWorkflowStepsWithMatchingCriteria() {
        when(workflowStepRepository.findByWorkflowNameAndStatusName("Test Workflow", "Status 2"))
                .thenReturn(List.of(step2));

        List<WorkflowStep> foundSteps = workflowStepService.findWorkflowStepsByWorkflowNameAndStatusName("Test Workflow", "Status 2");

        assertThat(foundSteps).hasSize(1);
        assertThat(foundSteps.get(0).getId()).isEqualTo(step2.getId());
        assertThat(foundSteps.get(0).getStatus().getStatusName()).isEqualTo(step2.getStatus().getStatusName());
    }

    @Test
    void findWorkflowStepsByWorkflowNameAndStatusName_returnsEmptyListForNonMatchingCriteria() {
        when(workflowStepRepository.findByWorkflowNameAndStatusName("Nonexistent Workflow", "Status 1"))
                .thenReturn(List.of());

        List<WorkflowStep> foundSteps = workflowStepService.findWorkflowStepsByWorkflowNameAndStatusName("Nonexistent Workflow", "Status 1");

        assertThat(foundSteps).isEmpty();
    }

    @Test
    void findAllWorkflowStepsByWorkflowId_returnsAllWorkflowStepsForGivenWorkflow() {
        when(workflowStepRepository.findAllByWorkflowId(workflow.getId()))
                .thenReturn(List.of(step1, step2));

        List<WorkflowStep> foundSteps = workflowStepService.findAllWorkflowStepsByWorkflowId(workflow.getId());

        assertThat(foundSteps).extracting(WorkflowStep::getId).containsExactlyInAnyOrder(step1.getId(), step2.getId());
    }

    @Test
    void findAllWorkflowStepsByWorkflowId_returnsEmptyListForNonexistentWorkflow() {
        when(workflowStepRepository.findAllByWorkflowId(-1L))
                .thenReturn(List.of());

        List<WorkflowStep> foundSteps = workflowStepService.findAllWorkflowStepsByWorkflowId(-1L);

        assertThat(foundSteps).isEmpty();
    }

    @Test
    void findAllWorkflowStepsByWorkflowIdWithPageable_returnsPaginatedWorkflowSteps() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<WorkflowStep> foundStepsPage = new PageImpl<>(List.of(step1), pageable, 2);
        when(workflowStepRepository.findAllByWorkflowId(workflow.getId(), pageable)).thenReturn(foundStepsPage);

        Page<WorkflowStep> result = workflowStepService.findAllWorkflowStepsByWorkflowId(workflow.getId(), pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void updateWorkflowStep_shouldUpdateWorkflowStepSuccessfully() {
        WorkflowStep updatedStep = new WorkflowStep();
        updatedStep.setId(step1.getId());
        updatedStep.setSequenceNumber(3);

        when(workflowStepRepository.findById(step1.getId())).thenReturn(Optional.of(step1));
        when(workflowStepRepository.save(any(WorkflowStep.class))).thenReturn(updatedStep);

        WorkflowStep result = workflowStepService.updateWorkflowStep(updatedStep);

        assertThat(result).isEqualTo(updatedStep);
        verify(workflowStepRepository).save(any(WorkflowStep.class));
    }

    @Test
    void updateWorkflowStep_shouldThrowExceptionIfWorkflowStepNotFound() {
        WorkflowStep.WorkflowStepId nonExistentId = new WorkflowStep.WorkflowStepId();
        nonExistentId.setWorkflowId(999L);
        nonExistentId.setStatusId(999L);
        WorkflowStep updatedStep = new WorkflowStep();
        updatedStep.setId(nonExistentId);

        when(workflowStepRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(WorkflowStepNotFoundException.class, () -> workflowStepService.updateWorkflowStep(updatedStep));
        verify(workflowStepRepository, never()).save(any());
    }

    @Test
    void deleteWorkflowStep_shouldDeleteWorkflowStepSuccessfully() {
        when(workflowStepRepository.existsById(step1.getId())).thenReturn(true);

        workflowStepService.deleteWorkflowStep(step1.getId());

        verify(workflowStepRepository).deleteById(step1.getId());
    }

    @Test
    void deleteWorkflowStep_shouldThrowExceptionIfWorkflowStepNotFound() {
        WorkflowStep.WorkflowStepId nonExistentId = new WorkflowStep.WorkflowStepId();
        nonExistentId.setWorkflowId(999L);
        nonExistentId.setStatusId(999L);
        when(workflowStepRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(WorkflowStepNotFoundException.class, () -> workflowStepService.deleteWorkflowStep(nonExistentId));
        verify(workflowStepRepository, never()).deleteById(any());
    }
}
