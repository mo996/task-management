package com.daaeboul.taskmanagementsystem.repository.project;

import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.model.project.ProjectRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectRoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRoleRepository projectRoleRepository;

    private Permission permission1, permission2;
    private ProjectRole projectRole1, projectRole2;

    @BeforeEach
    public void setup() {
        permission1 = new Permission("PERMISSION_1");
        permission2 = new Permission("PERMISSION_2");
        entityManager.persist(permission1);
        entityManager.persist(permission2);

        Set<Permission> permissions1 = new HashSet<>();
        permissions1.add(permission1);

        Set<Permission> permissions2 = new HashSet<>();
        permissions2.add(permission1);
        permissions2.add(permission2);

        projectRole1 = new ProjectRole();
        projectRole1.setRoleName("ROLE_1");
        projectRole1.setPermissions(permissions1);
        entityManager.persist(projectRole1);

        projectRole2 = new ProjectRole();
        projectRole2.setRoleName("ROLE_2");
        projectRole2.setPermissions(permissions2);
        entityManager.persist(projectRole2);

        entityManager.flush();
    }

    @AfterEach
    public void tearDown() {
        projectRoleRepository.deleteAll();
        entityManager.flush();
    }

    @Test
    public void testFindByRoleName() {
        Optional<ProjectRole> result = projectRoleRepository.findByRoleName("ROLE_1");
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(projectRole1);
    }

    @Test
    public void testFindByPermissionsIn() {
        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission1);
        List<ProjectRole> result = projectRoleRepository.findByPermissionsIn(permissions);
        assertThat(result).containsExactlyInAnyOrder(projectRole1, projectRole2);
    }

    @Test
    public void testFindByPermissionName() {
        List<ProjectRole> result = projectRoleRepository.findByPermissionName("PERMISSION_1");
        assertThat(result).containsExactlyInAnyOrder(projectRole1, projectRole2);
    }

    @Test
    public void testFindByPermissionNames() {
        Set<String> permissionNames = Set.of("PERMISSION_1", "PERMISSION_2");
        List<ProjectRole> result = projectRoleRepository.findByPermissionNames(permissionNames, 2);
        assertThat(result).containsExactly(projectRole2);
    }

    @Test
    public void testExistsByRoleName() {
        boolean exists = projectRoleRepository.existsByRoleName("ROLE_1");
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindWithAtLeastOnePermission() {
        List<ProjectRole> result = projectRoleRepository.findWithAtLeastOnePermission();
        assertThat(result).containsExactlyInAnyOrder(projectRole1, projectRole2);
    }
}
