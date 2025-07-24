package com.apenlor.pactflow.student.controller;

import com.apenlor.pactflow.student.annotations.TechnicalDebt;
import com.apenlor.pactflow.student.exceptions.ErrorDetails;
import com.apenlor.pactflow.student.exceptions.StudentNotFoundException;
import com.apenlor.pactflow.student.entities.Student;
import com.apenlor.pactflow.student.repository.StudentRepository;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/students")
@Slf4j
@TechnicalDebt(
        issue = "Controller is using JPA entities directly for request/response bodies.",
        solution = "Introduce Data Transfer Objects (DTOs) for API contracts. " +
                "Create StudentRequestDTO and StudentResponseDTO classes and use a " +
                "mapper (e.g., MapStruct) to convert between DTOs and the Student entity. " +
                "This was omitted to simplify the setup for the contract testing workshop."
)
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "201", description = "Student created successfully",
            content = @Content(schema = @Schema(implementation = Student.class)),
            headers = @Header(name = "Location", description = "URL of the created student", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        log.info("Creating student: {}", student);
        Student createdStudent = studentRepository.save(student);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdStudent.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdStudent);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Student> getStudents() {
        log.info("Retrieving all students");
        return StreamSupport.stream(studentRepository.findAll().spliterator(), false).toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Student found",
            content = @Content(schema = @Schema(implementation = Student.class)))
    @ApiResponse(responseCode = "404", description = "Student not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public Student getStudent(@PathVariable Long id) {
        log.info("Retrieving student by ID: {}", id);
        return getStudentById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Student updated successfully",
            content = @Content(schema = @Schema(implementation = Student.class)))
    @ApiResponse(responseCode = "404", description = "Student not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        log.info("Updating student: {}", student);
        student.setId(getStudentById(id).getId());
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Student deleted")
    @ApiResponse(responseCode = "404", description = "Student not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        log.info("Deleting student by ID: {}", id);
        studentRepository.deleteById(getStudentById(id).getId());
        return ResponseEntity.noContent().build();
    }

    private Student getStudentById(final Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

}
