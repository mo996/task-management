package com.daaeboul.taskmanagementsystem.exceptions.task.taskComment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TaskCommentNotFoundException extends RuntimeException{
    public TaskCommentNotFoundException(String message) {
        super(message);
    }
}
