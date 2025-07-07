package com.juangomez.todoapp.config.exception.authentication;

public class InvalidUsernameException extends RuntimeException{
    public InvalidUsernameException(String message) {
        super(message);
    }
}
