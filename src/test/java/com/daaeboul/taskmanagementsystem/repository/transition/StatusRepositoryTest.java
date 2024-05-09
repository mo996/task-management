package com.daaeboul.taskmanagementsystem.repository.transition;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StatusRepositoryTest {

    @Autowired
    private StatusRepository statusRepository;

    @Test
    void findByStatusName_returnsStatusWithMatchingName() {
        Status savedStatus = statusRepository.save(new Status("In Progress", "Work in progress"));

        Status foundStatus = statusRepository.findByStatusName("In Progress").orElse(null);

        assertThat(foundStatus).isNotNull();
        assertThat(foundStatus.getId()).isEqualTo(savedStatus.getId());
    }

    @Test
    void findByStatusNameContainingIgnoreCase_returnsStatusesWithMatchingNamePart() {
        statusRepository.save(new Status("In Progress", "Work in progress"));
        statusRepository.save(new Status("Completed", "Finished work"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Status> foundStatuses = statusRepository.findByStatusNameContainingIgnoreCase("progress", pageable);

        assertThat(foundStatuses).isNotEmpty();
        assertThat(foundStatuses.getContent().get(0).getStatusName()).isEqualTo("In Progress");
    }

    @Test
    void findByDescriptionContainingIgnoreCase_returnsStatusesWithMatchingDescriptionPart() {
        statusRepository.save(new Status("In Progress", "Work in progress"));
        statusRepository.save(new Status("Completed", "Finished work"));

        List<Status> foundStatuses = statusRepository.findByDescriptionContainingIgnoreCase("work");

        assertThat(foundStatuses).isNotEmpty();
        assertThat(foundStatuses).hasSize(2);
    }

    @Test
    void existsByStatusName_returnsTrueForExistingStatusName() {
        statusRepository.save(new Status("In Progress", "Work in progress"));

        boolean exists = statusRepository.existsByStatusName("In Progress");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByStatusName_returnsFalseForNonExistingStatusName() {
        boolean exists = statusRepository.existsByStatusName("Nonexistent Status");

        assertThat(exists).isFalse();
    }

}
