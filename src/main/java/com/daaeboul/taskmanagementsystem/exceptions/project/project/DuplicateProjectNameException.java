package com.daaeboul.taskmanagementsystem.exceptions.project.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT) // 409 Conflict is often used for duplicate resource creation attempts
public class DuplicateProjectNameException extends RuntimeException {
    public DuplicateProjectNameException(String projectName) {
        super("A project with the name '" + projectName + "' already exists.");
    }
}