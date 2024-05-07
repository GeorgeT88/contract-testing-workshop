package com.sngular.pactflow.teacher.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseEntity<Object> handleTeacherNotFound(TeacherNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails("Teacher not found", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDetails);
    }
}