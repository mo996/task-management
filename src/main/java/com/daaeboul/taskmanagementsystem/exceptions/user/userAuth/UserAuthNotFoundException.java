package com.daaeboul.taskmanagementsystem.exceptions.user.userAuth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserAuthNotFoundException extends RuntimeException {
    public UserAuthNotFoundException(String message) {
        super(message);
    }
}
