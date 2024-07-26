package com.daaeboul.taskmanagementsystem.controller.user;

import com.daaeboul.taskmanagementsystem.exceptions.user.user.UserNotFoundException;
import com.daaeboul.taskmanagementsystem.exceptions.user.userAuth.UserAuthNotFoundException;
import com.daaeboul.taskmanagementsystem.model.user.UserAuth;
import com.daaeboul.taskmanagementsystem.service.user.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user-auths")
public class UserAuthController {

    private final UserAuthService userAuthService;

    @Autowired
    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping
    public ResponseEntity<UserAuth> createUserAuth(@RequestBody UserAuth userAuth) {
        try {
            UserAuth createdUserAuth = userAuthService.createUserAuth(userAuth);
            return ResponseEntity.ok(createdUserAuth);
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAuth> findUserAuthById(@PathVariable Long id) {
        Optional<UserAuth> userAuth = userAuthService.findUserAuthById(id);
        return userAuth.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/auth-token")
    public ResponseEntity<UserAuth> findUserAuthByAuthToken(@RequestParam String authToken) {
        Optional<UserAuth> userAuth = userAuthService.findUserAuthByAuthToken(authToken);
        return userAuth.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAuth> updateUserAuth(@PathVariable Long id, @RequestBody UserAuth userAuthDetails) {
        Optional<UserAuth> optionalUserAuth = userAuthService.findUserAuthById(id);
        if (optionalUserAuth.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserAuth existingUserAuth = optionalUserAuth.get();
        existingUserAuth.setPasswordHash(userAuthDetails.getPasswordHash());
        existingUserAuth.setAuthToken(userAuthDetails.getAuthToken());
        existingUserAuth.setLastLoginAt(userAuthDetails.getLastLoginAt());
        existingUserAuth.setFailedLoginAttempts(userAuthDetails.getFailedLoginAttempts());
        existingUserAuth.setLocked(userAuthDetails.isLocked());

        try {
            userAuthService.updateUserAuth(existingUserAuth);
            return ResponseEntity.ok(existingUserAuth);
        } catch (UserAuthNotFoundException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAuth(@PathVariable Long id) {
        try {
            userAuthService.deleteUserAuth(id);
            return ResponseEntity.noContent().build();
        } catch (UserAuthNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/hard-delete/{id}")
    public ResponseEntity<Void> hardDeleteUserAuth(@PathVariable Long id) {
        try {
            userAuthService.hardDeleteUserAuth(id);
            return ResponseEntity.noContent().build();
        } catch (UserAuthNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAuth> findUserAuthByUserId(@PathVariable Long userId) {
        Optional<UserAuth> userAuth = userAuthService.findUserAuthByUserId(userId);
        return userAuth.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/locked-accounts")
    public ResponseEntity<List<UserAuth>> findLockedAccounts() {
        List<UserAuth> lockedAccounts = userAuthService.findLockedAccounts();
        return ResponseEntity.ok(lockedAccounts);
    }

    @PutMapping("/update-auth-token/{userId}")
    public ResponseEntity<Void> updateAuthTokenByUserId(@PathVariable Long userId, @RequestParam String authToken) {
        try {
            userAuthService.updateAuthTokenByUserId(userId, authToken);
            return ResponseEntity.noContent().build();
        } catch (UserAuthNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update-password/{userId}")
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId, @RequestParam String newPasswordHash) {
        userAuthService.updatePassword(userId, newPasswordHash);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/lock-account/{userId}")
    public ResponseEntity<Void> lockUserAccount(@PathVariable Long userId) {
        userAuthService.lockUserAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/unlock-account/{userId}")
    public ResponseEntity<Void> unlockUserAccount(@PathVariable Long userId) {
        userAuthService.unlockUserAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-last-login/{userId}")
    public ResponseEntity<Void> updateLastLoginTime(@PathVariable Long userId) {
        userAuthService.updateLastLoginTime(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/increment-failed-login-attempts/{userId}")
    public ResponseEntity<Void> incrementFailedLoginAttempts(@PathVariable Long userId) {
        userAuthService.incrementFailedLoginAttempts(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reset-failed-login-attempts/{userId}")
    public ResponseEntity<Void> resetFailedLoginAttempts(@PathVariable Long userId) {
        userAuthService.resetFailedLoginAttempts(userId);
        return ResponseEntity.noContent().build();
    }
}
