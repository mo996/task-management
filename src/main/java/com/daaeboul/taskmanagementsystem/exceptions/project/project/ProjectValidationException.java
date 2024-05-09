package com.daaeboul.taskmanagementsystem.exceptions.project.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ProjectValidationException extends RuntimeException {
    public ProjectValidationException(String message) {
        super(message);
    }
}