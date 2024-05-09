package com.daaeboul.taskmanagementsystem.repository.user;

import com.daaeboul.taskmanagementsystem.model.privileges.Group;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.repository.BaseSoftDeletableRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseSoftDeletableRepository<User, Long> {

    /**
     * Retrieves a User from the database based on a username, ignoring case differences.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Retrieves a list of Users belonging to a group with the specified group name.
     *
     * @param groupName The name of the group to filter users by.
     * @return A List of Users who are members of the specified group.
     */
    List<User> findByGroupsGroupName(String groupName);

    /**
     * Retrieves a page of Users belonging to a group with the specified group name, applying pagination parameters.
     *
     * @param groupName The name of the group to filter users by.
     * @param pageable  The object containing pagination information (page number, page size, sorting).
     * @return A Page of Users who are members of the specified group.
     */
    Page<User> findByGroupsGroupName(String groupName, Pageable pageable);

    /**
     * Retrieves a list of Users based on their email addresses, ignoring case differences.
     * It's likely that there's a related 'UserDetails' entity associated with the User.
     *
     * @param email The email address to search for.
     * @return A List of Users with matching email addresses.
     */
    List<User> findByUserDetailsEmailIgnoreCase(String email);

    /**
     * Counts the number of non-deleted users, likely by leveraging a @Filter annotation to exclude 'soft-deleted' entities.
     *
     * @return The count of active (non-deleted) users.
     */
    long count();

    /**
     * Retrieves a list of all non-deleted users, taking advantage of the default findAll() behavior and a 'SoftDeletable' mechanism that filters out deleted records.
     *
     * @return A List of active (non-deleted) users.
     */
    @Override
    @NonNull
    List<User> findAll();

    /**
     * Retrieves a page of non-deleted users, applying pagination and sorting parameters. This method overrides the default behavior, likely leveraging a 'SoftDeletable' mechanism that filters out deleted records.
     *
     * @param pageable The object containing pagination information (page number, page size, sorting).
     * @return A Page of active (non-deleted) users.
     */
    @Override
    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);

    /**
     * Retrieves a list of all users, including those marked as deleted. This method uses a custom query to bypass the 'SoftDeletable' filtering mechanism.
     *
     * @return A List of all users, regardless of their deleted status.
     */
    @Query("select u from User u")
    List<User> findAllIncludingDeleted();

    /**
     * Retrieves a page of all users, including those marked as deleted, applying pagination and sorting parameters. This method uses a custom query to bypass the 'SoftDeletable' filtering mechanism.
     *
     * @param pageable The object containing pagination information (page number, page size, sorting).
     * @return A Page of all users, regardless of their deleted status.
     */
    @Query("select u from User u")
    Page<User> findAllIncludingDeleted(Pageable pageable);

    /**
     * Retrieves a list of Users whose usernames contain the specified text, ignoring case differences.
     * This is useful for implementing partial matching or autocomplete features.
     *
     * @param usernamePart The text to search for within usernames.
     * @return A List of User objects with matching usernames.
     */
    List<User> findByUsernameContainingIgnoreCase(String usernamePart);

    /**
     * Retrieves a list of Users who belong to any of the Groups provided in the list.
     *
     * @param groups A List of Group entities to filter users by.
     * @return A List of Users who are members of the specified Groups.
     */
    List<User> findByGroupsIn(List<Group> groups);
}


