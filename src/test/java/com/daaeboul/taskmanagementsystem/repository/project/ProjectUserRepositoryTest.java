package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import com.daaeboul.taskmanagementsystem.model.project.ProjectUser;
import com.daaeboul.taskmanagementsystem.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectUserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    private Project project;
    private User user;
    private ProjectRole projectRole;
    private ProjectUser projectUser;

    @BeforeEach
    public void setup() {
        project = new Project();
        project.setProjectName("Test Project");
        entityManager.persist(project);

        user = new User();
        user.setUsername("testUser");
        entityManager.persist(user);

        projectRole = new ProjectRole();
        projectRole.setRoleName("Test Role");
        entityManager.persist(projectRole);

        ProjectUser.ProjectUserId puId = new ProjectUser.ProjectUserId();
        puId.setProjectId(project.getId());
        puId.setUserId(user.getId());

        projectUser = new ProjectUser();
        projectUser.setId(puId);
        projectUser.setProject(project);
        projectUser.setUser(user);
        projectUser.setProjectRole(projectRole);
        entityManager.persist(projectUser);

        entityManager.flush();
    }

    @Test
    public void testCreateProjectUser() {
        ProjectUser.ProjectUserId puId = new ProjectUser.ProjectUserId();
        puId.setProjectId(project.getId());
        puId.setUserId(user.getId());

        ProjectUser newProjectUser = new ProjectUser();
        newProjectUser.setId(puId);
        newProjectUser.setProject(project);
        newProjectUser.setUser(user);
        newProjectUser.setProjectRole(projectRole);

        ProjectUser savedProjectUser = projectUserRepository.save(newProjectUser);
        assertThat(savedProjectUser).isNotNull();
        assertThat(savedProjectUser.getId()).isNotNull();
        assertThat(savedProjectUser.getProject()).isEqualTo(project);
        assertThat(savedProjectUser.getUser()).isEqualTo(user);
    }

    @Test
    public void testReadProjectUser() {
        ProjectUser foundProjectUser = projectUserRepository.findById(projectUser.getId()).orElse(null);
        assertThat(foundProjectUser).isNotNull();
        assertThat(foundProjectUser.getProject()).isEqualTo(project);
        assertThat(foundProjectUser.getUser()).isEqualTo(user);
    }

    @Test
    public void testUpdateProjectUser() {
        projectUser.setProjectRole(new ProjectRole());
        ProjectUser updatedProjectUser = projectUserRepository.save(projectUser);
        assertThat(updatedProjectUser.getProjectRole()).isNotNull();
    }

    @Test
    public void testDeleteProjectUser() {
        projectUserRepository.delete(projectUser);
        entityManager.flush();

        ProjectUser deletedProjectUser = projectUserRepository.findById(projectUser.getId()).orElse(null);
        assertThat(deletedProjectUser).isNull();
    }

    @Test
    public void testFindByIdProjectIdAndIdUserId() {
        ProjectUser result = projectUserRepository.findByIdProjectIdAndIdUserId(project.getId(), user.getId());
        assertThat(result).isEqualTo(projectUser);
    }

    @Test
    public void testFindByIdProjectId() {
        Page<ProjectUser> result = projectUserRepository.findByIdProjectId(project.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).contains(projectUser);
    }

    @Test
    public void testFindByIdUserId() {
        Page<ProjectUser> result = projectUserRepository.findByIdUserId(user.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).contains(projectUser);
    }

    @Test
    public void testFindByProjectNameAndUserId() {
        List<ProjectUser> result = projectUserRepository.findByProjectNameAndUserId("Test Project", user.getId());
        assertThat(result).contains(projectUser);
    }

    @Test
    public void testFindByProjectIdAndProjectRoleName() {
        List<ProjectUser> result = projectUserRepository.findByProjectIdAndProjectRoleName(project.getId(), "Test Role");
        assertThat(result).contains(projectUser);
    }

    @Test
    public void testFindByProjectNameAndProjectRoleName() {
        List<ProjectUser> result = projectUserRepository.findByProjectNameAndProjectRoleName("Test Project", "Test Role");
        assertThat(result).contains(projectUser);
    }

    @Test
    public void testFindUsersByProjectId() {
        entityManager.clear();
        Page<User> users = projectUserRepository.findUsersByProjectId(project.getId(), PageRequest.of(0, 10));
        assertThat(users.getContent()).as("Check if users are associated with the project").isNotEmpty();
    }

    @Test
    public void testFindProjectsByUserId() {
        Page<Project> projects = projectUserRepository.findProjectsByUserId(user.getId(), PageRequest.of(0, 10));
        assertThat(projects).isNotEmpty();
    }

    @Test
    public void testExistsByIdProjectIdAndIdUserId() {
        boolean exists = projectUserRepository.existsByIdProjectIdAndIdUserId(project.getId(), user.getId());
        assertThat(exists).isTrue();
    }

    @Test
    public void testCountByIdProjectId() {
        long count = projectUserRepository.countByIdProjectId(project.getId());
        assertThat(count).isGreaterThan(0);
    }

    @Test
    public void testCountByIdUserId() {
        long count = projectUserRepository.countByIdUserId(user.getId());
        assertThat(count).isGreaterThan(0);
    }

}
