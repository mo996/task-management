package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskComment.TaskCommentNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskComment;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.task.TaskCommentRepository;
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
class TaskCommentServiceTest {

    @Mock
    private TaskCommentRepository taskCommentRepository;

    @InjectMocks
    private TaskCommentService taskCommentService;

    private Task task;
    private User user;
    private TaskComment comment1;
    private TaskComment comment2;

    @BeforeEach
    void setUp() {
        task = new Task();
        user = new User();
        comment1 = new TaskComment();
        comment1.setComment("This is the first comment.");
        comment2 = new TaskComment();
        comment2.setComment("This is the second comment.");

        ReflectionTestUtils.setField(task, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(comment1, "id", 1L);
        ReflectionTestUtils.setField(comment2, "id", 2L);
    }

    @Test
    void createTaskComment_shouldCreateCommentSuccessfully() {
        given(taskCommentRepository.save(comment1)).willReturn(comment1);

        TaskComment createdComment = taskCommentService.createTaskComment(task, user, comment1);

        assertThat(createdComment).isEqualTo(comment1);
        assertThat(createdComment.getTask()).isEqualTo(task);
        assertThat(createdComment.getUser()).isEqualTo(user);
        verify(taskCommentRepository).save(comment1);
    }

    @Test
    void findTaskCommentById_shouldReturnCommentIfFound() {
        given(taskCommentRepository.findById(comment1.getId())).willReturn(Optional.of(comment1));

        Optional<TaskComment> foundComment = taskCommentService.findTaskCommentById(comment1.getId());

        assertThat(foundComment).isPresent().contains(comment1);
    }

    @Test
    void findTaskCommentById_shouldReturnEmptyOptionalIfNotFound() {
        given(taskCommentRepository.findById(100L)).willReturn(Optional.empty());

        Optional<TaskComment> foundComment = taskCommentService.findTaskCommentById(100L);

        assertThat(foundComment).isEmpty();
    }

    @Test
    void findAllTaskComments_shouldReturnAllComments() {
        given(taskCommentRepository.findAll()).willReturn(List.of(comment1, comment2));

        List<TaskComment> allComments = taskCommentService.findAllTaskComments();

        assertThat(allComments).containsExactly(comment1, comment2);
    }

    @Test
    void findTaskCommentsByTask_shouldReturnCommentsForTheGivenTask() {
        given(taskCommentRepository.findByTask(task)).willReturn(List.of(comment1, comment2));

        List<TaskComment> comments = taskCommentService.findTaskCommentsByTask(task);

        assertThat(comments).containsExactly(comment1, comment2);
    }

    @Test
    void findTaskCommentsByTaskWithPageable_shouldReturnPaginatedCommentsForTheTask() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<TaskComment> commentPage = new PageImpl<>(List.of(comment1), pageable, 2);
        given(taskCommentRepository.findByTask(task, pageable)).willReturn(commentPage);

        Page<TaskComment> result = taskCommentService.findTaskCommentsByTask(task, pageable);

        assertThat(result).isEqualTo(commentPage);
    }

    @Test
    void findTaskCommentsByUser_shouldReturnCommentsMadeByTheGivenUser() {
        given(taskCommentRepository.findByUser(user)).willReturn(List.of(comment1, comment2));

        List<TaskComment> comments = taskCommentService.findTaskCommentsByUser(user);

        assertThat(comments).containsExactly(comment1, comment2);
    }

    @Test
    void findTaskCommentsByUserWithPageable_shouldReturnPaginatedCommentsForTheUser() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<TaskComment> commentPage = new PageImpl<>(List.of(comment1), pageable, 2);
        given(taskCommentRepository.findByUser(user, pageable)).willReturn(commentPage);

        Page<TaskComment> result = taskCommentService.findTaskCommentsByUser(user, pageable);

        assertThat(result).isEqualTo(commentPage);
    }

    @Test
    void findTaskCommentsByKeyword_shouldReturnCommentsContainingKeyword() {
        given(taskCommentRepository.findByCommentContainingIgnoreCase("first"))
                .willReturn(List.of(comment1));

        List<TaskComment> comments = taskCommentService.findTaskCommentsByKeyword("first");

        assertThat(comments).containsExactly(comment1);
    }

    @Test
    void findTaskCommentsByKeywordWithPageable_shouldReturnPaginatedCommentsContainingKeyword() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<TaskComment> commentPage = new PageImpl<>(List.of(comment1), pageable, 2);
        given(taskCommentRepository.findByCommentContainingIgnoreCase("comment", pageable)).willReturn(commentPage);

        Page<TaskComment> result = taskCommentService.findTaskCommentsByKeyword("comment", pageable);

        assertThat(result).isEqualTo(commentPage);
    }

    @Test
    void updateTaskComment_shouldUpdateCommentSuccessfully() {
        TaskComment updatedComment = new TaskComment();
        ReflectionTestUtils.setField(updatedComment, "id", comment1.getId());
        updatedComment.setComment("This is the updated comment.");

        given(taskCommentRepository.findById(comment1.getId())).willReturn(Optional.of(comment1));
        given(taskCommentRepository.save(any(TaskComment.class))).willReturn(updatedComment);

        TaskComment result = taskCommentService.updateTaskComment(updatedComment);

        assertThat(result).isEqualTo(updatedComment);
        verify(taskCommentRepository).save(any(TaskComment.class));
    }

    @Test
    void updateTaskComment_shouldThrowExceptionIfCommentNotFound() {
        TaskComment updatedComment = new TaskComment();
        ReflectionTestUtils.setField(updatedComment, "id", 100L);

        given(taskCommentRepository.findById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskCommentService.updateTaskComment(updatedComment))
                .isInstanceOf(TaskCommentNotFoundException.class)
                .hasMessageContaining("Task comment not found");
        verify(taskCommentRepository, never()).save(any());
    }

    @Test
    void deleteTaskComment_shouldDeleteCommentSuccessfully() {
        given(taskCommentRepository.existsById(comment1.getId())).willReturn(true);

        taskCommentService.deleteTaskComment(comment1.getId());

        verify(taskCommentRepository).deleteById(comment1.getId());
    }

    @Test
    void deleteTaskComment_shouldThrowExceptionIfCommentNotFound() {
        given(taskCommentRepository.existsById(100L)).willReturn(false);

        assertThatThrownBy(() -> taskCommentService.deleteTaskComment(100L))
                .isInstanceOf(TaskCommentNotFoundException.class)
                .hasMessageContaining("Task comment not found");
        verify(taskCommentRepository, never()).deleteById(any());
    }

    @Test
    void findAllTaskCommentsIncludingDeleted_shouldReturnAllCommentsIncludingDeleted() {
        given(taskCommentRepository.findAllIncludingDeleted()).willReturn(List.of(comment1, comment2));

        List<TaskComment> allComments = taskCommentService.findAllTaskCommentsIncludingDeleted();

        assertThat(allComments).containsExactly(comment1, comment2);
    }

    @Test
    void findAllTaskCommentsByTaskIdIncludingDeleted_shouldReturnAllCommentsForTaskIncludingDeleted() {
        given(taskCommentRepository.findAllByTaskIdIncludingDeleted(task.getId()))
                .willReturn(List.of(comment1, comment2));

        List<TaskComment> allComments = taskCommentService.findAllTaskCommentsByTaskIdIncludingDeleted(task.getId());

        assertThat(allComments).containsExactly(comment1, comment2);
    }
}