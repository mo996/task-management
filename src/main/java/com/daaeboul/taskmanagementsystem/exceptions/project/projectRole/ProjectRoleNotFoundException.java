package com.daaeboul.taskmanagementsystem.exceptions.project.projectRole;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ProjectRoleNotFoundException extends RuntimeException {
    public ProjectRoleNotFoundException(String message) {
        super(message);
    }

}
