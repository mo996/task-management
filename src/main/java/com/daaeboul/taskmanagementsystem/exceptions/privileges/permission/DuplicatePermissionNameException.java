package com.daaeboul.taskmanagementsystem.exceptions.privileges.permission;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicatePermissionNameException extends RuntimeException {

    public DuplicatePermissionNameException(String permissionName) {
        super("A permission with the name '" + permissionName + "' already exists.");
    }
}
