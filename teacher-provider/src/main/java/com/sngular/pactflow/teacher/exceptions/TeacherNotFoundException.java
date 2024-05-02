package com.sngular.pactflow.teacher.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Teacher not found")
public class TeacherNotFoundException extends RuntimeException {
}

