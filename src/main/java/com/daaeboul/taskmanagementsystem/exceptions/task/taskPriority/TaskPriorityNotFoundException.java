package com.daaeboul.taskmanagementsystem.exceptions.task.taskPriority;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TaskPriorityNotFoundException extends RuntimeException{
    public TaskPriorityNotFoundException(String message) {
        super(message);
    }

}
