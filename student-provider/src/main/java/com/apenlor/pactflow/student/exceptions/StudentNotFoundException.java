package com.apenlor.pactflow.student.exceptions;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long id) {
        super(String.format("Student with id %d does not exist", id));
    }
}

