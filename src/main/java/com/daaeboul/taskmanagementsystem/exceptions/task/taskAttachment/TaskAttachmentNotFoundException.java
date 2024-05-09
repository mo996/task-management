package com.daaeboul.taskmanagementsystem.exceptions.task.taskAttachment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TaskAttachmentNotFoundException extends RuntimeException{
    public TaskAttachmentNotFoundException(String message) {
        super(message);
    }
}
