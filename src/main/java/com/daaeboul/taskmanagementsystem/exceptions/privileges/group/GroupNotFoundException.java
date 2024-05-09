package com.daaeboul.taskmanagementsystem.exceptions.privileges.group;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(Long groupId) {
        super("Group not found with ID: " + groupId);
    }

    public GroupNotFoundException(String groupName) {
        super("Group not found with name: " + groupName);
    }
}