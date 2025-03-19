package com.taskmanager.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiError> handleTaskNotFoundException(
            TaskNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Error",
                "Wrong password",
                request.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Invalid input data",
                request.getDescription(false),
                errors
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Invalid input data",
                request.getDescription(false),
                errors
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(
            Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getDescription(false)
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 