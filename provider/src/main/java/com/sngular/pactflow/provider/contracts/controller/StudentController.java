package com.sngular.pactflow.provider.contracts.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.StreamSupport;

import com.sngular.pactflow.provider.contracts.exceptions.StudentNotFoundException;
import com.sngular.pactflow.provider.contracts.model.Student;
import com.sngular.pactflow.provider.contracts.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/students")
@Slf4j
public class StudentController {

  private final StudentRepository studentRepository;

  public StudentController(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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
  public Student getStudent(@PathVariable String id) {
    log.info("Retrieving student by ID: {}", id);
    return getStudentById(id);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student student) {
    log.info("Updating student: {}", student);
    student.setId(getStudentById(id).getId());
    return ResponseEntity.ok(studentRepository.save(student));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
    log.info("Deleting student by ID: {}", id);
    studentRepository.deleteById(getStudentById(id).getId());
    return ResponseEntity.noContent().build();
  }

  private Student getStudentById(final String id) {
    return studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
  }
}
