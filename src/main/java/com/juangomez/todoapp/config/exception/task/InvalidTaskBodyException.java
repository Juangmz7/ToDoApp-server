package com.juangomez.todoapp.config.exception.task;

public class InvalidTaskBodyException extends RuntimeException{
    public InvalidTaskBodyException(String message) {
        super(message);
    }
}
