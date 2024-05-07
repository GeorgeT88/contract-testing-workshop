package com.sngular.pactflow.teacher.controller;

import com.sngular.pactflow.teacher.exceptions.ErrorDetails;
import com.sngular.pactflow.teacher.exceptions.TeacherNotFoundException;
import com.sngular.pactflow.teacher.model.Teacher;
import com.sngular.pactflow.teacher.repository.TeacherRepository;
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
@RequestMapping("/teachers")
@Slf4j
public class TeacherController {

    private final TeacherRepository teacherRepository;

    public TeacherController(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "201", description = "Teacher created successfully",
            content = @Content(schema = @Schema(implementation = Teacher.class)),
            headers = @Header(name = "Location", description = "URL of the created teacher", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "400", description = "Invalid input")
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
    @ApiResponse(responseCode = "200", description = "Teacher found",
            content = @Content(schema = @Schema(implementation = Teacher.class)))
    @ApiResponse(responseCode = "404", description = "Teacher not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public Teacher getTeacher(@PathVariable Long id) {
        log.info("Retrieving teacher by ID: {}", id);
        return getTeacherById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Teacher updated successfully",
            content = @Content(schema = @Schema(implementation = Teacher.class)))
    @ApiResponse(responseCode = "404", description = "Teacher not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        log.info("Updating teacher: {}", teacher);
        teacher.setId(getTeacherById(id).getId());
        return ResponseEntity.ok(teacherRepository.save(teacher));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Teacher deleted")
    @ApiResponse(responseCode = "404", description = "Teacher not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        log.info("Deleting teacher by ID: {}", id);
        teacherRepository.deleteById(getTeacherById(id).getId());
        return ResponseEntity.noContent().build();
    }

    private Teacher getTeacherById(final Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException(id));
    }

}
