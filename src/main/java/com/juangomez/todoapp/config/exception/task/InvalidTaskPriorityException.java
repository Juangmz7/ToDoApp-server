package com.juangomez.todoapp.config.exception.task;

public class InvalidTaskPriorityException extends RuntimeException {
    public InvalidTaskPriorityException(String message) {
        super(message);
    }
}
