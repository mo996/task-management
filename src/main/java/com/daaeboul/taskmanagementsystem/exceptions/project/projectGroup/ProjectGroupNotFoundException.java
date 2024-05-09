package com.daaeboul.taskmanagementsystem.exceptions.project.projectGroup;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Supplier;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ProjectGroupNotFoundException extends RuntimeException {

    public ProjectGroupNotFoundException(String message) {
        super(message);
    }
}
