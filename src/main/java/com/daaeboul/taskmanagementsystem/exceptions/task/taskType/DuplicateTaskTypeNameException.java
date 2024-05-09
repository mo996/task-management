package com.daaeboul.taskmanagementsystem.exceptions.task.taskType;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateTaskTypeNameException extends RuntimeException{
    public DuplicateTaskTypeNameException(String name) {
        super("A Task-Type with the name '" + name + "' already exists.");
    }
}
