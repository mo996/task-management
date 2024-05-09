package com.daaeboul.taskmanagementsystem.exceptions.transition.status;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DuplicateStatusNameException extends RuntimeException{
    public DuplicateStatusNameException(String name) {
        super("A Status with the name '" + name + "' already exists.");
    }
}
