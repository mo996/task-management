package com.daaeboul.taskmanagementsystem.service.task;

import com.daaeboul.taskmanagementsystem.exceptions.task.taskAttachment.TaskAttachmentNotFoundException;
import com.daaeboul.taskmanagementsystem.model.task.Task;
import com.daaeboul.taskmanagementsystem.model.task.TaskAttachment;
import com.daaeboul.taskmanagementsystem.repository.task.TaskAttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskAttachmentServiceTest {

    @Mock
    private TaskAttachmentRepository taskAttachmentRepository;

    @InjectMocks
    private TaskAttachmentService taskAttachmentService;

    private Task task;
    private TaskAttachment attachment1;
    private TaskAttachment attachment2;

    @BeforeEach
    void setUp() {
        task = new Task();
        attachment1 = new TaskAttachment();
        attachment1.setFileName("attachment1.txt");
        attachment2 = new TaskAttachment();
        attachment2.setFileName("attachment2.pdf");

        ReflectionTestUtils.setField(task, "id", 1L);
        ReflectionTestUtils.setField(attachment1, "id", 1L);
        ReflectionTestUtils.setField(attachment2, "id", 2L);
    }

    @Test
    void createTaskAttachment_shouldCreateAttachmentSuccessfully() {
        given(taskAttachmentRepository.save(attachment1)).willReturn(attachment1);

        TaskAttachment createdAttachment = taskAttachmentService.createTaskAttachment(task, attachment1);

        assertThat(createdAttachment).isEqualTo(attachment1);
        assertThat(createdAttachment.getTask()).isEqualTo(task);
        verify(taskAttachmentRepository).save(attachment1);
    }

    @Test
    void findTaskAttachmentById_shouldReturnAttachmentIfFound() {
        given(taskAttachmentRepository.findById(attachment1.getId())).willReturn(Optional.of(attachment1));

        Optional<TaskAttachment> foundAttachment = taskAttachmentService.findTaskAttachmentById(attachment1.getId());

        assertThat(foundAttachment).isPresent().contains(attachment1);
    }

    @Test
    void findTaskAttachmentById_shouldReturnEmptyOptionalIfNotFound() {
        given(taskAttachmentRepository.findById(100L)).willReturn(Optional.empty());

        Optional<TaskAttachment> foundAttachment = taskAttachmentService.findTaskAttachmentById(100L);

        assertThat(foundAttachment).isEmpty();
    }

    @Test
    void findTaskAttachmentsByTaskId_shouldReturnAttachmentsForTheGivenTask() {
        given(taskAttachmentRepository.findByTaskId(task.getId())).willReturn(List.of(attachment1, attachment2));

        List<TaskAttachment> attachments = taskAttachmentService.findTaskAttachmentsByTaskId(task.getId());

        assertThat(attachments).containsExactly(attachment1, attachment2);
    }

    @Test
    void updateTaskAttachment_shouldUpdateAttachmentSuccessfully() {
        TaskAttachment updatedAttachment = new TaskAttachment();
        ReflectionTestUtils.setField(updatedAttachment, "id", attachment1.getId());
        updatedAttachment.setFileName("updated_attachment.txt");

        given(taskAttachmentRepository.findById(attachment1.getId())).willReturn(Optional.of(attachment1));
        given(taskAttachmentRepository.save(any(TaskAttachment.class))).willReturn(updatedAttachment);

        TaskAttachment result = taskAttachmentService.updateTaskAttachment(updatedAttachment);

        assertThat(result).isEqualTo(updatedAttachment);
        verify(taskAttachmentRepository).save(any(TaskAttachment.class));
    }

    @Test
    void updateTaskAttachment_shouldThrowExceptionIfAttachmentNotFound() {
        TaskAttachment updatedAttachment = new TaskAttachment();
        ReflectionTestUtils.setField(updatedAttachment, "id", 100L);

        given(taskAttachmentRepository.findById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskAttachmentService.updateTaskAttachment(updatedAttachment))
                .isInstanceOf(TaskAttachmentNotFoundException.class)
                .hasMessageContaining("Task attachment not found");
        verify(taskAttachmentRepository, never()).save(any());
    }

    @Test
    void deleteTaskAttachment_shouldDeleteAttachmentSuccessfully() {
        given(taskAttachmentRepository.existsById(attachment1.getId())).willReturn(true);

        taskAttachmentService.deleteTaskAttachment(attachment1.getId());

        verify(taskAttachmentRepository).deleteById(attachment1.getId());
    }

    @Test
    void deleteTaskAttachment_shouldThrowExceptionIfAttachmentNotFound() {
        given(taskAttachmentRepository.existsById(100L)).willReturn(false);

        assertThatThrownBy(() -> taskAttachmentService.deleteTaskAttachment(100L))
                .isInstanceOf(TaskAttachmentNotFoundException.class)
                .hasMessageContaining("Task attachment not found");
        verify(taskAttachmentRepository, never()).deleteById(any());
    }

    @Test
    void findTaskAttachmentsByFileName_shouldReturnAttachmentsWithMatchingFileName() {
        given(taskAttachmentRepository.findByFileNameIgnoreCase(attachment1.getFileName()))
                .willReturn(List.of(attachment1));

        List<TaskAttachment> foundAttachments = taskAttachmentService.findTaskAttachmentsByFileName(attachment1.getFileName());

        assertThat(foundAttachments).containsExactly(attachment1);
    }

    @Test
    void findTaskAttachmentsByFileType_shouldReturnAttachmentsWithMatchingFileType() {
        given(taskAttachmentRepository.findByFileTypeIgnoreCase("application/pdf"))
                .willReturn(List.of(attachment2));

        List<TaskAttachment> foundAttachments = taskAttachmentService.findTaskAttachmentsByFileType("application/pdf");

        assertThat(foundAttachments).containsExactly(attachment2);
    }

}