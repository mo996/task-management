package com.daaeboul.taskmanagementsystem.repository.user;

import com.daaeboul.taskmanagementsystem.model.user.UserDetails;
import com.daaeboul.taskmanagementsystem.repository.BaseSoftDeletableRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDetailsRepository extends BaseSoftDeletableRepository<UserDetails, Long> {

    /**
     * Retrieves a UserDetails object from the database based on an email address, ignoring case differences.
     *
     * @param email The email address to search for.
     * @return An Optional containing the UserDetails if found, otherwise an empty Optional.
     */
    Optional<UserDetails> findByEmailIgnoreCase(String email);

    /**
     * Retrieves a list of UserDetails objects whose first names contain the specified text, ignoring case differences.
     * This is useful for implementing partial matching or autocomplete features.
     *
     * @param firstNamePart The partial text to search for within first names.
     * @return A List of UserDetails objects with matching first names.
     */
    List<UserDetails> findByFirstNameContainingIgnoreCase(String firstNamePart);

    /**
     * Retrieves a list of UserDetails objects whose last names contain the specified text, ignoring case differences.
     * This is useful for implementing partial matching or autocomplete features.
     *
     * @param lastNamePart The partial text to search for within last names.
     * @return A List of UserDetails objects with matching last names.
     */
    List<UserDetails> findByLastNameContainingIgnoreCase(String lastNamePart);

    /**
     * Retrieves a UserDetails object from the database based on an exact first name and last name combination, ignoring case differences.
     *
     * @param firstName The user's first name to search for.
     * @param lastName The user's last name to search for.
     * @return An Optional containing the UserDetails if found, otherwise an empty Optional.
     */
    Optional<UserDetails> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
}
