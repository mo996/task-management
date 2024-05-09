package com.daaeboul.taskmanagementsystem.service.transition;


import com.daaeboul.taskmanagementsystem.exceptions.transition.status.DuplicateStatusNameException;
import com.daaeboul.taskmanagementsystem.exceptions.transition.status.StatusNotFoundException;
import com.daaeboul.taskmanagementsystem.model.transition.Status;
import com.daaeboul.taskmanagementsystem.repository.transition.StatusRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusService statusService;

    private Status status1;
    private Status status2;
    private Status status3;

    @BeforeEach
    void setUp() {
        status1 = new Status("To Do", "Tasks that are not yet started");
        status2 = new Status("In Progress", "Tasks that are currently being worked on");
        status3 = new Status("Done", "Completed tasks");

        ReflectionTestUtils.setField(status1, "id", 1L);
        ReflectionTestUtils.setField(status2, "id", 2L);
        ReflectionTestUtils.setField(status3, "id", 3L);
    }

    @Test
    void createStatus_shouldCreateStatusSuccessfully() {
        given(statusRepository.existsByStatusName(status1.getStatusName())).willReturn(false);
        given(statusRepository.save(status1)).willReturn(status1);

        Status createdStatus = statusService.createStatus(status1);

        assertThat(createdStatus).isEqualTo(status1);
        verify(statusRepository).save(status1);
    }

    @Test
    void createStatus_shouldThrowExceptionForDuplicateStatusName() {
        given(statusRepository.existsByStatusName(status1.getStatusName())).willReturn(true);

        assertThatThrownBy(() -> statusService.createStatus(status1))
                .isInstanceOf(DuplicateStatusNameException.class)
                .hasMessageContaining("Status name already exists");
        verify(statusRepository, never()).save(any());
    }

    @Test
    void findStatusById_shouldReturnStatusIfFound() {
        given(statusRepository.findById(status1.getId())).willReturn(Optional.of(status1));

        Optional<Status> foundStatus = statusService.findStatusById(status1.getId());

        assertThat(foundStatus).isPresent().contains(status1);
    }

    @Test
    void findStatusById_shouldReturnEmptyOptionalIfNotFound() {
        given(statusRepository.findById(100L)).willReturn(Optional.empty());

        Optional<Status> foundStatus = statusService.findStatusById(100L);

        assertThat(foundStatus).isEmpty();
    }

    @Test
    void findStatusByName_shouldReturnStatusIfFound() {
        given(statusRepository.findByStatusName(status1.getStatusName())).willReturn(Optional.of(status1));

        Optional<Status> foundStatus = statusService.findStatusByName(status1.getStatusName());

        assertThat(foundStatus).isPresent().contains(status1);
    }

    @Test
    void findStatusByName_shouldReturnEmptyOptionalIfNotFound() {
        given(statusRepository.findByStatusName("Nonexistent")).willReturn(Optional.empty());

        Optional<Status> foundStatus = statusService.findStatusByName("Nonexistent");

        assertThat(foundStatus).isEmpty();
    }

    @Test
    void findAllStatuses_shouldReturnAllStatuses() {
        given(statusRepository.findAll()).willReturn(List.of(status1, status2, status3));

        List<Status> allStatuses = statusService.findAllStatuses();

        assertThat(allStatuses).containsExactly(status1, status2, status3);
    }

    @Test
    void findAllStatuses_withPageable_shouldReturnPagedStatuses() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Status> pagedStatuses = new PageImpl<>(List.of(status1, status2), pageable, 3);
        given(statusRepository.findAll(pageable)).willReturn(pagedStatuses);

        Page<Status> result = statusService.findAllStatuses(pageable);

        assertThat(result).isEqualTo(pagedStatuses);
    }

    @Test
    void findStatusesByNameContaining_shouldReturnMatchingStatuses() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Status> pagedStatuses = new PageImpl<>(List.of(status1, status2), pageable, 3);
        given(statusRepository.findByStatusNameContainingIgnoreCase("Do", pageable)).willReturn(pagedStatuses);

        Page<Status> result = statusService.findStatusesByNameContaining("Do", pageable);

        assertThat(result).isEqualTo(pagedStatuses);
    }

    @Test
    void findStatusesByDescriptionContaining_shouldReturnMatchingStatuses() {
        given(statusRepository.findByDescriptionContainingIgnoreCase("work")).willReturn(List.of(status2));

        List<Status> result = statusService.findStatusesByDescriptionContaining("work");

        assertThat(result).containsExactly(status2);
    }

    @Test
    void updateStatus_shouldUpdateStatusSuccessfully() {
        Status updatedStatus = new Status("Updated", "Updated description");
        ReflectionTestUtils.setField(updatedStatus, "id", status1.getId());

        given(statusRepository.findById(status1.getId())).willReturn(Optional.of(status1));
        given(statusRepository.save(any(Status.class))).willReturn(updatedStatus);

        Status result = statusService.updateStatus(updatedStatus);

        assertThat(result).isEqualTo(updatedStatus);
        verify(statusRepository).save(any(Status.class));
    }

    @Test
    void updateStatus_shouldThrowExceptionIfStatusNotFound() {
        Status updatedStatus = new Status("Updated", "Updated description");
        ReflectionTestUtils.setField(updatedStatus, "id", 100L);

        given(statusRepository.findById(100L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> statusService.updateStatus(updatedStatus))
                .isInstanceOf(StatusNotFoundException.class)
                .hasMessageContaining("Status not found");
        verify(statusRepository, never()).save(any());
    }

    @Test
    void deleteStatus_shouldDeleteStatusSuccessfully() {
        given(statusRepository.existsById(status1.getId())).willReturn(true);

        statusService.deleteStatus(status1.getId());

        verify(statusRepository).deleteById(status1.getId());
    }

    @Test
    void deleteStatus_shouldThrowExceptionIfStatusNotFound() {
        given(statusRepository.existsById(100L)).willReturn(false);

        assertThatThrownBy(() -> statusService.deleteStatus(100L))
                .isInstanceOf(StatusNotFoundException.class)
                .hasMessageContaining("Status not found");
        verify(statusRepository, never()).deleteById(any());
    }

    @Test
    void findStatusesCreatedBefore_shouldReturnMatchingStatuses() {
        LocalDateTime dateTime = LocalDateTime.now();
        given(statusRepository.findByCreatedAtBefore(dateTime)).willReturn(List.of(status1, status2));

        List<Status> result = statusService.findStatusesCreatedBefore(dateTime);

        assertThat(result).containsExactly(status1, status2);
    }
}