package com.sngular.pactflow.consumer.service;

import java.util.List;

import com.sngular.pactflow.consumer.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StudentService {

  private static final String BASE_URI_STUDENTS = "/students/";

  private final RestTemplate restTemplate;

  @Autowired
  public StudentService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Student createStudent(Student student) {
    return restTemplate.postForObject(BASE_URI_STUDENTS, student, Student.class);
  }

  public Student getStudent(String id) {
    return restTemplate.getForObject(BASE_URI_STUDENTS + id, Student.class);
  }

  public List<Student> getStudents() {
    return restTemplate.exchange(BASE_URI_STUDENTS, HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {}).getBody();
  }

  public void updateStudent(String id, Student student) {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Student> entity = new HttpEntity<>(student, headers);
    restTemplate.exchange(BASE_URI_STUDENTS + id, HttpMethod.PUT, entity, Void.class);
  }

  public void deleteStudent(String id) {
    restTemplate.delete(BASE_URI_STUDENTS + id);
  }
}
