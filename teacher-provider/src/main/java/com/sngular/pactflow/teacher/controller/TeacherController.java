package com.sngular.pactflow.teacher.controller;

import com.sngular.pactflow.teacher.exceptions.TeacherNotFoundException;
import com.sngular.pactflow.teacher.model.Teacher;
import com.sngular.pactflow.teacher.repository.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/teachers")
@Slf4j
public class TeacherController {

    private final TeacherRepository teacherRepository;

    public TeacherController(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        log.info("Creating teacher: {}", teacher);
        Teacher createdTeacher = teacherRepository.save(teacher);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTeacher.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTeacher);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Teacher> getTeachers() {
        log.info("Retrieving all teachers");
        return StreamSupport.stream(teacherRepository.findAll().spliterator(), false).toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Teacher getTeacher(@PathVariable String id) {
        log.info("Retrieving teacher by ID: {}", id);
        return getTeacherById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Teacher> updateTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        log.info("Updating teacher: {}", teacher);
        teacher.setId(getTeacherById(id).getId());
        return ResponseEntity.ok(teacherRepository.save(teacher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        log.info("Deleting teacher by ID: {}", id);
        teacherRepository.deleteById(getTeacherById(id).getId());
        return ResponseEntity.noContent().build();
    }

    private Teacher getTeacherById(final String id) {
        return teacherRepository.findById(id).orElseThrow(TeacherNotFoundException::new);
    }
}
