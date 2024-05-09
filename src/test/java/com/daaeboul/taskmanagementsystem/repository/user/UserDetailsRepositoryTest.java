package com.daaeboul.taskmanagementsystem.repository.user;

import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.model.user.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserDetailsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setUsername("userTest");
        entityManager.persist(user);

        userDetails = new UserDetails();
        userDetails.setUser(user);
        userDetails.setEmail("test@example.com");
        userDetails.setFirstName("Test");
        userDetails.setLastName("User");
        userDetails.setPhoneNumber("1234567890");
        entityManager.persist(userDetails);
        entityManager.flush();
    }

    @Test
    public void testCreateUserDetailsWithNewUser() {
        User newUser = new User();
        newUser.setUsername("newUserTest");
        entityManager.persist(newUser);
        entityManager.flush();

        UserDetails newUserDetails = new UserDetails();
        newUserDetails.setUser(newUser);
        newUserDetails.setEmail("newuser@example.com");
        newUserDetails.setFirstName("New");
        newUserDetails.setLastName("UserDetails");
        newUserDetails.setPhoneNumber("0987654321");

        UserDetails savedUserDetails = userDetailsRepository.save(newUserDetails);

        assertThat(savedUserDetails).isNotNull();
        assertThat(savedUserDetails.getId()).isNotNull();
        assertThat(savedUserDetails.getUser().getId()).isEqualTo(newUser.getId());
    }

    @Test
    public void whenFindByEmail_thenRetrieveUserDetails() {
        UserDetails foundUserDetails = userDetailsRepository.findById(userDetails.getId()).orElse(null);

        assertThat(foundUserDetails).isNotNull();
        assertThat(foundUserDetails.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void whenUpdateUserDetails_thenUpdated() {
        userDetails.setFirstName("UpdatedName");
        userDetailsRepository.save(userDetails);

        UserDetails updatedUserDetails = entityManager.find(UserDetails.class, userDetails.getId());
        assertThat(updatedUserDetails.getFirstName()).isEqualTo("UpdatedName");
    }

    @Test
    public void whenSoftDeleteUserDetails_thenNotRetrievable() {
        userDetailsRepository.deleteById(userDetails.getId());
        entityManager.flush();

        UserDetails deletedUserDetails = entityManager.find(UserDetails.class, userDetails.getId());

        assertThat(deletedUserDetails).isNotNull();
        assertThat(deletedUserDetails.getDeletedAt()).isNotNull();
    }

    @Test
    public void whenSaveWithInvalidEmail_thenThrowException() {
        UserDetails invalidUserDetails = new UserDetails();
        invalidUserDetails.setEmail("invalidEmail");
        assertThrows(ConstraintViolationException.class, () -> userDetailsRepository.saveAndFlush(invalidUserDetails));
    }

    @Test
    void findByFirstNameContainingIgnoreCaseTest() {
        UserDetails partialMatch1 = new UserDetails("Tessie", "Doe", "tessie@example.com", entityManager.persist(new User("tessieUser")));
        UserDetails partialMatch2 = new UserDetails("tesKate", "Testerton", "testerton@email.com", entityManager.persist(new User("testertonUser")));
        entityManager.persist(partialMatch1);
        entityManager.persist(partialMatch2);
        entityManager.flush();

        List<UserDetails> results = userDetailsRepository.findByFirstNameContainingIgnoreCase("Tes");

        assertThat(results).hasSize(3)
                .anyMatch(u -> u.getFirstName().equals("Test"))
                .anyMatch(u -> u.getFirstName().equals("Tessie"))
                .anyMatch(u -> u.getFirstName().equals("tesKate"));
    }

    @Test
    void findByLastNameContainingIgnoreCaseTest() {
        UserDetails partialMatch1 = new UserDetails("John", "Userton", "userton@example.com", entityManager.persist(new User("usertonUser")));
        entityManager.persist(partialMatch1);
        entityManager.flush();

        List<UserDetails> results = userDetailsRepository.findByLastNameContainingIgnoreCase("er");

        assertThat(results).hasSize(2)
                .anyMatch(u -> u.getLastName().equals("User"))
                .anyMatch(u -> u.getLastName().equals("Userton"));
    }

    @Test
    void findByFirstNameIgnoreCaseAndLastNameIgnoreCaseTest() {
        UserDetails extraUserDetails = new UserDetails("Test2", "User", "different@example.com", entityManager.persist(new User("testUser2")));
        entityManager.persist(extraUserDetails);
        entityManager.flush();

        Optional<UserDetails> result = userDetailsRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase("Test", "User");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
        void findByEmailIgnoreCaseTest() {
        // Setup an additional UserDetails with a unique email
        UserDetails testDetails = new UserDetails("Test3", "User3", "unique@email.com", entityManager.persist(new User("uniqueUser")));
        entityManager.persist(testDetails);
        entityManager.flush();

        Optional<UserDetails> foundUserDetails = userDetailsRepository.findByEmailIgnoreCase("UNIQUE@EMAIL.COM");

        assertThat(foundUserDetails).isPresent();
        assertThat(foundUserDetails.get().getEmail()).isEqualTo("unique@email.com");
        assertThat(foundUserDetails.get().getFirstName()).isEqualTo("Test3");
        assertThat(foundUserDetails.get().getLastName()).isEqualTo("User3");

    }
}

