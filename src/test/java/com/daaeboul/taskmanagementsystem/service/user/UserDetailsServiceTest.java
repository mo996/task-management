package com.daaeboul.taskmanagementsystem.service.user;

import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.user.userDetails.UserDetailsNotFoundException;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.model.user.UserDetails;
import com.daaeboul.taskmanagementsystem.repository.user.UserDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @InjectMocks
    private UserDetailsService userDetailsService;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    private User createUserWithId(Long id) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private UserDetails createUserDetailsWithId(Long id, User user) {
        UserDetails userDetails = new UserDetails();
        ReflectionTestUtils.setField(userDetails, "id", id);
        userDetails.setUser(user);
        return userDetails;
    }

    @Test
    void shouldCreateUserDetails() {
        User user = createUserWithId(1L);
        UserDetails newUserDetails = createUserDetailsWithId(null, user);
        newUserDetails.setEmail("test@example.com");
        when(userDetailsRepository.save(newUserDetails)).thenReturn(newUserDetails);

        UserDetails createdUserDetails = userDetailsService.createUserDetails(newUserDetails);

        assertNotNull(createdUserDetails);
        assertEquals(user, createdUserDetails.getUser());
        assertEquals("test@example.com", createdUserDetails.getEmail());
        verify(userDetailsRepository).save(newUserDetails);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotSet() {
        UserDetails newUserDetails = createUserDetailsWithId(null, null);
        newUserDetails.setEmail("test@example.com");

        assertThrows(UserNotFoundException.class, () -> userDetailsService.createUserDetails(newUserDetails));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        User user = new User();
        UserDetails newUserDetails = createUserDetailsWithId(null, user);
        newUserDetails.setEmail("test@example.com");

        assertThrows(UserNotFoundException.class, () -> userDetailsService.createUserDetails(newUserDetails));
    }

    @Test
    void shouldFindUserDetailsById() {
        Long userDetailsId = 1L;
        User user = createUserWithId(1L);
        UserDetails expectedUserDetails = createUserDetailsWithId(userDetailsId, user);
        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.of(expectedUserDetails));

        Optional<UserDetails> foundUserDetails = userDetailsService.findUserDetailsById(userDetailsId);

        assertTrue(foundUserDetails.isPresent());
        assertEquals(expectedUserDetails, foundUserDetails.get());
    }

    @Test
    void shouldUpdateUserDetailsSuccessfully() {
        User user = createUserWithId(1L);
        UserDetails existingUserDetails = createUserDetailsWithId(1L, user);
        existingUserDetails.setEmail("original@example.com");

        UserDetails updatedUserDetails = createUserDetailsWithId(1L, user);
        updatedUserDetails.setEmail("updated@example.com");
        when(userDetailsRepository.findById(1L)).thenReturn(Optional.of(existingUserDetails));
        when(userDetailsRepository.save(any(UserDetails.class))).thenReturn(updatedUserDetails);

        userDetailsService.updateUserDetails(updatedUserDetails);

        verify(userDetailsRepository).save(any(UserDetails.class));
    }

    @Test
    void shouldThrowExceptionWhenUserDetailsDoesNotExistForUpdate() {
        Long userDetailsId = 999L;
        UserDetails updatedUserDetails = createUserDetailsWithId(userDetailsId, null);
        when(userDetailsRepository.findById(userDetailsId)).thenReturn(Optional.empty());

        assertThrows(UserDetailsNotFoundException.class, () -> userDetailsService.updateUserDetails(updatedUserDetails));
    }

    @Test
    void shouldDeleteUserDetailsSuccessfully() {
        Long userDetailsId = 1L;
        when(userDetailsRepository.existsById(userDetailsId)).thenReturn(true);

        userDetailsService.deleteUserDetails(userDetailsId);

        verify(userDetailsRepository).deleteById(userDetailsId);
    }

    @Test
    void shouldThrowExceptionWhenUserDetailsDoesNotExistForDelete() {
        Long userDetailsId = 999L;
        when(userDetailsRepository.existsById(userDetailsId)).thenReturn(false);

        assertThrows(UserDetailsNotFoundException.class, () -> userDetailsService.deleteUserDetails(userDetailsId));
    }

    @Test
    void shouldHardDeleteUserDetailsSuccessfully() {
        Long userDetailsId = 1L;
        when(userDetailsRepository.existsById(userDetailsId)).thenReturn(true);
        doNothing().when(userDetailsRepository).hardDeleteById(userDetailsId);

        assertDoesNotThrow(() -> userDetailsService.hardDeleteUserDetails(userDetailsId));

        verify(userDetailsRepository, times(1)).hardDeleteById(userDetailsId);
    }

    @Test
    void shouldThrowExceptionWhenUserDetailsDoesNotExistForHardDelete() {
        Long userDetailsId = 999L;
        when(userDetailsRepository.existsById(userDetailsId)).thenReturn(false);

        assertThrows(UserDetailsNotFoundException.class, () -> userDetailsService.hardDeleteUserDetails(userDetailsId));
    }

    @Test
    void findByFirstNameContainingIgnoreCaseTest() {
        String firstNamePart = "Jo";
        List<UserDetails> userDetailsList = Arrays.asList(new UserDetails());
        when(userDetailsRepository.findByFirstNameContainingIgnoreCase(firstNamePart)).thenReturn(userDetailsList);

        List<UserDetails> foundUserDetails = userDetailsService.findUserDetailsByFirstNameContainingIgnoreCase(firstNamePart);

        assertNotNull(foundUserDetails);
        assertEquals(1, foundUserDetails.size());
        verify(userDetailsRepository).findByFirstNameContainingIgnoreCase(firstNamePart);
    }

    @Test
    void findByLastNameContainingIgnoreCaseTest() {
        String lastNamePart = "oe";
        List<UserDetails> userDetailsList = Arrays.asList(new UserDetails());
        when(userDetailsRepository.findByLastNameContainingIgnoreCase(lastNamePart)).thenReturn(userDetailsList);

        List<UserDetails> foundUserDetails = userDetailsService.findUserDetailsByLastNameContainingIgnoreCase(lastNamePart);

        assertNotNull(foundUserDetails);
        assertEquals(1, foundUserDetails.size());
        verify(userDetailsRepository).findByLastNameContainingIgnoreCase(lastNamePart);
    }

    @Test
    void findByFirstNameIgnoreCaseAndLastNameIgnoreCaseTest() {
        String firstName = "John";
        String lastName = "Doe";
        UserDetails userDetails = new UserDetails();
        when(userDetailsRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName)).thenReturn(Optional.of(userDetails));

        Optional<UserDetails> foundUserDetails = userDetailsService.findUserDetailsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);

        assertTrue(foundUserDetails.isPresent());
        assertEquals(userDetails, foundUserDetails.get());
        verify(userDetailsRepository).findByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
    }

    @Test
    void findUserDetailsByEmailIgnoreCaseTest() {
        String email = "test@EXAMPLE.com";
        UserDetails expectedUserDetails = createUserDetailsWithId(1L, createUserWithId(1L));
        expectedUserDetails.setEmail(email.toUpperCase());

        when(userDetailsRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(expectedUserDetails));

        Optional<UserDetails> foundUserDetails = userDetailsService.findUserDetailsByEmailIgnoreCase(email);

        assertTrue(foundUserDetails.isPresent());
        assertEquals(expectedUserDetails, foundUserDetails.get());
        verify(userDetailsRepository).findByEmailIgnoreCase(email);
    }

}