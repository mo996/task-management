package com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateTaskPriorityNameException extends RuntimeException{
    public DuplicateTaskPriorityNameException(String name) {
        super("A Task-Priority with the name '" + name + "' already exists.");
    }
}
