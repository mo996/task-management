package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.task.TaskPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskPriorityRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    private TaskPriority taskPriority1;
    private TaskPriority taskPriority2;
    private TaskPriority taskPriority3;

    @BeforeEach
    public void setup() {
        taskPriority1 = new TaskPriority();
        taskPriority1.setPriorityName("High");
        entityManager.persist(taskPriority1);

        taskPriority2 = new TaskPriority();
        taskPriority2.setPriorityName("Medium");
        entityManager.persist(taskPriority2);

        taskPriority3 = new TaskPriority();
        taskPriority3.setPriorityName("Low");
        entityManager.persist(taskPriority3);

        entityManager.flush();
    }

    @Test
    public void testCreateTaskPriority() {
        TaskPriority newTaskPriority = new TaskPriority();
        newTaskPriority.setPriorityName("Critical");

        TaskPriority savedTaskPriority = taskPriorityRepository.save(newTaskPriority);

        assertThat(savedTaskPriority).isNotNull();
        assertThat(savedTaskPriority.getId()).isNotNull();
        assertThat(savedTaskPriority.getPriorityName()).isEqualTo("Critical");
    }

    @Test
    public void testFindByPriorityNameIgnoreCase() {
        Optional<TaskPriority> foundTaskPriority = taskPriorityRepository.findByPriorityNameIgnoreCase("high");

        assertThat(foundTaskPriority).isPresent();
        assertThat(foundTaskPriority.get().getPriorityName()).isEqualTo("High");
    }

    @Test
    public void testFindByPriorityNameContainingIgnoreCase() {
        List<TaskPriority> foundTaskPriorities = taskPriorityRepository.findByPriorityNameContainingIgnoreCase("med");

        assertThat(foundTaskPriorities).hasSize(1);
        assertThat(foundTaskPriorities.get(0).getPriorityName()).isEqualTo("Medium");
    }

    @Test
    public void testFindAllWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<TaskPriority> taskPriorityPage = taskPriorityRepository.findAll(pageable);

        assertThat(taskPriorityPage.getContent()).hasSize(2);
    }

    @Test
    public void testExistsByPriorityNameIgnoreCase() {
        boolean exists = taskPriorityRepository.existsByPriorityNameIgnoreCase("high");
        assertThat(exists).isTrue();
    }
}
