package com.daaeboul.taskmanagementsystem.repository.user;


import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.model.user.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Group group1;
    private Group group2;

    @BeforeEach
    public void setup() {
        user1 = new User();
        user1.setUsername("john.doe");
        user1 = entityManager.persistFlushFind(user1);

        user2 = new User();
        user2.setUsername("jane.doe");
        user2 = entityManager.persistFlushFind(user2);

        group1 = new Group();
        group1.setGroupName("Administrators");
        group1 = entityManager.persistFlushFind(group1);

        group2 = new Group();
        group2.setGroupName("Developers");
        group2 = entityManager.persistFlushFind(group2);

        user1.getGroups().add(group1);
        user2.getGroups().add(group2);

        UserDetails userDetails1 = new UserDetails();
        userDetails1.setUser(user1);
        userDetails1.setEmail("john.doe@example.com");
        entityManager.persistAndFlush(userDetails1);
    }

    @Test
    public void testCreateUser() {
        User newUser = new User();
        newUser.setUsername("new.user");

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("new.user");
    }

    @Test
    public void testReadUser() {
        Optional<User> foundUser = userRepository.findById(user1.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("john.doe");
    }

    @Test
    public void testUpdateUser() {
        user1.setUsername("updated.username");
        userRepository.save(user1);

        User updatedUser = entityManager.find(User.class, user1.getId());
        assertThat(updatedUser.getUsername()).isEqualTo("updated.username");
    }

    @Test
    public void testDeleteUser() {
        User userToDelete = userRepository.findByUsernameIgnoreCase("john.doe").orElseThrow();

        userRepository.deleteById(userToDelete.getId());
        entityManager.flush();

        User deletedUser = entityManager.find(User.class, userToDelete.getId());

        assertThat(deletedUser).isNotNull();

        assertThat(deletedUser.getDeletedAt()).isNotNull();
    }

    @Test
    public void testHardDeleteUser() {
        User userToDelete = userRepository.findByUsernameIgnoreCase("john.doe").orElseThrow();
        Long userId = userToDelete.getId();

        userRepository.hardDeleteById(userId);
        entityManager.flush(); // Ensure the deletion is flushed to the database
        entityManager.clear();

        User deletedUser = entityManager.find(User.class, userId);

        assertThat(deletedUser).isNull();
    }

    @Test
    public void whenFindByUsernameIgnoreCase_thenReturnUser() {
        Optional<User> foundUser = userRepository.findByUsernameIgnoreCase("JOHN.DOE");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("john.doe");
    }

    @Test
    public void whenFindByGroupsGroupName_thenReturnUsers() {
        List<User> foundUsers = userRepository.findByGroupsGroupName("Developers");
        assertThat(foundUsers).containsExactly(user2);
    }

    @Test
    public void whenFindByGroupsGroupNameWithPaging_thenReturnPaginatedUsers() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> foundUsers = userRepository.findByGroupsGroupName("Developers", pageable);
        assertThat(foundUsers.getContent()).containsExactly(user2);
        assertThat(foundUsers.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void whenFindByUserDetailsEmailIgnoreCase_thenReturnUsers() {
        List<User> foundUsers = userRepository.findByUserDetailsEmailIgnoreCase("JOHN.DOE@EXAMPLE.COM");
        assertThat(foundUsers).containsExactly(user1);
    }

    @Test
    public void whenCount_thenReturnCountOfNonDeletedUsers() {
        long count = userRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void whenFindAll_thenReturnNonDeletedUsers() {
        List<User> users = userRepository.findAll();
        assertThat(users).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    public void whenFindAllWithPaging_thenReturnPaginatedNonDeletedUsers() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("username").descending());
        Page<User> users = userRepository.findAll(pageable);
        assertThat(users.getContent()).containsExactly(user1);
        assertThat(users.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void whenFindAllIncludingDeleted_thenReturnAllUsers() {
        List<User> users = userRepository.findAllIncludingDeleted();
        assertThat(users).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    public void whenFindAllIncludingDeletedWithPaging_thenReturnPaginatedAllUsers() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("username").descending());
        Page<User> users = userRepository.findAllIncludingDeleted(pageable);
        assertThat(users.getContent()).containsExactly(user1);
        assertThat(users.getTotalElements()).isEqualTo(2);
    }
}