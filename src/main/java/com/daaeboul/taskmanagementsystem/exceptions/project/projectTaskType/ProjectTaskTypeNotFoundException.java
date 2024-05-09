package com.daaeboul.taskmanagementsystem.exceptions.project.projectTaskType;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ProjectTaskTypeNotFoundException extends RuntimeException{
    public ProjectTaskTypeNotFoundException(String message) {
        super(message);
    }
}
