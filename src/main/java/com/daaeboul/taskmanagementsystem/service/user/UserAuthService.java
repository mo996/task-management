package com.daaeboul.taskmanagementsystem.service.user;

import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.user.userAuth.UserAuthNotFoundException;
import com.daaeboul.taskmanagementsystem.model.user.UserAuth;
import com.daaeboul.taskmanagementsystem.repository.user.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;

    @Autowired
    public UserAuthService(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    /**
     * Creates a new UserAuth entity. Enforces that a valid User is associated and has an ID before saving.
     *
     * @param userAuth The UserAuth object containing the data for the new UserAuth.
     * @return The newly created and saved UserAuth object.
     * @throws UserNotFoundException if the User associated with the UserAuth is not found or doesn't have a valid ID.
     */
    @Transactional
    public UserAuth createUserAuth(UserAuth userAuth) {
        if (userAuth.getUser() == null || userAuth.getUser().getId() == null) {
            throw new UserNotFoundException("User must be set and have a valid ID");
        }
        return userAuthRepository.save(userAuth);
    }

    /**
     * Retrieves a UserAuth from the database by its ID.
     *
     * @param userAuthId The ID of the UserAuth to retrieve.
     * @return An Optional containing the UserAuth if found, otherwise an empty Optional.
     */
    public Optional<UserAuth> findUserAuthById(Long userAuthId) {
        return userAuthRepository.findById(userAuthId);
    }

    /**
     * Retrieves a UserAuth from the database based on its unique authentication token.
     *
     * @param authToken The authentication token to search for.
     * @return An Optional containing the UserAuth if found, otherwise an empty Optional.
     */
    public Optional<UserAuth> findUserAuthByAuthToken(String authToken) {
        return userAuthRepository.findByAuthToken(authToken);
    }

    /**
     * Checks if a UserAuth record exists for a given user ID.
     *
     * @param userId The ID of the user to check for.
     * @return True if a UserAuth record exists for the specified user ID, otherwise false.
     */
    public boolean existsByUserId(Long userId) {
        return userAuthRepository.existsByUserId(userId);
    }

    /**
     * Updates an existing UserAuth record with new data.
     *
     * @param updatedUserAuth The UserAuth object containing the updated information.
     * @throws UserAuthNotFoundException if a UserAuth record with the provided ID doesn't exist.
     */
    @Transactional
    public void updateUserAuth(UserAuth updatedUserAuth) {
        UserAuth existingUserAuth = userAuthRepository.findById(updatedUserAuth.getId())
                .orElseThrow(() -> new UserAuthNotFoundException("UserAuth not found with id " + updatedUserAuth.getId()));

        existingUserAuth.setPasswordHash(updatedUserAuth.getPasswordHash());
        existingUserAuth.setAuthToken(updatedUserAuth.getAuthToken());
        existingUserAuth.setLastLoginAt(updatedUserAuth.getLastLoginAt());
        existingUserAuth.setFailedLoginAttempts(updatedUserAuth.getFailedLoginAttempts());
        existingUserAuth.setLocked(updatedUserAuth.isLocked());

        userAuthRepository.save(existingUserAuth);
    }

    /**
     * Deletes a UserAuth record. It's likely that this performs a soft delete.
     *
     * @param userAuthId The ID of the UserAuth record to delete.
     * @throws UserAuthNotFoundException if a UserAuth record with the provided ID doesn't exist.
     */
    @Transactional
    public void deleteUserAuth(Long userAuthId) {
        if (!userAuthRepository.existsById(userAuthId)) {
            throw new UserAuthNotFoundException("UserAuth not found with id " + userAuthId);
        }
        userAuthRepository.deleteById(userAuthId); // Soft delete
    }

    /**
     * Permanently deletes a UserAuth record from the database.  Use with caution as this cannot be undone.
     *
     * @param userAuthId  The ID of the UserAuth to permanently delete.
     * @throws UserAuthNotFoundException if a UserAuth with the provided ID doesn't exist.
     */
    @Transactional
    public void hardDeleteUserAuth(Long userAuthId) {
        if (!userAuthRepository.existsById(userAuthId)) {
            throw new UserAuthNotFoundException("UserAuth not found with id " + userAuthId);
        }
        userAuthRepository.hardDeleteById(userAuthId); // Hard delete
    }

    /**
     * Retrieves a UserAuth from the database based on the associated user ID.
     *
     * @param userId The ID of the user to search for.
     * @return An Optional containing the UserAuth if found, otherwise an empty Optional.
     */
    public Optional<UserAuth> findUserAuthByUserId(Long userId) {
        return userAuthRepository.findByUserId(userId);
    }

    /**
     * Retrieves a UserAuth for authentication purposes, verifying both the provided user ID and password hash match an existing record.
     *
     * @param userId The ID of the user to authenticate.
     * @param passwordHash  The user's password hash.
     * @return An Optional containing the UserAuth if found and the password hash matches, otherwise an empty Optional.
     */
    public Optional<UserAuth> authenticateUser(Long userId, String passwordHash) {
        return userAuthRepository.findByUserIdAndPasswordHash(userId, passwordHash);
    }

    /**
     * Updates the authentication token for a UserAuth record associated with the specified user ID.
     *
     * @param userId The ID of the user whose UserAuth record to update.
     * @param authToken The new authentication token to be set.
     * @throws UserAuthNotFoundException if a UserAuth record is not associated with the given user ID.
     */
    @Transactional
    public void updateAuthTokenByUserId(Long userId, String authToken) {
        if (!userAuthRepository.existsByUserId(userId)) {
            throw new UserAuthNotFoundException("UserAuth not found for user ID: " + userId);
        }
        userAuthRepository.updateAuthTokenByUserId(userId, authToken);
    }

    /**
     * Deletes a UserAuth record based on the specified user ID. It's likely that this performs a soft delete.
     *
     * @param userId The ID of the user whose UserAuth record to delete.
     * @throws UserAuthNotFoundException if a UserAuth record is not associated with the given user ID.
     */
    @Transactional
    public void deleteByUserId(Long userId) {
        if (!userAuthRepository.existsByUserId(userId)) {
            throw new UserAuthNotFoundException("UserAuth not found for user ID: " + userId);
        }
        userAuthRepository.deleteByUserId(userId);
    }

    /**
     * Updates the 'lastLoginAt' timestamp for the UserAuth record associated with the specified user ID to the current time.
     */
    @Transactional
    public void updateLastLoginTime(Long userId) {
        userAuthRepository.updateLastLoginTimeByUserId(userId);
    }

    /**
     * Increments the 'failedLoginAttempts' counter for the UserAuth record associated with the specified user ID.
     */
    @Transactional
    public void incrementFailedLoginAttempts(Long userId) {
        userAuthRepository.incrementFailedLoginAttemptsByUserId(userId);
    }

    /**
     * Resets the 'failedLoginAttempts' counter to 0 for the UserAuth record associated with the specified user ID.
     */
    @Transactional
    public void resetFailedLoginAttempts(Long userId) {
        userAuthRepository.resetFailedLoginAttemptsByUserId(userId);
    }

    /**
     * Sets the 'isLocked' flag to true for the UserAuth record associated with the specified user ID, effectively locking the user's account.
     */
    @Transactional
    public void lockUserAccount(Long userId) {
        userAuthRepository.lockUserAccountByUserId(userId);
    }

    /**
     * Sets the 'isLocked' flag to false for the UserAuth record associated with the specified user ID, effectively unlocking the user's account.
     */
    @Transactional
    public void unlockUserAccount(Long userId) {
        userAuthRepository.unlockUserAccountByUserId(userId);
    }

    /**
     * Retrieves a list of UserAuth records where the 'isLocked' flag is set to true, representing locked user accounts.
     *
     * @return A List of UserAuth entities representing locked accounts.
     */
    public List<UserAuth> findLockedAccounts() {
        return userAuthRepository.findByIsLockedTrue();
    }

    /**
     * Updates the 'passwordHash' for the UserAuth record associated with the specified user ID.
     *
     * @param userId The ID of the user whose password to update.
     * @param newPasswordHash  The new password hash (likely already processed and secured) for the user.
     */
    @Transactional
    public void updatePassword(Long userId, String newPasswordHash) {
        userAuthRepository.updatePasswordByUserId(userId, newPasswordHash);
    }
}
