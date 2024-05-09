package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskAttachment;
import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import com.daaeboul.taskmanagementsystem.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskAttachmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    private Task task;
    private TaskAttachment attachment1;
    private TaskAttachment attachment2;

    @BeforeEach
    void setUp() {

        User assignee = new User();
        assignee.setUsername("assigneeUsername");
        assignee = entityManager.persistFlushFind(assignee);

        Project project = new Project();
        project.setProjectName("Example Project");
        project = entityManager.persistFlushFind(project);

        TaskType taskType = new TaskType();
        taskType.setTaskTypeName("testBug");
        taskType = entityManager.persistFlushFind(taskType);

        Workflow workflow = new Workflow();
        workflow.setName("testDefault Workflow");
        workflow = entityManager.persistFlushFind(workflow);

        ProjectTaskType projectTaskType = new ProjectTaskType();
        ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId(project.getId(), taskType.getId());
        projectTaskType.setId(id);
        projectTaskType.setProject(project);
        projectTaskType.setTaskType(taskType);
        projectTaskType.setWorkflow(workflow);
        entityManager.persistAndFlush(projectTaskType);

        TaskPriority priority = new TaskPriority();
        priority.setPriorityName("testHigh");
        priority = entityManager.persistFlushFind(priority);

        Status taskStatus = new Status();
        taskStatus.setStatusName("testInProgress");
        taskStatus = entityManager.persistFlushFind(taskStatus);

        task = new Task();
        task.setTaskTitle("Test Task");
        task.setProject(project);
        task.setPriority(priority);
        task.setTaskType(taskType);
        task.setAssignee(assignee);
        task.setStatus(taskStatus);
        task.setTaskTitle("Sample Task");
        task.setTaskDescription("This is a sample task.");
        task.setTaskDueDate(LocalDate.now().plusDays(7));
        task = entityManager.persistFlushFind(task);
        entityManager.persist(task);

        attachment1 = new TaskAttachment();
        attachment1.setTask(task);
        attachment1.setFileName("attachment1.txt");
        attachment1.setFileType("text/plain");
        attachment1.setFileSize(1024L);
        entityManager.persist(attachment1);

        attachment2 = new TaskAttachment();
        attachment2.setTask(task);
        attachment2.setFileName("attachment2.pdf");
        attachment2.setFileType("application/pdf");
        attachment2.setFileSize(2048L);
        entityManager.persist(attachment2);

        entityManager.flush();
    }

    @Test
    void findByTaskId_shouldReturnAttachmentsForTheGivenTask() {
        List<TaskAttachment> attachments = taskAttachmentRepository.findByTaskId(task.getId());
        assertThat(attachments).containsExactlyInAnyOrder(attachment1, attachment2);
    }

    @Test
    void findById_shouldReturnAttachmentIfFound() {
        Optional<TaskAttachment> foundAttachment = taskAttachmentRepository.findById(attachment1.getId());
        assertThat(foundAttachment).isPresent();
        assertThat(foundAttachment.get()).isEqualTo(attachment1);
    }

    @Test
    void findById_shouldReturnEmptyOptionalIfNotFound() {
        Optional<TaskAttachment> foundAttachment = taskAttachmentRepository.findById(-1L);
        assertThat(foundAttachment).isEmpty();
    }

    @Test
    void findByFileNameIgnoreCase_shouldReturnAttachmentsWithMatchingFileName() {
        List<TaskAttachment> attachments = taskAttachmentRepository.findByFileNameIgnoreCase("attachment1.txt");
        assertThat(attachments).containsExactly(attachment1);
    }

    @Test
    void findByFileNameIgnoreCase_shouldReturnEmptyListIfNoMatchingFileName() {
        List<TaskAttachment> attachments = taskAttachmentRepository.findByFileNameIgnoreCase("nonexistent.file");
        assertThat(attachments).isEmpty();
    }

    @Test
    void findByFileTypeIgnoreCase_shouldReturnAttachmentsWithMatchingFileType() {
        List<TaskAttachment> attachments = taskAttachmentRepository.findByFileTypeIgnoreCase("application/pdf");
        assertThat(attachments).containsExactly(attachment2);
    }

    @Test
    void findByFileTypeIgnoreCase_shouldReturnEmptyListIfNoMatchingFileType() {
        List<TaskAttachment> attachments = taskAttachmentRepository.findByFileTypeIgnoreCase("image/png");
        assertThat(attachments).isEmpty();
    }

    @Test
    void existsById_shouldReturnTrueIfAttachmentExists() {
        boolean exists = taskAttachmentRepository.existsById(attachment1.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalseIfAttachmentDoesNotExist() {
        boolean exists = taskAttachmentRepository.existsById(-1L);
        assertThat(exists).isFalse();
    }
}