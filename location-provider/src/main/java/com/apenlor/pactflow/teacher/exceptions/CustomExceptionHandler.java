package com.apenlor.pactflow.teacher.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<Object> handleLocationNotFound(LocationNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails("Location not found", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDetails);
    }
}