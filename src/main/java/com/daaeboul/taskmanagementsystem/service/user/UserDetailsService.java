package com.daaeboul.taskmanagementsystem.service.user;


import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.user.userDetails.UserDetailsNotFoundException;
import com.daaeboul.taskmanagementsystem.model.user.UserDetails;
import com.daaeboul.taskmanagementsystem.repository.user.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public UserDetailsService(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    /**
     * Creates a new UserDetails entity. Enforces that a valid User is associated and has an ID before saving.
     *
     * @param userDetails The UserDetails object containing the data for the new UserDetails.
     * @return The newly created and saved UserDetails object.
     * @throws UserNotFoundException if the User associated with the UserDetails is not found or doesn't have a valid ID.
     */
    @Transactional
    public UserDetails createUserDetails(UserDetails userDetails) {
        if (userDetails.getUser() == null || userDetails.getUser().getId() == null) {
            throw new UserNotFoundException("User must be set and have a valid ID");
        }
        return userDetailsRepository.save(userDetails);
    }

    /**
     * Retrieves a UserDetails from the database by its ID.
     *
     * @param userDetailsId The ID of the UserDetails to retrieve.
     * @return An Optional containing the UserDetails if found, otherwise an empty Optional.
     */
    public Optional<UserDetails> findUserDetailsById(Long userDetailsId) {
        return userDetailsRepository.findById(userDetailsId);
    }

    /**
     * Retrieves a UserDetails from the database based on an email address, ignoring case differences.
     *
     * @param email The email address to search for.
     * @return An Optional containing the UserDetails if found, otherwise an empty Optional.
     */
    public Optional<UserDetails> findUserDetailsByEmailIgnoreCase(String email) {
        return userDetailsRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Retrieves a list of UserDetails objects whose first names contain the specified text, ignoring case differences. This is useful for implementing partial matching or autocomplete features.
     *
     * @param firstNamePart The partial text to search for within first names.
     * @return A List of UserDetails objects with matching first names.
     */
    public List<UserDetails> findUserDetailsByFirstNameContainingIgnoreCase(String firstNamePart) {
        return userDetailsRepository.findByFirstNameContainingIgnoreCase(firstNamePart);
    }

    /**
     * Retrieves a list of UserDetails objects whose last names contain the specified text, ignoring case differences.  This is useful for implementing partial matching or autocomplete features.
     *
     * @param lastNamePart The partial text to search for within last names.
     * @return A List of UserDetails objects with matching last names.
     */
    public List<UserDetails> findUserDetailsByLastNameContainingIgnoreCase(String lastNamePart) {
        return userDetailsRepository.findByLastNameContainingIgnoreCase(lastNamePart);
    }

    /**
     * Retrieves a UserDetails object from the database based on an exact first name and last name combination, ignoring case differences.
     *
     * @param firstName The user's first name to search for.
     * @param lastName The user's last name to search for.
     * @return An Optional containing the UserDetails if found, otherwise an empty Optional.
     */
    public Optional<UserDetails> findUserDetailsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName) {
        return userDetailsRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
    }

    /**
     * Updates an existing UserDetails record with new data.
     *
     * @param updatedUserDetails The UserDetails object containing the updated information.
     * @throws UserDetailsNotFoundException if a UserDetails record with the provided ID doesn't exist.
     */
    @Transactional
    public void updateUserDetails(UserDetails updatedUserDetails) {
        UserDetails existingUserDetails = userDetailsRepository.findById(updatedUserDetails.getId())
                .orElseThrow(() -> new UserDetailsNotFoundException("UserDetails not found with id " + updatedUserDetails.getId()));

        existingUserDetails.setEmail(updatedUserDetails.getEmail());
        existingUserDetails.setFirstName(updatedUserDetails.getFirstName());
        existingUserDetails.setLastName(updatedUserDetails.getLastName());
        existingUserDetails.setPhoneNumber(updatedUserDetails.getPhoneNumber());

        userDetailsRepository.save(existingUserDetails);
    }

    /**
     * Deletes a UserDetails record. It's likely that this performs a "soft delete" by setting a `deletedAt` timestamp.
     *
     * @param userDetailsId The ID of the UserDetails record to delete.
     * @throws UserDetailsNotFoundException if a UserDetails entry with the provided ID doesn't exist.
     */
    @Transactional
    public void deleteUserDetails(Long userDetailsId) {
        if (!userDetailsRepository.existsById(userDetailsId)) {
            throw new UserDetailsNotFoundException("UserDetails not found with id " + userDetailsId);
        }
        userDetailsRepository.deleteById(userDetailsId);
    }

    /**
     * Permanently deletes a UserDetails record from the database.  Use with caution as this cannot be undone.
     *
     * @param userDetailsId  The ID of the UserDetails to permanently delete.
     * @throws UserDetailsNotFoundException if a UserDetails entry with the provided ID doesn't exist.
     */
    @Transactional
    public void hardDeleteUserDetails(Long userDetailsId) {
        if (!userDetailsRepository.existsById(userDetailsId)) {
            throw new UserDetailsNotFoundException("UserDetails not found with id " + userDetailsId);
        }
        userDetailsRepository.hardDeleteById(userDetailsId);
    }
}