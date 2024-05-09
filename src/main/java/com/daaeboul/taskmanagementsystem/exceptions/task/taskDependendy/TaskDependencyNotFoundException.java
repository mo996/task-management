package com.daaeboul.taskmanagementsystem.exceptions.task.taskDependendy;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TaskDependencyNotFoundException extends RuntimeException{
    public TaskDependencyNotFoundException(String message) {
        super(message);
    }
}
