package com.daaeboul.taskmanagementsystem.exceptions.privileges.role;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicateRoleNameException extends RuntimeException {

    public DuplicateRoleNameException(String roleName) {
        super("A role with the name '" + roleName + "' already exists.");
    }
}
