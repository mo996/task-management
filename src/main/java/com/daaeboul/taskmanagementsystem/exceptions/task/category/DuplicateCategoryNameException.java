package com.daaeboul.taskmanagementsystem.exceptions.task.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateCategoryNameException extends RuntimeException{
    public DuplicateCategoryNameException(String name) {
        super("A Category with the name '" + name + "' already exists.");
    }
}
