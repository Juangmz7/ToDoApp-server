package com.juangomez.todoapp.config.exception.authentication;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(String message) {
        super(message);
    }
}
