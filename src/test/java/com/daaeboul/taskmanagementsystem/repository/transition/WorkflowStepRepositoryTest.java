package com.daaeboul.taskmanagementsystem.repository.transition;

import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WorkflowStepRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkflowStepRepository workflowStepRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private StatusRepository statusRepository;

    private Workflow workflow;
    private Status status1;
    private Status status2;
    private WorkflowStep step1;
    private WorkflowStep step2;

    @BeforeEach
    void setUp() {
        workflow = new Workflow();
        workflow.setName("Test Workflow");
        workflowRepository.save(workflow);

        status1 = statusRepository.save(new Status("Status 1", "Description 1"));
        status2 = statusRepository.save(new Status("Status 2", "Description 2"));
        step1 = createWorkflowStep(workflow, status1, 1);
        step2 = createWorkflowStep(workflow, status2, 2);
        workflowStepRepository.save(step1);
        workflowStepRepository.save(step2);
    }


    private WorkflowStep createWorkflowStep() {
        Workflow workflow = new Workflow();
        workflow.setName("Sample Workflow Name");

        Status status = new Status();
        status.setStatusName("Sample Status Name");

        entityManager.persist(workflow);
        entityManager.persist(status);

        WorkflowStep workflowStep = new WorkflowStep();
        WorkflowStep.WorkflowStepId id = new WorkflowStep.WorkflowStepId();
        id.setWorkflowId(workflow.getId());
        id.setStatusId(status.getId());
        workflowStep.setId(id);
        workflowStep.setWorkflow(workflow);
        workflowStep.setStatus(status);
        workflowStep.setSequenceNumber(1);

        return entityManager.persistFlushFind(workflowStep);
    }

    @Test
    public void whenCreateWorkflowStep_thenWorkflowStepIsCreated() {
        WorkflowStep workflowStep = createWorkflowStep();
        assertThat(workflowStep).isNotNull();
    }

    @Test
    public void whenFindById_thenReturnWorkflowStep() {
        WorkflowStep workflowStep = createWorkflowStep();
        Optional<WorkflowStep> found = workflowStepRepository.findById(workflowStep.getId());
        assertThat(found).isPresent().contains(workflowStep);
    }

    @Test
    public void whenFindAll_thenReturnAllWorkflowSteps() {
        createWorkflowStep();
        List<WorkflowStep> workflowSteps = workflowStepRepository.findAll();
        assertThat(workflowSteps).isNotEmpty();
    }

    @Test
    public void whenUpdateWorkflowStep_thenWorkflowStepIsUpdated() {
        WorkflowStep workflowStep = createWorkflowStep();
        workflowStep.setSequenceNumber(2);
        WorkflowStep updatedWorkflowStep = workflowStepRepository.save(workflowStep);

        assertThat(updatedWorkflowStep.getSequenceNumber()).isEqualTo(2);
    }

    @Test
    public void whenDeleteWorkflowStep_thenWorkflowStepIsDeleted() {
        WorkflowStep workflowStep = createWorkflowStep();
        workflowStepRepository.delete(workflowStep);
        Optional<WorkflowStep> deleted = workflowStepRepository.findById(workflowStep.getId());
        assertThat(deleted).isEmpty();
    }

    // Helper method to create WorkflowStep entities
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
    void findByWorkflowIdAndStatusName_returnsWorkflowStepsWithMatchingCriteria() {
        List<WorkflowStep> foundSteps = workflowStepRepository.findByWorkflowIdAndStatusName(workflow.getId(), "Status 1");
        assertThat(foundSteps).hasSize(1);
        assertThat(foundSteps.get(0)).isEqualTo(step1);
    }

    @Test
    void findByWorkflowIdAndStatusName_returnsEmptyListForNonMatchingCriteria() {
        List<WorkflowStep> foundSteps = workflowStepRepository.findByWorkflowIdAndStatusName(workflow.getId(), "Nonexistent Status");
        assertThat(foundSteps).isEmpty();
    }

    @Test
    void findByWorkflowNameAndStatusName_returnsWorkflowStepsWithMatchingCriteria() {
        List<WorkflowStep> foundSteps = workflowStepRepository.findByWorkflowNameAndStatusName("Test Workflow", "Status 2");
        assertThat(foundSteps).hasSize(1);
        assertThat(foundSteps.get(0).getId()).isEqualTo(step2.getId());
        assertThat(foundSteps.get(0).getStatus().getStatusName()).isEqualTo(step2.getStatus().getStatusName());
    }

    @Test
    void findByWorkflowNameAndStatusName_returnsEmptyListForNonMatchingCriteria() {
        List<WorkflowStep> foundSteps = workflowStepRepository.findByWorkflowNameAndStatusName("Nonexistent Workflow", "Status 1");
        assertThat(foundSteps).isEmpty();
    }

    @Test
    void findAllByWorkflowId_returnsAllWorkflowStepsForGivenWorkflow() {
        List<WorkflowStep> foundSteps = workflowStepRepository.findAllByWorkflowId(workflow.getId());
        assertThat(foundSteps).extracting(WorkflowStep::getId).containsExactlyInAnyOrder(step1.getId(), step2.getId());
    }

    @Test
    void findAllByWorkflowId_returnsEmptyListForNonexistentWorkflow() {
        List<WorkflowStep> foundSteps = workflowStepRepository.findAllByWorkflowId(-1L);
        assertThat(foundSteps).isEmpty();
    }

    @Test
    void findAllByWorkflowIdWithPageable_returnsPaginatedWorkflowSteps() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<WorkflowStep> foundStepsPage = workflowStepRepository.findAllByWorkflowId(workflow.getId(), pageable);

        assertThat(foundStepsPage.getTotalElements()).isEqualTo(2);
        assertThat(foundStepsPage.getContent()).hasSize(1); // Only one step in the first page
    }

    @Test
    void countByWorkflowId_returnsCorrectCount() {
        long count = workflowStepRepository.countByWorkflowId(workflow.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    void countByWorkflowId_returnsZeroForNonexistentWorkflow() {
        long count = workflowStepRepository.countByWorkflowId(-1L);
        assertThat(count).isEqualTo(0);
    }
}
