package com.daaeboul.taskmanagementsystem.exceptions.user.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String username) {
        super("A user with the username '" + username + "' already exists.");
    }
}

