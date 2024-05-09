package com.daaeboul.taskmanagementsystem.exceptions.task.taskType;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TaskTypeNotFoundException extends RuntimeException{
    public TaskTypeNotFoundException(String message) {
        super(message);
    }
}
