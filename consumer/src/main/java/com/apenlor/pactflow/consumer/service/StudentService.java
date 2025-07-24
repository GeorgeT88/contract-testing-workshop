package com.apenlor.pactflow.consumer.service;

import com.apenlor.pactflow.consumer.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class StudentService {

    private static final String BASE_URI_STUDENTS = "/students";
    private static final String URI_STUDENT_BY_ID = BASE_URI_STUDENTS + "/{id}";

    private final RestTemplate restTemplate;

    @Autowired
    public StudentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Student createStudent(Student student) {
        return restTemplate.postForObject(BASE_URI_STUDENTS, student, Student.class);
    }

    public Student getStudent(Long id) {
        return restTemplate.getForObject(URI_STUDENT_BY_ID, Student.class, id);
    }

    public List<Student> getStudents() {
        return restTemplate.exchange(BASE_URI_STUDENTS, HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {
        }).getBody();
    }

    public Student updateStudent(Long id, Student student) {
        HttpEntity<Student> entity = new HttpEntity<>(student, new HttpHeaders());
        return restTemplate.exchange(URI_STUDENT_BY_ID, HttpMethod.PUT, entity, Student.class, id).getBody();
    }

    public void deleteStudent(Long id) {
        restTemplate.delete(URI_STUDENT_BY_ID, id);
    }
}