package com.daaeboul.taskmanagementsystem.controller.user;

import com.daaeboul.taskmanagementsystem.exceptions.user.user.DuplicateUsernameException;
import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.model.user.User;
import com.daaeboul.taskmanagementsystem.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (DuplicateUsernameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Long id) {
        Optional<User> user = userService.findUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<User>> findAllUsers(Pageable pageable) {
        Page<User> users = userService.findAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> findByUsernameContaining(@RequestParam String username) {
        List<User> users = userService.findByUsernameContaining(username);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userService.findUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = optionalUser.get();
        existingUser.setUsername(userDetails.getUsername());

        try {
            User updatedUser = userService.updateUser(existingUser);
            return ResponseEntity.ok(updatedUser);
        } catch (DuplicateUsernameException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/groups/{groupName}")
    public ResponseEntity<List<User>> findByGroupsGroupName(@PathVariable String groupName) {
        List<User> users = userService.findByGroupsGroupName(groupName);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/groups/{groupName}/page")
    public ResponseEntity<Page<User>> findByGroupsGroupName(@PathVariable String groupName, Pageable pageable) {
        Page<User> users = userService.findByGroupsGroupName(groupName, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email")
    public ResponseEntity<List<User>> findByUserDetailsEmailIgnoreCase(@RequestParam String email) {
        List<User> users = userService.findByUserDetailsEmailIgnoreCase(email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countNonDeletedUsers() {
        long count = userService.countNonDeletedUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/all-including-deleted")
    public ResponseEntity<List<User>> findAllIncludingDeleted() {
        List<User> users = userService.findAllIncludingDeleted();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all-including-deleted/page")
    public ResponseEntity<Page<User>> findAllIncludingDeleted(Pageable pageable) {
        Page<User> users = userService.findAllIncludingDeleted(pageable);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDeleteUser(@PathVariable Long id) {
        try {
            userService.hardDeleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
