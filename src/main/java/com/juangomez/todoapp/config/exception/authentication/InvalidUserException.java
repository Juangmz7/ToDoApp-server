package com.juangomez.todoapp.config.exception.authentication;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException(String message) {
        super(message);
    }
}
