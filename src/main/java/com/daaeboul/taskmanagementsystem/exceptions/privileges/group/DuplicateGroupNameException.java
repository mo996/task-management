package com.daaeboul.taskmanagementsystem.exceptions.privileges.group;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicateGroupNameException extends RuntimeException {
    public DuplicateGroupNameException(String groupName) {
        super("Group with name '" + groupName + "' already exists.");
    }
}
