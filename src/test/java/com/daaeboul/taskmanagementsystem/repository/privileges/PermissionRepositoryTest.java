package com.daaeboul.taskmanagementsystem.repository.privileges;

import com.daaeboul.taskmanagementsystem.model.privileges.Permission;
import com.daaeboul.taskmanagementsystem.model.privileges.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PermissionRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    public void whenFindByPermissionNameIgnoreCase_thenReturnPermission() {
        Permission viewProjectPermission = new Permission();
        viewProjectPermission.setPermissionName("View_ProJect");
        entityManager.persist(viewProjectPermission);
        entityManager.flush();

        Optional<Permission> found = permissionRepository.findByPermissionNameIgnoreCase("view_project");

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getPermissionName()).isEqualTo("View_ProJect");
    }

    @Test
    public void whenFindByNonExistentPermissionName_thenReturnEmpty() {
        String nonExistentPermissionName = "NON_EXISTENT";

        Optional<Permission> found = permissionRepository.findByPermissionNameIgnoreCase(nonExistentPermissionName);

        assertThat(found.isPresent()).isFalse();
    }

    @Test
    public void testPermissionCreation() {
        Permission newPermission = new Permission();
        newPermission.setPermissionName("edit_comment");
        newPermission.setDescription("Allows editing of existing comments");

        Permission savedPermission = permissionRepository.save(newPermission);

        assertThat(entityManager.find(Permission.class, savedPermission.getId())).isNotNull();
        assertThat(savedPermission.getPermissionName()).isEqualTo("edit_comment");
        assertThat(savedPermission.getDescription()).isEqualTo("Allows editing of existing comments");
    }

    @Test
    public void testPermissionUpdate() {
        Permission permission = new Permission();
        permission.setPermissionName("delete_attachment");
        entityManager.persist(permission);
        entityManager.flush();

        permission.setDescription("Ability to remove attachments");
        permissionRepository.save(permission);

        Permission updatedPermission = entityManager.find(Permission.class, permission.getId());
        assertThat(updatedPermission.getDescription()).isEqualTo("Ability to remove attachments");
    }

    @Test
    public void testPermissionDeletion() {
        Permission permissionToDelete = new Permission();
        permissionToDelete.setPermissionName("manage_workflow");
        entityManager.persist(permissionToDelete);
        entityManager.flush();

        permissionRepository.delete(permissionToDelete);
        Permission deletedPermission = entityManager.find(Permission.class, permissionToDelete.getId());

        assertThat(deletedPermission).isNull();
    }

    @Test
    public void whenFindUnused_thenReturnPermissionsWithoutRoles() {
        Permission unusedPermission1 = new Permission("UNUSED_1");
        Permission unusedPermission2 = new Permission("UNUSED_2");
        entityManager.persist(unusedPermission1);
        entityManager.persist(unusedPermission2);

        Permission usedPermission = new Permission("USED");
        Role role = new Role("TEST_ROLE");
        role.getPermissions().add(usedPermission); // Assign to a role
        entityManager.persist(role);
        entityManager.persist(usedPermission);

        entityManager.flush();

        List<Permission> unusedPermissions = permissionRepository.findUnused();

        assertThat(unusedPermissions).hasSize(2) // Only unused ones should be returned
                .containsExactlyInAnyOrder(unusedPermission1, unusedPermission2);
    }

    @Test
    public void whenFindByRoleName_thenReturnPermissionsAssociatedWithRole() {
        Role testRole = new Role("TEST_ROLE");
        Permission permission1 = new Permission("VIEW_TASKS");
        Permission permission2 = new Permission("ASSIGN_USERS");

        testRole.getPermissions().add(permission1);
        testRole.getPermissions().add(permission2);

        entityManager.persist(testRole);
        entityManager.persist(permission1);
        entityManager.persist(permission2);
        entityManager.flush();

        Set<Permission> foundPermissions = permissionRepository.findByRoleName("TEST_ROLE");

        assertThat(foundPermissions).hasSize(2)
                .containsExactlyInAnyOrder(permission1, permission2);
    }

}
