package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.project.Project;
import com.daaeboul.taskmanagementsystem.model.project.ProjectGroup;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import com.daaeboul.taskmanagementsystem.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectGroupRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectGroupRepository projectGroupRepository;

    private Project project;
    private User user;
    private Group group;
    private ProjectRole projectRole;
    private ProjectGroup projectGroup;

    @BeforeEach
    public void setup() {
        project = new Project();
        project.setProjectName("Test Project");
        entityManager.persist(project);

        user = new User();
        user.setUsername("testUser");
        entityManager.persist(user);

        group = new Group();
        group.setGroupName("Test Group");
        group.getUsers().add(user);
        user.getGroups().add(group);
        entityManager.persist(group);

        projectRole = new ProjectRole();
        projectRole.setRoleName("Test Role");
        entityManager.persist(projectRole);

        ProjectGroup.ProjectGroupId pgId = new ProjectGroup.ProjectGroupId();
        pgId.setProjectId(project.getId());
        pgId.setGroupId(group.getId());

        projectGroup = new ProjectGroup();
        projectGroup.setId(pgId);
        projectGroup.setProject(project);
        projectGroup.setGroup(group);
        projectGroup.setProjectRole(projectRole);
        entityManager.persist(projectGroup);

        entityManager.flush();
    }

    @Test
    public void testCreateProjectGroup() {

        ProjectGroup.ProjectGroupId pgId = new ProjectGroup.ProjectGroupId();
        pgId.setProjectId(project.getId());
        pgId.setGroupId(group.getId());

        ProjectGroup newProjectGroup = new ProjectGroup();
        newProjectGroup.setId(pgId);
        newProjectGroup.setProject(project);
        newProjectGroup.setGroup(group);
        newProjectGroup.setProjectRole(projectRole);

        ProjectGroup savedProjectGroup = projectGroupRepository.save(newProjectGroup);
        assertThat(savedProjectGroup).isNotNull();
        assertThat(savedProjectGroup.getId()).isNotNull();
        assertThat(savedProjectGroup.getProject()).isEqualTo(project);
        assertThat(savedProjectGroup.getGroup()).isEqualTo(group);
    }

    @Test
    public void testReadProjectGroup() {
        ProjectGroup foundProjectGroup = projectGroupRepository.findById(projectGroup.getId()).orElse(null);
        assertThat(foundProjectGroup).isNotNull();
        assertThat(foundProjectGroup.getProject()).isEqualTo(project);
        assertThat(foundProjectGroup.getGroup()).isEqualTo(group);
    }

    @Test
    public void testUpdateProjectGroup() {
        projectGroup.setProjectRole(new ProjectRole());
        ProjectGroup updatedProjectGroup = projectGroupRepository.save(projectGroup);
        assertThat(updatedProjectGroup.getProjectRole()).isNotNull();
    }

    @Test
    public void testDeleteProjectGroup() {
        projectGroupRepository.delete(projectGroup);
        entityManager.flush();

        ProjectGroup deletedProjectGroup = projectGroupRepository.findById(projectGroup.getId()).orElse(null);
        assertThat(deletedProjectGroup).isNull();
    }

    @Test
    public void testFindByProjectId() {
        Page<ProjectGroup> result = projectGroupRepository.findByProjectId(project.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).contains(projectGroup);
    }

    @Test
    public void testFindByGroupId() {
        Page<ProjectGroup> result = projectGroupRepository.findByGroupId(group.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).contains(projectGroup);
    }

    @Test
    public void testFindByProjectRoleId() {
        Page<ProjectGroup> result = projectGroupRepository.findByProjectRoleId(projectRole.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).contains(projectGroup);
    }

    @Test
    public void testFindByProjectIdAndGroupId() {
        ProjectGroup result = projectGroupRepository.findByProjectIdAndGroupId(project.getId(), group.getId());
        assertThat(result).isEqualTo(projectGroup);
    }

    @Test
    public void testFindByProjectProjectName() {
        Page<ProjectGroup> result = projectGroupRepository.findByProjectProjectName("Test Project", PageRequest.of(0, 10));
        assertThat(result.getContent()).contains(projectGroup);
    }

    @Test
    public void testFindByGroupGroupName() {
        Page<ProjectGroup> result = projectGroupRepository.findByGroupGroupName("Test Group", PageRequest.of(0, 10));
        assertThat(result.getContent()).contains(projectGroup);
    }

    @Test
    public void testDeleteByProjectId() {
        projectGroupRepository.deleteByProjectId(project.getId());
        entityManager.flush();

        List<ProjectGroup> result = projectGroupRepository.findByProjectId(project.getId(), PageRequest.of(0, 10)).getContent();
        assertThat(result).isEmpty();
    }

    @Test
    public void testDeleteByGroupId() {
        projectGroupRepository.deleteByGroupId(group.getId());
        entityManager.flush();

        List<ProjectGroup> result = projectGroupRepository.findByGroupId(group.getId(), PageRequest.of(0, 10)).getContent();
        assertThat(result).isEmpty();
    }

    @Test
    public void testFindUsersByProjectId() {
        entityManager.clear();
        Page<User> users = projectGroupRepository.findUsersByProjectId(project.getId(), PageRequest.of(0, 10));
        assertThat(users.getContent()).as("Check if users are associated with the project").isNotEmpty();
    }

    @Test
    public void testFindProjectsByGroupId() {
        Page<Project> projects = projectGroupRepository.findProjectsByGroupId(group.getId(), PageRequest.of(0, 10));
        assertThat(projects).isNotEmpty(); // Check for non-empty result
    }

    @Test
    public void testFindByProjectIdAndGroupGroupName() {
        List<ProjectGroup> result = projectGroupRepository.findByProjectIdAndGroupGroupName(project.getId(), "Test Group");
        assertThat(result).contains(projectGroup);
    }

    @Test
    public void testFindByProjectUsersIdAndProjectId() {
        List<ProjectGroup> result = projectGroupRepository.findProjectGroupsByUserAndProject(user.getId(), project.getId());
        assertThat(result).isNotEmpty();
        assertThat(result).contains(projectGroup);
    }
}
