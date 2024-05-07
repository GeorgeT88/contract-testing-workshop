package com.sngular.pactflow.consumer.service;

import com.sngular.pactflow.consumer.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TeacherService {

    private static final String BASE_URI_TEACHERS = "/teachers/";

    private final RestTemplate restTemplate;

    @Autowired
    public TeacherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Teacher createTeacher(Teacher teacher) {
        return restTemplate.postForObject(BASE_URI_TEACHERS, teacher, Teacher.class);
    }

    public Teacher getTeacher(Long id) {
        return restTemplate.getForObject(BASE_URI_TEACHERS + id, Teacher.class);
    }

    public List<Teacher> getTeachers() {
        return restTemplate.exchange(BASE_URI_TEACHERS, HttpMethod.GET, null, new ParameterizedTypeReference<List<Teacher>>() {
        }).getBody();
    }

    public Teacher updateTeacher(Long id, Teacher teacher) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Teacher> entity = new HttpEntity<>(teacher, headers);
        return restTemplate.exchange(BASE_URI_TEACHERS + id, HttpMethod.PUT, entity, Teacher.class).getBody();
    }

    public void deleteTeacher(Long id) {
        restTemplate.delete(BASE_URI_TEACHERS + id);
    }
}
