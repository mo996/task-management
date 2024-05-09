package com.daaeboul.taskmanagementsystem.service.user;

import com.daaeboul.taskmanagementsystem.exceptions.user.user.DuplicateUsernameException;
import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldCreateUser() {
        User newUser = new User();
        newUser.setUsername("newUser");
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(userRepository.findByUsernameIgnoreCase("newUser")).thenReturn(Optional.empty());

        User createdUser = userService.createUser(newUser);

        assertNotNull(createdUser);
        assertEquals("newUser", createdUser.getUsername());
        verify(userRepository).save(newUser);
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsDuplicate() {
        User duplicateUser = new User();
        duplicateUser.setUsername("existingUser");
        when(userRepository.findByUsernameIgnoreCase("existingUser")).thenReturn(Optional.of(duplicateUser));

        Exception exception = assertThrows(DuplicateUsernameException.class, () -> userService.createUser(duplicateUser));

        assertEquals("A user with the username 'existingUser' already exists.", exception.getMessage());
    }

    @Test
    void shouldFindUserById() {
        Long userId = 1L;
        User expectedUser = new User();
        ReflectionTestUtils.setField(expectedUser, "id", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        Optional<User> foundUser = userService.findUserById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(expectedUser, foundUser.get());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User existingUser = new User();
        ReflectionTestUtils.setField(existingUser, "id", 1L);
        existingUser.setUsername("OriginalUsername");

        User updatedUser = new User();
        ReflectionTestUtils.setField(updatedUser, "id", 1L);
        updatedUser.setUsername("UpdatedUsername");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsernameIgnoreCase("UpdatedUsername")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(updatedUser);

        assertNotNull(result);
        assertEquals("UpdatedUsername", result.getUsername());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void shouldDeleteUserSuccessfully() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void shouldThrowUserNotFoundExceptionOnDeleteWhenUserDoesNotExist() {
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void findUserByUsernameTest() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(user));
        Optional<User> foundUser = userService.findUserByUsername(username);
        assertTrue(foundUser.isPresent());
        assertEquals(username, foundUser.get().getUsername());
    }

    @Test
    void findUsersByUsernameContainingTest() {
        String usernamePart = "joh";
        List<User> expectedUsers = Arrays.asList(
                new User(),
                new User()
        );
        expectedUsers.get(0).setUsername("john.doe");
        expectedUsers.get(1).setUsername("BigJohnny");

        when(userRepository.findByUsernameContainingIgnoreCase(usernamePart)).thenReturn(expectedUsers);

        List<User> foundUsers = userService.findByUsernameContaining(usernamePart);

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.stream().anyMatch(user -> user.getUsername().equals("john.doe")));
        assertTrue(foundUsers.stream().anyMatch(user -> user.getUsername().equals("BigJohnny")));
    }
    @Test
    void findAllUsersTest() {
        List<User> userList = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);
        List<User> users = userService.findAllUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void findAllUsersPageableTest() {
        Pageable pageable = PageRequest.of(0, 1);
        List<User> userList = List.of(new User());
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        Page<User> users = userService.findAllUsers(pageable);
        assertNotNull(users);
        assertEquals(1, users.getTotalElements());
    }

    @Test
    void findByGroupsGroupNameTest() {
        String groupName = "TestGroup";
        List<User> userList = Arrays.asList(new User(), new User());
        when(userRepository.findByGroupsGroupName(groupName)).thenReturn(userList);
        List<User> users = userService.findByGroupsGroupName(groupName);
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void findByGroupsGroupNamePageableTest() {
        String groupName = "TestGroup";
        Pageable pageable = PageRequest.of(0, 1);
        List<User> userList = List.of(new User());
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());
        when(userRepository.findByGroupsGroupName(groupName, pageable)).thenReturn(userPage);
        Page<User> users = userService.findByGroupsGroupName(groupName, pageable);
        assertNotNull(users);
        assertEquals(1, users.getTotalElements());
    }

    @Test
    void findByUserDetailsEmailIgnoreCaseTest() {
        String email = "test@example.com";
        List<User> userList = List.of(new User());
        when(userRepository.findByUserDetailsEmailIgnoreCase(email)).thenReturn(userList);
        List<User> users = userService.findByUserDetailsEmailIgnoreCase(email);
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void countNonDeletedUsersTest() {
        long expectedCount = 5;
        when(userRepository.count()).thenReturn(expectedCount);
        long userCount = userService.countNonDeletedUsers();
        assertEquals(expectedCount, userCount);
    }

    @Test
    void findAllIncludingDeletedTest() {
        List<User> userList = Arrays.asList(new User(), new User());
        when(userRepository.findAllIncludingDeleted()).thenReturn(userList);
        List<User> users = userService.findAllIncludingDeleted();
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void findAllIncludingDeletedPageableTest() {
        Pageable pageable = PageRequest.of(0, 1);
        List<User> userList = List.of(new User());
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());
        when(userRepository.findAllIncludingDeleted(pageable)).thenReturn(userPage);
        Page<User> users = userService.findAllIncludingDeleted(pageable);
        assertNotNull(users);
        assertEquals(1, users.getTotalElements());
    }

    @Test
    void hardDeleteUserTest() {
        Long userId = 1L;
        doNothing().when(userRepository).hardDeleteById(userId);
        assertDoesNotThrow(() -> userService.hardDeleteUser(userId));
        verify(userRepository, times(1)).hardDeleteById(userId);
    }

    @Test
    void findByGroupsInTest() {
        Group group1 = new Group("Administrators");
        Group group2 = new Group("Developers");
        List<Group> searchGroups = Arrays.asList(group1, group2);

        User userInGroup1 = new User();
        userInGroup1.setGroups(new HashSet<>(List.of(group1)));

        User userInGroup2 = new User();
        userInGroup2.setGroups(new HashSet<>(List.of(group2)));

        List<User> expectedUsers = Arrays.asList(userInGroup1, userInGroup2);

        when(userRepository.findByGroupsIn(searchGroups)).thenReturn(expectedUsers);

        List<User> foundUsers = userService.findByGroupsIn(searchGroups);

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(userInGroup1));
        assertTrue(foundUsers.contains(userInGroup2));
    }
}
