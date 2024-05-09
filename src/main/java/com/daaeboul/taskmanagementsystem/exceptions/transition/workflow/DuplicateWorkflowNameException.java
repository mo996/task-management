package com.daaeboul.taskmanagementsystem.exceptions.transition.workflow;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicateWorkflowNameException extends RuntimeException {
    public DuplicateWorkflowNameException(String name) {
        super("A Workflow with the name '" + name + "' already exists.");
    }

}
