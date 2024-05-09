package com.daaeboul.taskmanagementsystem.service.user;

import com.daaeboul.taskmanagementsystem.exceptions.user.user.DuplicateUsernameException;
import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new User entity. Performs validation (likely related to the uniqueness of the username) before saving the entity to the database.
     *
     * @param user The User object containing the data for the new User.
     * @return The newly created and saved User object.
     * @throws RuntimeException (Or a more specific exception) if validation of the username fails.
     */
    @Transactional
    public User createUser(User user) {
        validateUser(user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Retrieves a User from the database by its ID.
     *
     * @param userId The ID of the User to retrieve.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Retrieves a User from the database based on an exact username, ignoring case differences.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    /**
     * Retrieves a list of Users whose usernames contain the specified text, ignoring case differences.  This is useful for implementing partial matching or autocomplete features.
     *
     * @param username The partial text to search for within usernames.
     * @return A List of User objects with matching usernames.
     */
    public List<User> findByUsernameContaining(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    /**
     * Retrieves a list of all Users from the database.
     *
     * @return A List of User entities representing all existing Users.
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a page of Users from the database, applying pagination parameters.
     *
     * @param pageable The object containing pagination information (page number, page size, sorting).
     * @return A Page of User entities.
     */
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Retrieves a list of Users belonging to a group with the specified group name.
     *
     * @param groupName The name of the group to filter users by.
     * @return A List of Users who are members of the specified group.
     */
    public List<User> findByGroupsGroupName(String groupName) {
        return userRepository.findByGroupsGroupName(groupName);
    }

    /**
     * Retrieves a page of Users belonging to a group with the specified group name, applying pagination parameters.
     *
     * @param groupName The name of the group to filter users by.
     * @param pageable The object containing pagination information (page number, page size, sorting).
     * @return A Page of Users who are members of the specified group.
     */
    public Page<User> findByGroupsGroupName(String groupName, Pageable pageable) {
        return userRepository.findByGroupsGroupName(groupName, pageable);
    }

    /**
     * Retrieves a list of Users based on their associated email address, ignoring case differences. It's likely that there's a related `UserDetails` entity associated with the User.
     *
     * @param email The email address to search for.
     * @return A List of Users with matching email addresses.
     */
    public List<User> findByUserDetailsEmailIgnoreCase(String email) {
        return userRepository.findByUserDetailsEmailIgnoreCase(email);
    }

    /**
     * Counts the number of non-deleted users. This method likely relies on a "soft delete" mechanism where a `deletedAt` timestamp is used to filter out deleted records.
     *
     * @return The count of active (non-deleted) users.
     */
    public long countNonDeletedUsers() {
        return userRepository.count();
    }

    /**
     * Retrieves a list of all Users, including those marked as deleted. This method likely utilizes a custom query to bypass the "soft delete" filtering.
     *
     * @return A List of all Users, regardless of their deleted status.
     */
    public List<User> findAllIncludingDeleted() {
        return userRepository.findAllIncludingDeleted();
    }

    /**
     * Retrieves a page of all Users, including those marked as deleted, applying pagination parameters. This method likely utilizes a custom query to bypass the "soft delete" filtering.
     *
     * @param pageable The object containing pagination information (page number, page size, sorting).
     * @return A Page of all Users, regardless of their deleted status.
     */
    public Page<User> findAllIncludingDeleted(Pageable pageable) {
        return userRepository.findAllIncludingDeleted(pageable);
    }

    /**
     * Updates an existing User with new data.
     *
     * @param updatedUser The User object containing the updated information.
     * @return The updated and saved User object.
     * @throws UserNotFoundException if a User with the provided ID doesn't exist.
     * @throws DuplicateUsernameException if a User with the new username (case-insensitive) already exists.
     */
    @Transactional
    public User updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + updatedUser.getId()));

        // Update fields in existingUser with values from updatedUser
        existingUser.setUsername(updatedUser.getUsername());

        validateUser(updatedUser.getUsername());
        return userRepository.save(existingUser);
    }

    /**
     * Deletes a User. It's likely that this performs a "soft delete" by setting a `deletedAt` timestamp.
     *
     * @param userId The ID of the User to delete.
     * @throws UserNotFoundException if a User with the provided ID doesn't exist.
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Permanently deletes a User record from the database.  Use with caution as this cannot be undone.
     *
     * @param userId  The ID of the User to permanently delete.
     */
    @Transactional
    public void hardDeleteUser(Long userId) {
        // This method directly removes the user from the database, bypassing any soft-delete mechanism.
        userRepository.hardDeleteById(userId);
    }

    /**
     * Helper method used for validating the uniqueness of a User's username (case-insensitive).
     *
     * @param username The username to check for uniqueness.
     * @throws DuplicateUsernameException if a User with the given username already exists.
     */
    private void validateUser(String username) {
        if (userRepository.findByUsernameIgnoreCase(username).isPresent()) {
            throw new DuplicateUsernameException(username);
        }
    }

    /**
     * Retrieves a list of Users who belong to any of the Groups within a provided list.
     *
     * @param groups A List of Group entities to filter users by.
     * @return A List of Users who are members of any of the specified Groups.
     */
    @Transactional
    public List<User> findByGroupsIn(List<Group> groups) {
        return userRepository.findByGroupsIn(groups);
    }
}
