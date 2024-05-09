package com.daaeboul.taskmanagementsystem.repository.transition;

import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.model.transition.WorkflowStep;
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
public class WorkflowRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private WorkflowStepRepository workflowStepRepository;

    private Workflow workflow;
    private Workflow savedWorkflow1;
    private Workflow savedWorkflow2;
    private Workflow workflow1;
    private Workflow workflow2;

    @BeforeEach
    public void setup() {
        workflow = new Workflow();
        workflow.setName("Development Process");
        workflow.setDescription("The workflow for software development tasks.");

        savedWorkflow1 = new Workflow();
        savedWorkflow1.setName("Test Workflow 1");
        savedWorkflow2 = new Workflow();
        savedWorkflow2.setName("Test Workflow 2");

        workflow1 = new Workflow();
        workflow1.setName("Workflow with Steps");
        workflow2 = new Workflow();
        workflow2.setName("Workflow without Steps");

        entityManager.persist(workflow);
        entityManager.persist(savedWorkflow1);
        entityManager.persist(savedWorkflow2);
        entityManager.persist(workflow1);
        entityManager.persist(workflow2);

        entityManager.flush();
    }

    @Test
    public void whenFindByName_thenReturnWorkflow() {
        List<Workflow> found = workflowRepository.findByName(workflow.getName());

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getName()).isEqualTo(workflow.getName());
    }

    @Test
    public void whenCreateWorkflow_thenWorkflowIsCreated() {
        Workflow newWorkflow = new Workflow();
        newWorkflow.setName("QA Process");
        newWorkflow.setDescription("The workflow for QA tasks.");

        Workflow savedWorkflow = workflowRepository.save(newWorkflow);

        Workflow found = entityManager.find(Workflow.class, savedWorkflow.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(newWorkflow.getName());
    }


    @Test
    public void whenUpdateWorkflow_thenWorkflowIsUpdated() {
        Workflow existingWorkflow = workflowRepository.findByName("Development Process").get(0);
        existingWorkflow.setDescription("Updated description for Development Process.");

        workflowRepository.save(existingWorkflow);
        Workflow updatedWorkflow = entityManager.find(Workflow.class, existingWorkflow.getId());

        assertThat(updatedWorkflow.getDescription()).isEqualTo("Updated description for Development Process.");
    }

    @Test
    public void whenDeleteWorkflow_thenWorkflowIsDeleted() {
        Workflow existingWorkflow = workflowRepository.findByName("Development Process").get(0);
        Long workflowId = existingWorkflow.getId();

        workflowRepository.delete(existingWorkflow);
        Optional<Workflow> deletedWorkflow = workflowRepository.findById(workflowId);

        assertThat(deletedWorkflow).isEmpty();
    }


    @Test
    void findAllByNameContainingIgnoreCase_returnsWorkflowsWithMatchingNamePartIncludingDeleted() {
        workflowRepository.deleteById(workflow.getId());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Workflow> foundWorkflows = workflowRepository.findAllByNameContainingIgnoreCase("Development P", pageable);

        assertThat(foundWorkflows).isNotEmpty();
        assertThat(foundWorkflows.getContent().get(0).getId()).isEqualTo(workflow.getId());
    }

    @Test
    void findAllIncludingDeleted_returnsAllWorkflowsIncludingDeleted() {
        workflowRepository.deleteById(savedWorkflow2.getId());

        List<Workflow> foundWorkflows = workflowRepository.findAllIncludingDeleted();

        assertThat(foundWorkflows).hasSize(5);
    }

    @Test
    void findWithAtLeastOneStep_returnsWorkflowsWithSteps() {
        Status status1 = statusRepository.save(new Status("To Do", "Task is pending"));
        Status status2 = statusRepository.save(new Status("In Progress", "Task is being worked on"));

        WorkflowStep step1 = new WorkflowStep();
        WorkflowStep.WorkflowStepId stepId1 = new WorkflowStep.WorkflowStepId();
        stepId1.setWorkflowId(workflow1.getId());
        stepId1.setStatusId(status1.getId());
        step1.setId(stepId1);
        step1.setWorkflow(workflow1);
        step1.setStatus(status1);
        step1.setSequenceNumber(1);
        workflowStepRepository.save(step1);

        WorkflowStep step2 = new WorkflowStep();
        WorkflowStep.WorkflowStepId stepId2 = new WorkflowStep.WorkflowStepId();
        stepId2.setWorkflowId(workflow1.getId());
        stepId2.setStatusId(status2.getId());
        step2.setId(stepId2);
        step2.setWorkflow(workflow1);
        step2.setStatus(status2);
        step2.setSequenceNumber(2);
        workflowStepRepository.save(step2);

        List<Workflow> workflowsWithSteps = workflowRepository.findWithAtLeastOneStep();

        assertThat(workflowsWithSteps).containsExactly(workflow1);
    }

    @Test
    void existsByName_returnsTrueForExistingWorkflowNameIncludingDeleted() {
        workflowRepository.deleteById(workflow.getId());

        boolean exists = workflowRepository.existsByName("Development Process");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_returnsFalseForNonExistingWorkflowName() {
        boolean exists = workflowRepository.existsByName("Nonexistent Workflow");

        assertThat(exists).isFalse();
    }
}