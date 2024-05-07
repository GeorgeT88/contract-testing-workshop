package com.sngular.pactflow.teacher.exceptions;

public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(Long id) {
        super(String.format("Teacher with id %d does not exist", id));
    }
}

