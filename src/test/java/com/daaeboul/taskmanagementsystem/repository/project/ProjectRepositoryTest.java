package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private Project project;

    @BeforeEach
    public void setup() {
        project = new Project();
        project.setProjectName("Project Alpha");
        project.setProjectDescription("An initial project.");
        project.setProjectStartDate(LocalDateTime.now().minusDays(10));
        project.setProjectEndDate(LocalDateTime.now().plusDays(20));
        project.setDeletedAt(null); // Ensure project is not soft-deleted
        entityManager.persistAndFlush(project);
    }

    @Test
    public void testCreateAndReadProject() {
        Project newProject = new Project();
        newProject.setProjectName("Project Beta");
        newProject.setProjectDescription("A secondary project.");
        newProject.setProjectStartDate(LocalDateTime.now());
        newProject.setProjectEndDate(LocalDateTime.now().plusDays(60));

        Project savedProject = projectRepository.save(newProject);
        assertThat(savedProject).isNotNull();
        assertThat(savedProject.getId()).isNotNull();
        assertThat(savedProject.getProjectName()).isEqualTo("Project Beta");

        Optional<Project> readProject = projectRepository.findById(savedProject.getId());
        assertThat(readProject).isPresent();
        assertThat(readProject.get().getProjectName()).isEqualTo("Project Beta");
    }

    @Test
    public void testUpdateProject() {
        project.setProjectDescription("An updated project description.");
        Project updatedProject = projectRepository.save(project);
        assertThat(updatedProject.getProjectDescription()).isEqualTo("An updated project description.");
    }

    @Test
    public void testDeleteProject() {
        projectRepository.deleteById(project.getId());
        Optional<Project> deletedProject = projectRepository.findById(project.getId());
        assertThat(deletedProject).isEmpty();
    }

    // Custom queries

    @Test
    public void testFindAllByDeletedAtIsNull() {
        List<Project> projects = projectRepository.findAllByDeletedAtIsNull();
        assertThat(projects).isNotEmpty();
        assertThat(projects).contains(project);
    }

    @Test
    public void testFindAllByDeletedAtIsNullWithPageable() {
        Page<Project> projects = projectRepository.findAllByDeletedAtIsNull(PageRequest.of(0, 10));
        assertThat(projects).isNotEmpty();
        assertThat(projects).contains(project);
    }

    @Test
    public void testFindAllByDeletedAtIsNotNull() {
        projectRepository.deleteById(project.getId());
        entityManager.flush();
        List<Project> projects = projectRepository.findAllByDeletedAtIsNotNull();
        assertThat(projects).isNotEmpty();
    }

    @Test
    public void testFindByProjectStartDateBeforeAndDeletedAtIsNull() {
        List<Project> projects = projectRepository.findByProjectStartDateBeforeAndDeletedAtIsNull(LocalDateTime.now().minusDays(5));
        assertThat(projects).isNotEmpty();
        assertThat(projects).contains(project);
    }

    @Test
    public void testFindByProjectEndDateAfterAndDeletedAtIsNull() {
        List<Project> projects = projectRepository.findByProjectEndDateAfterAndDeletedAtIsNull(LocalDateTime.now().plusDays(10));
        assertThat(projects).isNotEmpty();
        assertThat(projects).contains(project);
    }

    @Test
    public void testFindByProjectStartDateBetweenAndDeletedAtIsNull() {
        List<Project> projects = projectRepository.findByProjectStartDateBetweenAndDeletedAtIsNull(LocalDateTime.now().minusDays(15), LocalDateTime.now().minusDays(5));
        assertThat(projects).isNotEmpty();
        assertThat(projects).contains(project);
    }

    @Test
    public void testFindByProjectNameContainingIgnoreCaseAndDeletedAtIsNull() {
        String projectNamePart = "alpha";
        List<Project> projects = projectRepository.findByProjectNameContainingIgnoreCaseAndDeletedAtIsNull(projectNamePart);
        assertThat(projects).isNotEmpty();
        assertThat(projects.get(0).getProjectName()).containsIgnoringCase(projectNamePart);
    }

    @Test
    public void testHardDeleteProject() {
        Optional<Project> preDeletedProject = projectRepository.findById(project.getId());
        assertThat(preDeletedProject).isPresent();

        projectRepository.hardDeleteById(project.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<Project> deletedProject = projectRepository.findById(project.getId());
        assertThat(deletedProject).isNotPresent();
    }
}
