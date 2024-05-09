package com.daaeboul.taskmanagementsystem.repository.user;

import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.model.user.UserAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserAuthRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserAuthRepository userAuthRepository;

    private UserAuth userAuth;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setUsername("testUser");
        entityManager.persist(user);

        userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setPasswordHash("hashedPassword");
        userAuth.setAuthToken("authToken");
        userAuth.setDeletedAt(null);

        entityManager.persist(userAuth);
        entityManager.flush();
    }

    @Test
    public void testCreateUserAuth() {
        User user = new User();
        user.setUsername("username");
        user = entityManager.persistFlushFind(user);

        UserAuth userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setPasswordHash("hashedPassword");
        userAuth.setAuthToken("authToken");

        UserAuth savedUserAuth = userAuthRepository.save(userAuth);

        assertNotNull(savedUserAuth);
        assertNotNull(savedUserAuth.getUser());
        assertEquals(user.getId(), savedUserAuth.getUser().getId());
    }


    @Test
    public void whenFindById_thenReturnUserAuth() {
        UserAuth foundUserAuth = userAuthRepository.findById(userAuth.getId()).orElse(null);

        assertThat(foundUserAuth).isNotNull();
        assertThat(foundUserAuth.getUser()).isEqualTo(userAuth.getUser());
    }

    @Test
    public void testUpdateUserAuth() {
        Optional<UserAuth> optionalUserAuth = userAuthRepository.findById(userAuth.getId());

        if (optionalUserAuth.isPresent()) {
            UserAuth existingUserAuth = optionalUserAuth.get();
            existingUserAuth.setAuthToken("updatedAuthToken");
            userAuthRepository.save(existingUserAuth);

            UserAuth updatedUserAuth = entityManager.find(UserAuth.class, existingUserAuth.getId());
            assertThat(updatedUserAuth.getAuthToken()).isEqualTo("updatedAuthToken");
        } else {
            fail("UserAuth not found with the provided ID");
        }
    }

    @Test
    public void whenSoftDelete_thenShouldNotFindUserAuthById() {
        userAuthRepository.deleteById(userAuth.getId());
        entityManager.flush();

        UserAuth deletedUserAuth = entityManager.find(UserAuth.class, userAuth.getId());

        assertThat(deletedUserAuth).isNotNull();
        assertThat(deletedUserAuth.getDeletedAt()).isNotNull();
    }

    @Test
    public void findByAuthToken_ShouldReturnUserAuth() {
        Optional<UserAuth> result = userAuthRepository.findByAuthToken(userAuth.getAuthToken());
        assertTrue(result.isPresent());
        assertEquals(userAuth.getAuthToken(), result.get().getAuthToken());
    }

    @Test
    public void existsByUserId_ShouldReturnTrue() {
        boolean exists = userAuthRepository.existsByUserId(userAuth.getUser().getId());
        assertTrue(exists);
    }

    @Test
    public void updateAuthTokenByUserId_ShouldUpdateAuthToken() {
        String newAuthToken = "newAuthToken";
        userAuthRepository.updateAuthTokenByUserId(userAuth.getUser().getId(), newAuthToken);
        entityManager.flush();
        entityManager.refresh(userAuth);
        Optional<UserAuth> updatedUserAuth = userAuthRepository.findById(userAuth.getId());
        updatedUserAuth.ifPresent(auth -> assertEquals(newAuthToken, auth.getAuthToken()));
    }

    @Test
    public void deleteByUserId_ShouldSoftDeleteUserAuth() {
        userAuthRepository.deleteByUserId(userAuth.getUser().getId());
        entityManager.flush();

        Optional<UserAuth> softDeletedUserAuth = userAuthRepository.findById(userAuth.getId());
        assertTrue(softDeletedUserAuth.isPresent());
        assertNotNull(softDeletedUserAuth.get().getDeletedAt());
    }

    @Test
    public void findByUserIdAndPasswordHash_ShouldReturnUserAuth() {
        Optional<UserAuth> result = userAuthRepository.findByUserIdAndPasswordHash(userAuth.getUser().getId(), userAuth.getPasswordHash());
        assertTrue(result.isPresent());
        assertEquals(userAuth.getPasswordHash(), result.get().getPasswordHash());
    }

    @Test
    public void updateLastLoginTimeByUserId_ShouldUpdateLastLoginTime() {
        userAuthRepository.updateLastLoginTimeByUserId(userAuth.getUser().getId());
        entityManager.flush();
        entityManager.refresh(userAuth);

        UserAuth updatedUserAuth = userAuthRepository.findById(userAuth.getId()).get();
        assertNotNull(updatedUserAuth.getLastLoginAt());
    }

    @Test
    public void incrementFailedLoginAttemptsByUserId_ShouldIncrementFailedLoginAttempts() {
        userAuthRepository.incrementFailedLoginAttemptsByUserId(userAuth.getUser().getId());
        entityManager.flush();
        entityManager.refresh(userAuth);
        Optional<UserAuth> updatedUserAuth = userAuthRepository.findById(userAuth.getId());
        updatedUserAuth.ifPresent(auth -> assertEquals(1, auth.getFailedLoginAttempts()));
    }

    @Test
    public void resetFailedLoginAttemptsByUserId_ShouldResetFailedLoginAttempts() {
        userAuthRepository.incrementFailedLoginAttemptsByUserId(userAuth.getUser().getId());
        entityManager.flush();

        userAuthRepository.resetFailedLoginAttemptsByUserId(userAuth.getUser().getId());
        entityManager.flush();

        UserAuth updatedUserAuth = userAuthRepository.findById(userAuth.getId()).get();
        assertEquals(0, updatedUserAuth.getFailedLoginAttempts());
    }

    @Test
    public void lockUserAccountByUserId_ShouldLockUserAccount() {
        userAuthRepository.lockUserAccountByUserId(userAuth.getUser().getId());
        entityManager.flush();
        entityManager.refresh(userAuth);
        Optional<UserAuth> lockedUserAuth = userAuthRepository.findById(userAuth.getId());
        lockedUserAuth.ifPresent(auth -> assertTrue(auth.isLocked()));
    }

    @Test
    public void unlockUserAccountByUserId_ShouldUnlockUserAccount() {
        userAuthRepository.lockUserAccountByUserId(userAuth.getUser().getId());
        entityManager.flush();

        userAuthRepository.unlockUserAccountByUserId(userAuth.getUser().getId());
        entityManager.flush();

        UserAuth unlockedUserAuth = userAuthRepository.findById(userAuth.getId()).get();
        assertFalse(unlockedUserAuth.isLocked());
    }

    @Test
    public void findByIsLockedTrue_ShouldReturnLockedUserAuths() {
        userAuthRepository.lockUserAccountByUserId(userAuth.getUser().getId());
        entityManager.flush();
        entityManager.refresh(userAuth);
        List<UserAuth> lockedUserAuths = userAuthRepository.findByIsLockedTrue();
        assertThat(lockedUserAuths).isNotEmpty();
        assertTrue(lockedUserAuths.stream().anyMatch(UserAuth::isLocked));
    }

    @Test
    public void updatePasswordByUserId_ShouldUpdatePasswordHash() {
        String newPasswordHash = "newHashedPassword";
        userAuthRepository.updatePasswordByUserId(userAuth.getUser().getId(), newPasswordHash);
        entityManager.flush();
        entityManager.refresh(userAuth);
        Optional<UserAuth> updatedUserAuth = userAuthRepository.findById(userAuth.getId());
        updatedUserAuth.ifPresent(auth -> assertEquals(newPasswordHash, auth.getPasswordHash()));

    }

    @Test
    public void findByUserId_ShouldReturnUserAuth() {
        Optional<UserAuth> result = userAuthRepository.findByUserId(userAuth.getUser().getId());
        assertTrue(result.isPresent());
        assertEquals(userAuth.getUser().getId(), result.get().getUser().getId());
    }

    @Test
    public void whenHardDeleteById_thenShouldPermanentlyDeleteUserAuth() {
        Optional<UserAuth> userAuth1 = userAuthRepository.findById(userAuth.getId());
        assertTrue(userAuth1.isPresent());

        userAuthRepository.hardDeleteById(userAuth1.get().getId());
        entityManager.flush();
        entityManager.clear();

        UserAuth deletedUserAuth = entityManager.find(UserAuth.class, userAuth1.get().getId());

        assertThat(deletedUserAuth).isNull();
    }
}


