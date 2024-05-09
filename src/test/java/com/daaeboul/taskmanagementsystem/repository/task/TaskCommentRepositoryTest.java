package com.daaeboul.taskmanagementsystem.repository.task;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskComment;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskCommentRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    private Task task;
    private User user;
    private TaskComment comment1;
    private TaskComment comment2;

    @BeforeEach
    void setUp() {
        this.user = new User();
        this.user.setUsername("testUser");
        this.user = entityManager.persistFlushFind(this.user);

        Project project = new Project();
        project.setProjectName("Test Project Name");
        project = entityManager.persistFlushFind(project);

        TaskType taskType = new TaskType();
        taskType.setTaskTypeName("testBug");
        taskType = entityManager.persistFlushFind(taskType);

        Workflow workflow =  new Workflow();
        workflow.setName("New Workflow");
        workflow = entityManager.persistFlushFind(workflow);

        TaskPriority priority = new TaskPriority();
        priority.setPriorityName("testHigh");
        priority = entityManager.persistFlushFind(priority);

        Status taskStatus = new Status();
        taskStatus.setStatusName("testInProgress");
        taskStatus = entityManager.persistFlushFind(taskStatus);

        ProjectTaskType projectTaskType = new ProjectTaskType();
        ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId(project.getId(), taskType.getId());
        projectTaskType.setId(id);
        projectTaskType.setProject(project);
        projectTaskType.setTaskType(taskType);
        projectTaskType.setWorkflow(workflow);
        entityManager.persistAndFlush(projectTaskType);

        this.task = new Task();
        this.task.setAssignee(this.user);
        this.task.setProject(project);
        this.task.setTaskType(taskType);
        this.task.setPriority(priority);
        this.task.setStatus(taskStatus);
        this.task.setTaskTitle("Sample Task");
        this.task.setTaskDescription("This is a sample task.");
        this.task.setTaskDueDate(LocalDate.now().plusDays(7));
        this.task = entityManager.persistFlushFind(this.task);

        comment1 = new TaskComment();
        comment1.setTask(task);
        comment1.setUser(user);
        comment1.setComment("This is the first comment.");
        entityManager.persist(comment1);

        comment2 = new TaskComment();
        comment2.setTask(task);
        comment2.setUser(user);
        comment2.setComment("This is the second comment.");
        entityManager.persist(comment2);

        entityManager.flush();
    }

    @Test
    void findByTask_shouldReturnCommentsForTheGivenTask() {
        List<TaskComment> comments = taskCommentRepository.findByTask(task);
        assertThat(comments).containsExactlyInAnyOrder(comment1, comment2);
    }

    @Test
    void findByTaskWithPageable_shouldReturnPaginatedCommentsForTheTask() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<TaskComment> commentPage = taskCommentRepository.findByTask(task, pageable);
        assertThat(commentPage.getContent()).hasSize(1);
    }

    @Test
    void findById_shouldReturnCommentIfFound() {
        Optional<TaskComment> foundComment = taskCommentRepository.findById(comment1.getId());
        assertThat(foundComment).isPresent();
        assertThat(foundComment.get()).isEqualTo(comment1);
    }

    @Test
    void findById_shouldReturnEmptyOptionalIfNotFound() {
        Optional<TaskComment> foundComment = taskCommentRepository.findById(-1L);
        assertThat(foundComment).isEmpty();
    }

    @Test
    void findByUser_shouldReturnCommentsMadeByTheGivenUser() {
        List<TaskComment> comments = taskCommentRepository.findByUser(user);
        assertThat(comments).containsExactlyInAnyOrder(comment1, comment2);
    }

    @Test
    void findByUserWithPageable_shouldReturnPaginatedCommentsForTheUser() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<TaskComment> commentPage = taskCommentRepository.findByUser(user, pageable);
        assertThat(commentPage.getContent()).hasSize(1);
    }

    @Test
    void findByCommentContainingIgnoreCase_shouldReturnCommentsContainingKeyword() {
        List<TaskComment> comments = taskCommentRepository.findByCommentContainingIgnoreCase("first");
        assertThat(comments).containsExactly(comment1);
    }

    @Test
    void findByCommentContainingIgnoreCaseWithPageable_shouldReturnPaginatedCommentsContainingKeyword() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<TaskComment> commentPage = taskCommentRepository.findByCommentContainingIgnoreCase("comment", pageable);
        assertThat(commentPage.getContent()).hasSize(1);
    }

    @Test
    void existsById_shouldReturnTrueIfCommentExists() {
        boolean exists = taskCommentRepository.existsById(comment1.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_shouldReturnFalseIfCommentDoesNotExist() {
        boolean exists = taskCommentRepository.existsById(-1L);
        assertThat(exists).isFalse();
    }

    @Test

    void findAllIncludingDeleted_shouldReturnAllCommentsIncludingDeleted() {
        taskCommentRepository.deleteById(comment2.getId());
        entityManager.flush();

        List<TaskComment> allComments = taskCommentRepository.findAllIncludingDeleted();
        assertThat(allComments).hasSize(2);
    }

    @Test
    void findAllByTaskIdIncludingDeleted_shouldReturnAllCommentsForTaskIncludingDeleted() {
        taskCommentRepository.deleteById(comment2.getId());
        entityManager.flush();

        List<TaskComment> allComments = taskCommentRepository.findAllByTaskIdIncludingDeleted(task.getId());
        assertThat(allComments).hasSize(2);
    }
}