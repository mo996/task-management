package com.daaeboul.taskmanagementsystem.exceptions.transition.workflowStep;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class WorkflowStepNotFoundException extends RuntimeException {
    public WorkflowStepNotFoundException(String message) {
        super(message);
    }

}
