package com.juangomez.todoapp.config.exception;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.juangomez.todoapp.config.exception.task.InvalidTaskBodyException;
import com.juangomez.todoapp.config.exception.task.InvalidTaskPriorityException;
import com.juangomez.todoapp.config.exception.task.TaskNotFoundException;
import com.juangomez.todoapp.config.exception.user.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

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
            InvalidTaskBodyException.class,
            InvalidFormatException.class,
            InvalidTaskPriorityException.class,
            MethodArgumentNotValidException.class,      // Json body validation
            ConstraintViolationException.class,         // Request parameters validation
            MethodArgumentTypeMismatchException.class,   // When cannot convert a request parameter to object
            MissingServletRequestParameterException.class,
            InvalidAudioFormatException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException exception, WebRequest request) {
       return createErrorResponseEntity(exception, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            IllegalStateException.class,
            ExpiredJwtException.class
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

    // Other exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(RuntimeException ex, WebRequest request) {
        return createErrorResponseEntity(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
