package com.juangomez.todoapp.config.exception;


import com.juangomez.todoapp.config.exception.task.InvalidTaskIdException;
import com.juangomez.todoapp.config.exception.task.TaskNotFoundException;
import com.juangomez.todoapp.config.exception.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler extends RuntimeException{

    // Helper method to create an ErrorResponse
    private ResponseEntity<ErrorResponse> createErrorResponseEntity(
            RuntimeException exception,
            WebRequest request,
            HttpStatus status
    ) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler({
            DuplicateUsernameException.class,
            InvalidEmailException.class,
            InvalidPasswordException.class,
            InvalidUsernameException.class,
            InvalidUserException.class,
            BadCredentialsException.class,
            InvalidTaskIdException.class,
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException exception, WebRequest request) {
       return createErrorResponseEntity(exception, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            IllegalStateException.class,
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            TaskNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException exception, WebRequest request) {
        return createErrorResponseEntity(exception, request, HttpStatus.NOT_FOUND);
    }

}
