package com.daaeboul.taskmanagementsystem.repository.task;

import com.daaeboul.taskmanagementsystem.model.task.TaskType;
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
public class TaskTypeRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    private TaskType taskType1;
    private TaskType taskType2;
    private TaskType taskType3;

    @BeforeEach
    public void setup() {
        taskType1 = new TaskType();
        taskType1.setTaskTypeName("Development");
        taskType1.setDescription("Tasks related to software development.");
        entityManager.persist(taskType1);

        taskType2 = new TaskType();
        taskType2.setTaskTypeName("Testing");
        taskType2.setDescription("Tasks related to quality assurance and testing.");
        entityManager.persist(taskType2);

        taskType3 = new TaskType();
        taskType3.setTaskTypeName("Deployment");
        taskType3.setDescription("Tasks related to deploying software to production.");
        entityManager.persist(taskType3);

        entityManager.flush();
    }

    @Test
    public void testCreateTaskType() {
        TaskType newTaskType = new TaskType();
        newTaskType.setTaskTypeName("Design");
        newTaskType.setDescription("Tasks related to user interface and user experience design.");

        TaskType savedTaskType = taskTypeRepository.save(newTaskType);

        assertThat(savedTaskType).isNotNull();
        assertThat(savedTaskType.getId()).isNotNull();
        assertThat(savedTaskType.getTaskTypeName()).isEqualTo("Design");
    }

    @Test
    public void whenFindById_thenReturnTaskType() {
        TaskType foundTaskType = taskTypeRepository.findById(taskType1.getId()).orElse(null);

        assertThat(foundTaskType).isNotNull();
        assertThat(foundTaskType.getTaskTypeName()).isEqualTo("Development");
    }

    @Test
    public void whenUpdateTaskType_thenUpdated() {
        taskType1.setTaskTypeName("Updated Task Type Name");
        taskTypeRepository.save(taskType1);

        TaskType updatedTaskType = entityManager.find(TaskType.class, taskType1.getId());
        assertThat(updatedTaskType.getTaskTypeName()).isEqualTo("Updated Task Type Name");
    }

    @Test
    public void whenDeleteTaskType_thenShouldBeRemoved() {
        TaskType foundTaskType = taskTypeRepository.findById(taskType1.getId()).orElse(null);
        assertThat(foundTaskType).isNotNull();

        taskTypeRepository.delete(foundTaskType);

        entityManager.flush();

        TaskType deletedTaskType = taskTypeRepository.findById(taskType1.getId()).orElse(null);
        assertThat(deletedTaskType).isNull();
    }

    @Test
    public void testFindByTaskTypeNameIgnoreCase() {
        Optional<TaskType> foundTaskType = taskTypeRepository.findByTaskTypeNameIgnoreCase("development");

        assertThat(foundTaskType).isPresent();
        assertThat(foundTaskType.get().getTaskTypeName()).isEqualTo("Development");
    }

    @Test
    public void testFindByTaskTypeNameContainingIgnoreCase() {
        List<TaskType> foundTaskTypes = taskTypeRepository.findByTaskTypeNameContainingIgnoreCase("ment");

        assertThat(foundTaskTypes).hasSize(2);
        assertThat(foundTaskTypes.get(0).getTaskTypeName()).isEqualTo("Development");
    }

    @Test
    public void testFindAllWithPagination() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<TaskType> taskTypePage = taskTypeRepository.findAll(pageable);

        assertThat(taskTypePage.getContent()).hasSize(2);
    }

    @Test
    public void testFindByDescriptionContainingIgnoreCase() {
        List<TaskType> foundTaskTypes = taskTypeRepository.findByDescriptionContainingIgnoreCase("quality");

        assertThat(foundTaskTypes).hasSize(1);
        assertThat(foundTaskTypes.get(0).getTaskTypeName()).isEqualTo("Testing");
    }

    @Test
    public void testExistsByTaskTypeNameIgnoreCase() {
        boolean exists = taskTypeRepository.existsByTaskTypeNameIgnoreCase("development");
        assertThat(exists).isTrue();
    }
}
