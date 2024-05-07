package com.sngular.pactflow.consumer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.pactflow.consumer.model.Student;
import com.sngular.pactflow.consumer.model.Teacher;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

public class Assertions {

    private Assertions() {
    }

    public static void assertTeacherDetails(Teacher expected, Teacher actual) {
        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getHireDate(), actual.getHireDate());
        assertEquals(expected.getLicenseNumber(), actual.getLicenseNumber());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getSpeciality(), actual.getSpeciality());
        assertEquals(expected.getTaughtCourses(), actual.getTaughtCourses());
    }

    public static void assertStudentDetails(Student expected, Student actual) {
        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getBirth(), actual.getBirth());
        assertEquals(expected.getCredits(), actual.getCredits());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getEnrolledCourses(), actual.getEnrolledCourses());
    }

    public static void assertHttpClientError(HttpClientErrorException exception, HttpStatus expectedStatus) {
        assertEquals(expectedStatus, exception.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(exception.getResponseBodyAsString());
            assertTrue(rootNode.has("error"));
            assertTrue(rootNode.get("error").isTextual());
            assertTrue(rootNode.has("message"));
            assertTrue(rootNode.get("message").isTextual());
        } catch (JsonProcessingException e) {
            fail("Failed to parse error response JSON", e);
        }
    }
}
