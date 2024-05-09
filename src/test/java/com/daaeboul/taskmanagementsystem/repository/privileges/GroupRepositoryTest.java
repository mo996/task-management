package com.daaeboul.taskmanagementsystem.repository.privileges;

import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.privileges.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupRepository groupRepository;

    private Group group;

    @BeforeEach
    public void setup() {
        Role role = new Role();
        role.setRoleName("TestRole");
        entityManager.persist(role);

        group = new Group();
        group.setGroupName("TestGroup");
        group.setRole(role);
        entityManager.persist(group);
        entityManager.flush();
    }

    @Test
    public void testCreateGroup() {
        Group newGroup = new Group();
        newGroup.setGroupName("NewTestGroup");
        newGroup.setRole(group.getRole());

        Group savedGroup = groupRepository.save(newGroup);

        assertThat(savedGroup).isNotNull();
        assertThat(savedGroup.getId()).isNotNull();
        assertThat(savedGroup.getGroupName()).isEqualTo("NewTestGroup");
    }

    @Test
    public void whenFindById_thenReturnGroup() {
        Group foundGroup = groupRepository.findById(group.getId()).orElse(null);

        assertThat(foundGroup).isNotNull();
        assertThat(foundGroup.getGroupName()).isEqualTo("TestGroup");
    }

    @Test
    public void whenUpdateGroup_thenUpdated() {
        group.setGroupName("UpdatedGroupName");
        groupRepository.save(group);

        Group updatedGroup = entityManager.find(Group.class, group.getId());
        assertThat(updatedGroup.getGroupName()).isEqualTo("UpdatedGroupName");
    }

    @Test
    public void whenSoftDeleteGroup_thenNotRetrievable() {
        groupRepository.deleteById(group.getId());
        entityManager.flush();

        Group deletedGroup = entityManager.find(Group.class, group.getId());

        assertThat(deletedGroup).isNotNull();
        assertThat(deletedGroup.getDeletedAt()).isNotNull();
    }

}
