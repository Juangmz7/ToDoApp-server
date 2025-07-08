package com.juangomez.todoapp.config.exception.user;

public class InvalidUsernameException extends RuntimeException{
    public InvalidUsernameException(String message) {
        super(message);
    }
}
