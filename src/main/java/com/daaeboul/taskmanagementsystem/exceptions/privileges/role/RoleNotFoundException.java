package com.daaeboul.taskmanagementsystem.exceptions.privileges.role;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(Long id) {
        super("Role with ID " + id + " not found");
    }

    public RoleNotFoundException(String roleName) {
        super("Role with name '" + roleName + "' not found");
    }

    // Optionally, you can add more constructors here if needed
}