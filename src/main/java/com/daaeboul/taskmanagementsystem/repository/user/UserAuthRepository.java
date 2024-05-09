package com.daaeboul.taskmanagementsystem.repository.user;


import com.daaeboul.taskmanagementsystem.model.user.UserAuth;
import com.daaeboul.taskmanagementsystem.repository.BaseSoftDeletableRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAuthRepository extends BaseSoftDeletableRepository<UserAuth, Long> {

    /**
     * Retrieves a UserAuth from the database based on its unique authentication token.
     *
     * @param authToken The authentication token to search for.
     * @return An Optional containing the UserAuth if found, otherwise an empty Optional.
     */
    Optional<UserAuth> findByAuthToken(String authToken);

    /**
     * Checks if a UserAuth record exists associated with the specified user ID.
     *
     * @param userId The ID of the user to check for existence.
     * @return True if a UserAuth record is associated with the given user ID, otherwise false.
     */
    boolean existsByUserId(Long userId);

    /**
     * Updates the authentication token for a UserAuth record associated with the specified user ID.
     * This method is marked as modifying, ensuring a database transaction occurs.
     *
     * @param userId The ID of the user whose UserAuth record to update.
     * @param authToken The new authentication token to be set.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAuth u SET u.authToken = :authToken WHERE u.user.id = :userId")
    void updateAuthTokenByUserId(@Param("userId") Long userId, @Param("authToken") String authToken);

    /**
     * Deletes a UserAuth record associated with the specified user ID.
     * This method is marked as modifying, ensuring a database transaction occurs.
     *
     * @param userId The ID of the user whose UserAuth record to delete.
     */
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

    /**
     * Retrieves a UserAuth from the database based on the associated user ID.
     *
     * @param userId The ID of the user to search for.
     * @return An Optional containing the UserAuth if found, otherwise an empty Optional.
     */
    Optional<UserAuth> findByUserId(Long userId);

    /**
     * Retrieves a UserAuth for authentication purposes, matching the provided user ID and password hash.
     *
     * @param userId The ID of the user to authenticate.
     * @param passwordHash The user's password hash.
     * @return An Optional containing the UserAuth if found and the password hash matches, otherwise an empty Optional.
     */
    Optional<UserAuth> findByUserIdAndPasswordHash(Long userId, String passwordHash);

    /**
     * Updates the 'lastLoginAt' timestamp for the UserAuth associated with the specified user ID.
     * The timestamp is set to the current time. This method is marked as modifying, ensuring a database transaction occurs.
     *
     * @param userId The ID of the user whose UserAuth record to update.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAuth u SET u.lastLoginAt = CURRENT_TIMESTAMP WHERE u.user.id = :userId")
    void updateLastLoginTimeByUserId(@Param("userId") Long userId);

    /**
     * Increments the 'failedLoginAttempts' counter for the UserAuth associated with the specified user ID.
     * This method is marked as modifying, ensuring a database transaction occurs.
     *
     * @param userId The ID of the user whose UserAuth record to update.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAuth u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.user.id = :userId")
    void incrementFailedLoginAttemptsByUserId(@Param("userId") Long userId);

    /**
     * Resets the 'failedLoginAttempts' counter to 0 for the UserAuth associated with the specified user ID.
     * This method is marked as modifying, ensuring a database transaction occurs.
     *
     * @param userId The ID of the user whose UserAuth record to update.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAuth u SET u.failedLoginAttempts = 0 WHERE u.user.id = :userId")
    void resetFailedLoginAttemptsByUserId(@Param("userId") Long userId);


    /**
     * Sets the 'isLocked' flag to true for the UserAuth associated with the specified user ID, effectively locking the user's account.
     * This method is marked as modifying, ensuring a database transaction occurs.
     *
     * @param userId The ID of the user whose account to lock.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAuth u SET u.isLocked = true WHERE u.user.id = :userId")
    void lockUserAccountByUserId(@Param("userId") Long userId);

    /**
     * Sets the 'isLocked' flag to false for the UserAuth associated with the specified user ID, effectively unlocking the user's account.
     * This method is marked as modifying, ensuring a database transaction occurs.
     *
     * @param userId The ID of the user whose account to unlock.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAuth u SET u.isLocked = false WHERE u.user.id = :userId")
    void unlockUserAccountByUserId(@Param("userId") Long userId);

    /**
     * Retrieves a list of UserAuth records where the 'isLocked' flag is set to true (representing locked user accounts).
     *
     * @return A List of UserAuth entities representing locked accounts.
     */
    List<UserAuth> findByIsLockedTrue();

    /**
     * Updates the 'passwordHash' for the UserAuth associated with the specified user ID.
     * This method is marked as modifying, ensuring a database transaction occurs.
     *
     * @param userId The ID of the user whose password to update.
     * @param passwordHash  The new password hash for the user.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAuth u SET u.passwordHash = :passwordHash WHERE u.user.id = :userId")
    void updatePasswordByUserId(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);

}


