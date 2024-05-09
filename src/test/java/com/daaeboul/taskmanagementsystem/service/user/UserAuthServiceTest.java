package com.daaeboul.taskmanagementsystem.service.user;

import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.user.userAuth.UserAuthNotFoundException;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.model.user.UserAuth;
import com.daaeboul.taskmanagementsystem.repository.user.UserAuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserAuthServiceTest {

    @Autowired
    private UserAuthService userAuthService;

    @MockBean
    private UserAuthRepository userAuthRepository;

    @Test
    void shouldCreateUserAuth() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        UserAuth newUserAuth = new UserAuth();
        newUserAuth.setUser(user);
        newUserAuth.setPasswordHash("hashedPassword");
        newUserAuth.setAuthToken("authToken");
        when(userAuthRepository.save(newUserAuth)).thenReturn(newUserAuth);

        UserAuth createdUserAuth = userAuthService.createUserAuth(newUserAuth);

        assertNotNull(createdUserAuth);
        assertEquals(user, createdUserAuth.getUser());
        assertEquals("hashedPassword", createdUserAuth.getPasswordHash());
        assertEquals("authToken", createdUserAuth.getAuthToken());
        verify(userAuthRepository).save(newUserAuth);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotSet() {
        UserAuth newUserAuth = new UserAuth();
        newUserAuth.setPasswordHash("hashedPassword");
        newUserAuth.setAuthToken("authToken");

        assertThrows(UserNotFoundException.class, () -> userAuthService.createUserAuth(newUserAuth));
    }

    @Test
    void shouldFindUserAuthById() {
        Long userAuthId = 1L;
        UserAuth expectedUserAuth = new UserAuth();
        ReflectionTestUtils.setField(expectedUserAuth, "id", userAuthId);
        when(userAuthRepository.findById(userAuthId)).thenReturn(Optional.of(expectedUserAuth));

        Optional<UserAuth> foundUserAuth = userAuthService.findUserAuthById(userAuthId);

        assertTrue(foundUserAuth.isPresent());
        assertEquals(expectedUserAuth, foundUserAuth.get());
    }

    @Test
    void shouldUpdateUserAuth() {
        UserAuth existingUserAuth = new UserAuth();
        ReflectionTestUtils.setField(existingUserAuth, "id", 1L);
        existingUserAuth.setPasswordHash("oldPasswordHash");
        existingUserAuth.setAuthToken("oldAuthToken");

        UserAuth updatedUserAuth = new UserAuth();
        ReflectionTestUtils.setField(updatedUserAuth, "id", 1L);
        updatedUserAuth.setPasswordHash("newPasswordHash");
        updatedUserAuth.setAuthToken("newAuthToken");
        when(userAuthRepository.findById(1L)).thenReturn(Optional.of(existingUserAuth));
        when(userAuthRepository.save(any(UserAuth.class))).thenReturn(updatedUserAuth);

        userAuthService.updateUserAuth(updatedUserAuth);

        verify(userAuthRepository).save(any(UserAuth.class));
    }

    @Test
    void shouldThrowUserAuthNotFoundExceptionOnUpdateWhenUserAuthDoesNotExist() {
        Long userAuthId = 999L;
        UserAuth updatedUserAuth = new UserAuth();
        ReflectionTestUtils.setField(updatedUserAuth, "id", userAuthId);
        when(userAuthRepository.findById(userAuthId)).thenReturn(Optional.empty());

        assertThrows(UserAuthNotFoundException.class, () -> userAuthService.updateUserAuth(updatedUserAuth));
    }

    @Test
    void shouldDeleteUserAuthSuccessfully() {
        Long userAuthId = 1L;
        when(userAuthRepository.existsById(userAuthId)).thenReturn(true);

        userAuthService.deleteUserAuth(userAuthId);

        verify(userAuthRepository).deleteById(userAuthId);
    }

    @Test
    void shouldThrowUserAuthNotFoundExceptionOnDeleteWhenUserAuthDoesNotExist() {
        Long userAuthId = 999L;
        when(userAuthRepository.existsById(userAuthId)).thenReturn(false);

        assertThrows(UserAuthNotFoundException.class, () -> userAuthService.deleteUserAuth(userAuthId));
    }

    @Test
    void shouldHardDeleteUserAuthSuccessfully() {
        Long userAuthId = 1L;
        when(userAuthRepository.existsById(userAuthId)).thenReturn(true);
        doNothing().when(userAuthRepository).hardDeleteById(userAuthId);

        assertDoesNotThrow(() -> userAuthService.hardDeleteUserAuth(userAuthId));

        verify(userAuthRepository, times(1)).hardDeleteById(userAuthId);
    }

    @Test
    void findUserAuthByAuthTokenTest() {
        String authToken = "testToken";
        UserAuth userAuth = new UserAuth();
        userAuth.setAuthToken(authToken);
        when(userAuthRepository.findByAuthToken(authToken)).thenReturn(Optional.of(userAuth));
        Optional<UserAuth> foundUserAuth = userAuthService.findUserAuthByAuthToken(authToken);
        assertTrue(foundUserAuth.isPresent());
        assertEquals(authToken, foundUserAuth.get().getAuthToken());
    }

    @Test
    void existsByUserIdTest() {
        Long userId = 1L;
        when(userAuthRepository.existsByUserId(userId)).thenReturn(true);
        boolean exists = userAuthService.existsByUserId(userId);
        assertTrue(exists);
    }

    @Test
    void findUserAuthByUserIdTest() {
        Long userId = 1L;
        UserAuth userAuth = new UserAuth();
        User dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", userId);
        ReflectionTestUtils.setField(userAuth, "user", dummyUser);
        when(userAuthRepository.findByUserId(userId)).thenReturn(Optional.of(userAuth));

        Optional<UserAuth> foundUserAuth = userAuthService.findUserAuthByUserId(userId);

        assertTrue(foundUserAuth.isPresent());
        assertEquals(userId, foundUserAuth.get().getUser().getId());
    }

    @Test
    void authenticateUserTest() {
        Long userId = 1L;
        String passwordHash = "testHash";
        UserAuth userAuth = new UserAuth();
        userAuth.setPasswordHash(passwordHash);
        when(userAuthRepository.findByUserIdAndPasswordHash(userId, passwordHash)).thenReturn(Optional.of(userAuth));
        Optional<UserAuth> authenticatedUserAuth = userAuthService.authenticateUser(userId, passwordHash);
        assertTrue(authenticatedUserAuth.isPresent());
        assertEquals(passwordHash, authenticatedUserAuth.get().getPasswordHash());
    }

    @Test
    void updateLastLoginTimeTest() {
        Long userId = 1L;
        doNothing().when(userAuthRepository).updateLastLoginTimeByUserId(userId);
        assertDoesNotThrow(() -> userAuthService.updateLastLoginTime(userId));
        verify(userAuthRepository, times(1)).updateLastLoginTimeByUserId(userId);
    }

    @Test
    void incrementFailedLoginAttemptsTest() {
        Long userId = 1L;
        doNothing().when(userAuthRepository).incrementFailedLoginAttemptsByUserId(userId);
        assertDoesNotThrow(() -> userAuthService.incrementFailedLoginAttempts(userId));
        verify(userAuthRepository, times(1)).incrementFailedLoginAttemptsByUserId(userId);
    }

    @Test
    void resetFailedLoginAttemptsTest() {
        Long userId = 1L;
        doNothing().when(userAuthRepository).resetFailedLoginAttemptsByUserId(userId);
        assertDoesNotThrow(() -> userAuthService.resetFailedLoginAttempts(userId));
        verify(userAuthRepository, times(1)).resetFailedLoginAttemptsByUserId(userId);
    }

    @Test
    void lockUserAccountTest() {
        Long userId = 1L;
        doNothing().when(userAuthRepository).lockUserAccountByUserId(userId);
        assertDoesNotThrow(() -> userAuthService.lockUserAccount(userId));
        verify(userAuthRepository, times(1)).lockUserAccountByUserId(userId);
    }

    @Test
    void unlockUserAccountTest() {
        Long userId = 1L;
        doNothing().when(userAuthRepository).unlockUserAccountByUserId(userId);
        assertDoesNotThrow(() -> userAuthService.unlockUserAccount(userId));
        verify(userAuthRepository, times(1)).unlockUserAccountByUserId(userId);
    }

    @Test
    void findLockedAccountsTest() {
        List<UserAuth> lockedUserAuths = Arrays.asList(new UserAuth(), new UserAuth());
        when(userAuthRepository.findByIsLockedTrue()).thenReturn(lockedUserAuths);
        List<UserAuth> foundLockedAccounts = userAuthService.findLockedAccounts();
        assertNotNull(foundLockedAccounts);
        assertEquals(2, foundLockedAccounts.size());
    }

    @Test
    void updatePasswordTest() {
        Long userId = 1L;
        String newPasswordHash = "newHashedPassword";
        doNothing().when(userAuthRepository).updatePasswordByUserId(userId, newPasswordHash);
        assertDoesNotThrow(() -> userAuthService.updatePassword(userId, newPasswordHash));
        verify(userAuthRepository, times(1)).updatePasswordByUserId(userId, newPasswordHash);
    }

    @Test
    void shouldUpdateAuthTokenByUserId() {
        Long userId = 1L;
        String newAuthToken = "newAuthToken";
        when(userAuthRepository.existsByUserId(userId)).thenReturn(true);
        doNothing().when(userAuthRepository).updateAuthTokenByUserId(userId, newAuthToken);

        userAuthService.updateAuthTokenByUserId(userId, newAuthToken);

        verify(userAuthRepository).updateAuthTokenByUserId(userId, newAuthToken);
    }

    @Test
    void shouldThrowExceptionWhenUserAuthDoesNotExistForUpdateAuthToken() {
        Long userId = 999L;
        String newAuthToken = "newAuthToken";
        when(userAuthRepository.existsByUserId(userId)).thenReturn(false);

        assertThrows(UserAuthNotFoundException.class, () -> userAuthService.updateAuthTokenByUserId(userId, newAuthToken));
    }

    @Test
    void shouldDeleteUserAuthByUserId() {
        Long userId = 1L;
        when(userAuthRepository.existsByUserId(userId)).thenReturn(true);
        doNothing().when(userAuthRepository).deleteByUserId(userId);

        userAuthService.deleteByUserId(userId);

        verify(userAuthRepository).deleteByUserId(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserAuthDoesNotExistForDeleteByUserId() {
        Long userId = 999L;
        when(userAuthRepository.existsByUserId(userId)).thenReturn(false);

        assertThrows(UserAuthNotFoundException.class, () -> userAuthService.deleteByUserId(userId));
    }
}