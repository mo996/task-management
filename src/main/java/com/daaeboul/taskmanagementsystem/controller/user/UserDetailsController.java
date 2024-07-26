package com.daaeboul.taskmanagementsystem.controller.user;

import com.daaeboul.taskmanagementsystem.exceptions.user.userDetails.UserDetailsNotFoundException;
import com.daaeboul.taskmanagementsystem.model.user.UserDetails;
import com.daaeboul.taskmanagementsystem.service.user.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user-details")
public class UserDetailsController {

    private final UserDetailsService userDetailsService;

    @Autowired
    public UserDetailsController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    public ResponseEntity<UserDetails> createUserDetails(@RequestBody UserDetails userDetails) {
        try {
            UserDetails createdUserDetails = userDetailsService.createUserDetails(userDetails);
            return ResponseEntity.ok(createdUserDetails);
        } catch (UserDetailsNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetails> findUserDetailsById(@PathVariable Long id) {
        Optional<UserDetails> userDetails = userDetailsService.findUserDetailsById(id);
        return userDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    public ResponseEntity<UserDetails> findUserDetailsByEmailIgnoreCase(@RequestParam String email) {
        Optional<UserDetails> userDetails = userDetailsService.findUserDetailsByEmailIgnoreCase(email);
        return userDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/first-name")
    public ResponseEntity<List<UserDetails>> findUserDetailsByFirstNameContainingIgnoreCase(@RequestParam String firstNamePart) {
        List<UserDetails> userDetailsList = userDetailsService.findUserDetailsByFirstNameContainingIgnoreCase(firstNamePart);
        return ResponseEntity.ok(userDetailsList);
    }

    @GetMapping("/search/last-name")
    public ResponseEntity<List<UserDetails>> findUserDetailsByLastNameContainingIgnoreCase(@RequestParam String lastNamePart) {
        List<UserDetails> userDetailsList = userDetailsService.findUserDetailsByLastNameContainingIgnoreCase(lastNamePart);
        return ResponseEntity.ok(userDetailsList);
    }

    @GetMapping("/search/full-name")
    public ResponseEntity<UserDetails> findUserDetailsByFirstNameIgnoreCaseAndLastNameIgnoreCase(@RequestParam String firstName, @RequestParam String lastName) {
        Optional<UserDetails> userDetails = userDetailsService.findUserDetailsByFirstNameIgnoreCaseAndLastNameIgnoreCase(firstName, lastName);
        return userDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDetails> updateUserDetails(@PathVariable Long id, @RequestBody UserDetails userDetails) {
        Optional<UserDetails> optionalUserDetails = userDetailsService.findUserDetailsById(id);
        if (optionalUserDetails.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDetails existingUserDetails = optionalUserDetails.get();
        existingUserDetails.setEmail(userDetails.getEmail());
        existingUserDetails.setFirstName(userDetails.getFirstName());
        existingUserDetails.setLastName(userDetails.getLastName());
        existingUserDetails.setPhoneNumber(userDetails.getPhoneNumber());

        try {
            userDetailsService.updateUserDetails(existingUserDetails);
            return ResponseEntity.ok(existingUserDetails);
        } catch (UserDetailsNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserDetails(@PathVariable Long id) {
        try {
            userDetailsService.deleteUserDetails(id);
            return ResponseEntity.noContent().build();
        } catch (UserDetailsNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDeleteUserDetails(@PathVariable Long id) {
        try {
            userDetailsService.hardDeleteUserDetails(id);
            return ResponseEntity.noContent().build();
        } catch (UserDetailsNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
