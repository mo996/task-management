package com.daaeboul.taskmanagementsystem.repository.privileges;

import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.model.privileges.Role;
import com.daaeboul.taskmanagementsystem.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @AfterEach
    public void tearDown() {
        entityManager.clear(); // Clear the persistence context
    }

    @Test
    public void whenFindByRoleName_thenReturnRole() {
        Role admin = new Role();
        admin.setRoleName("SUPER_ADMIN");
        entityManager.persist(admin);
        entityManager.flush();

        Optional<Role> found = roleRepository.findByRoleName("SUPER_ADMIN");

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getRoleName()).isEqualTo("SUPER_ADMIN");
    }

    @Test
    public void whenFindByRoleName_thenReturnEmpty() {
        String nonExistentRoleName = "NON_EXISTENT";

        Optional<Role> found = roleRepository.findByRoleName(nonExistentRoleName);

        assertThat(found.isPresent()).isFalse();
    }

    @Test
    public void testRoleCreation() {
        Role newRole = new Role();
        newRole.setRoleName("USER2");

        Role savedRole = roleRepository.save(newRole);

        assertThat(entityManager.find(Role.class, savedRole.getId())).isNotNull();
        assertThat(savedRole.getRoleName()).isEqualTo("USER2");
    }

    @Test
    public void testRoleDeletion() {
        Role roleToDelete = new Role();
        roleToDelete.setRoleName("TO_DELETE");
        entityManager.persist(roleToDelete);
        entityManager.flush();

        roleRepository.delete(roleToDelete);
        Role deletedRole = entityManager.find(Role.class, roleToDelete.getId());

        assertThat(deletedRole).isNull();
    }

    @Test
    public void whenFindByPermissionsPermissionNameIn_thenReturnRolesWithPermissions() {
        Permission permission1 = new Permission();
        permission1.setPermissionName("create_task");
        entityManager.persist(permission1);

        Permission permission2 = new Permission();
        permission2.setPermissionName("edit_project");
        entityManager.persist(permission2);

        Role role1 = new Role();
        role1.setRoleName("Role1");
        role1.setPermissions(Set.of(permission1));
        entityManager.persist(role1);

        Role role2 = new Role();
        role2.setRoleName("Role2");
        role2.setPermissions(Set.of(permission2));
        entityManager.persist(role2);

        List<Role> roles = roleRepository.findByPermissionsPermissionNameIn(List.of("create_task", "edit_project"));

        assertThat(roles).hasSize(2).containsExactlyInAnyOrder(role1, role2);
    }

    @Test
    public void whenExistsByRoleNameAndPermissionsPermissionName_thenReturnTrueIfRoleHasPermission() {
        Permission permission1 = new Permission();
        permission1.setPermissionName("create_task");
        entityManager.persist(permission1);

        Permission permission2 = new Permission();
        permission2.setPermissionName("edit_project");
        entityManager.persist(permission2);

        Role role1 = new Role();
        role1.setRoleName("Role1");
        role1.setPermissions(Set.of(permission1));
        entityManager.persist(role1);

        Role role2 = new Role();
        role2.setRoleName("Role2");
        role2.setPermissions(Set.of(permission2));
        entityManager.persist(role2);

        boolean exists = roleRepository.existsByRoleNameAndPermissionsPermissionName("Role1", "create_task");

        assertThat(exists).isTrue();
    }

    @Test
    public void whenFindUnassignedRoles_thenReturnRolesNotAssignedToUsers() {
        Role assignedRole = new Role();
        assignedRole.setRoleName("AssignedRole");
        entityManager.persist(assignedRole);

        Group group = new Group();
        group.setGroupName("TestGroup");
        group.setRole(assignedRole);
        entityManager.persist(group);

        User user = new User();
        user.setUsername("testUser");
        user.setGroups(Set.of(group));
        entityManager.persist(user);

        Role unassignedRole = new Role();
        unassignedRole.setRoleName("UnassignedRole");
        entityManager.persist(unassignedRole);

        List<Role> unassignedRoles = roleRepository.findUnassignedRoles();

        assertThat(unassignedRoles).contains(unassignedRole);
    }
}
