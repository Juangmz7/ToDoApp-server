package com.juangomez.todoapp.config.exception.authentication;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException(String message) {
        super(message);
    }
}
