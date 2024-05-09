package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectTaskType;
import com.daaeboul.taskmanagementsystem.model.task.TaskType;
import com.daaeboul.taskmanagementsystem.model.transition.Workflow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectTaskTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectTaskTypeRepository projectTaskTypeRepository;

    @BeforeEach
    public void setup() {
        Project project = new Project();
        project.setProjectName("Alpha Project");
        project = entityManager.persistFlushFind(project);

        TaskType taskType = new TaskType();
        taskType.setTaskTypeName("Development");
        taskType = entityManager.persistFlushFind(taskType);

        Workflow workflow = new Workflow();
        workflow.setName("Standard Workflow");
        workflow = entityManager.persistFlushFind(workflow);

        ProjectTaskType.ProjectTaskTypeId id = new ProjectTaskType.ProjectTaskTypeId(project.getId(), taskType.getId());
        ProjectTaskType projectTaskType = new ProjectTaskType();
        projectTaskType.setId(id);
        projectTaskType.setProject(project);
        projectTaskType.setTaskType(taskType);
        projectTaskType.setWorkflow(workflow);
        entityManager.persistFlushFind(projectTaskType);
    }

    @Test
    public void whenFindByProjectName_thenReturnProjectTaskTypes() {
        List<ProjectTaskType> found = projectTaskTypeRepository.findByProjectName("Alpha Project");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getProject().getProjectName()).isEqualTo("Alpha Project");
    }

    @Test
    public void whenFindByTaskTypeName_thenReturnProjectTaskTypes() {
        List<ProjectTaskType> found = projectTaskTypeRepository.findByTaskTypeName("Development");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getTaskType().getTaskTypeName()).isEqualTo("Development");
    }

    @Test
    public void whenFindByWorkflowName_thenReturnProjectTaskTypes() {
        List<ProjectTaskType> found = projectTaskTypeRepository.findByWorkflowName("Standard Workflow");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getWorkflow().getName()).isEqualTo("Standard Workflow");
    }

    @Test
    public void whenFindProjectsUsingWorkflowByName_thenReturnProjects() {
        List<Project> projects = projectTaskTypeRepository.findProjectsUsingWorkflowByName("Standard Workflow");
        assertThat(projects).isNotEmpty();
        assertThat(projects.get(0).getProjectName()).isEqualTo("Alpha Project");
    }

    @Test
    public void whenFindTaskTypesByProjectName_thenReturnTaskTypes() {
        List<TaskType> taskTypes = projectTaskTypeRepository.findTaskTypesByProjectName("Alpha Project");
        assertThat(taskTypes).isNotEmpty();
        assertThat(taskTypes.get(0).getTaskTypeName()).isEqualTo("Development");
    }

    @Test
    public void whenCountDistinctTaskTypesByProjectName_thenReturnCount() {
        Long count = projectTaskTypeRepository.countDistinctTaskTypesByProjectName("Alpha Project");
        assertThat(count).isEqualTo(1);
    }
}