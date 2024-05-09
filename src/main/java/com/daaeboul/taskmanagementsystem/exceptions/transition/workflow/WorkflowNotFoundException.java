package com.daaeboul.taskmanagementsystem.exceptions.transition.workflow;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class WorkflowNotFoundException extends RuntimeException {
    public WorkflowNotFoundException(String message) {
        super(message);
    }

}
