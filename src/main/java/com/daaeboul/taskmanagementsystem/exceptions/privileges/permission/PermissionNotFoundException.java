package com.daaeboul.taskmanagementsystem.exceptions.privileges.permission;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PermissionNotFoundException extends RuntimeException {

    public PermissionNotFoundException(Long id) {
        super("Permission with ID " + id + " not found");
    }

    // You can also overload the constructor to support different types of input parameters
    public PermissionNotFoundException(String permissionName) {
        super("Permission with name '" + permissionName + "' not found");
    }
}
