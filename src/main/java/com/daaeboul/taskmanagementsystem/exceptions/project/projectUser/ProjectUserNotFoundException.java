package com.daaeboul.taskmanagementsystem.exceptions.project.projectUser;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ProjectUserNotFoundException extends RuntimeException{

    public ProjectUserNotFoundException(String message) {
        super(message);
    }
}
