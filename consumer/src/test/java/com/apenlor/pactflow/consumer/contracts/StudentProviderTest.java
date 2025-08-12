package com.apenlor.pactflow.consumer.contracts;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.apenlor.pactflow.consumer.model.Student;
import com.apenlor.pactflow.consumer.service.StudentService;
import com.apenlor.pactflow.consumer.utils.DslBodyFactory;
import com.apenlor.pactflow.consumer.utils.Regex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static com.apenlor.pactflow.consumer.utils.Assertions.assertHttpClientError;
import static com.apenlor.pactflow.consumer.utils.Assertions.assertStudentDetails;
import static com.apenlor.pactflow.consumer.utils.DslBodyFactory.studentSampleBody;
import static com.apenlor.pactflow.consumer.utils.FixtureFactory.getStudentSample;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
class StudentProviderTest {

    public static final String NO_STUDENTS_EXIST = "no students exist";
    public static final String STUDENT_1_EXISTS = "student with ID 1 exists";
    public static final String STUDENT_3_EXISTS = "student with ID 1 exists";
    public static final String MULTIPLE_STUDENTS_EXISTS = "multiple students exist";

    private StudentService studentService;

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact createStudent(PactDslWithProvider builder) {
        return builder
                .given(NO_STUDENTS_EXIST)
                .uponReceiving("create a student")
                .method("POST")
                .headers("Content-Type", "application/json")
                .path("/students")
                .body(newJsonBody(DslBodyFactory::studentSampleBody).build())
                .willRespondWith()
                .status(201)
                .matchHeader("Location", Regex.POST_CREATION_LOCATION_HEADER, "/students/1")
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    studentSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact getStudentWithId1(PactDslWithProvider builder) {
        return builder.given(STUDENT_1_EXISTS)
                .uponReceiving("get an existing student with ID 1 - updated")
                .path("/students/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    studentSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact getStudentWithId3(PactDslWithProvider builder) {
        return builder.given(STUDENT_3_EXISTS)
                .uponReceiving("get an existing student with ID 3 - updated")
                .path("/students/3")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(object -> {
                    object.numberType("id", 3L);
                    studentSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact getNonExistingStudent(PactDslWithProvider builder) {
        return builder.given(NO_STUDENTS_EXIST)
                .uponReceiving("get a non-existing student")
                .path("/students/1")
                .method("GET")
                .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(object -> {
                    object.stringType("error", "Student not found");
                    object.stringType("message", "Student with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact getAllStudents(PactDslWithProvider builder) {
        return builder.given(MULTIPLE_STUDENTS_EXISTS)
                .uponReceiving("get all students")
                .path("/students")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonArrayMinLike(2, array -> array.object(object -> {
                    object.numberType("id", 1L);
                    studentSampleBody(object);
                })).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact getAllStudentsEmptyResponse(PactDslWithProvider builder) {
        return builder.given(NO_STUDENTS_EXIST)
                .uponReceiving("get all students when no students exists")
                .path("/students")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("[]")
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact updateStudent(PactDslWithProvider builder) {
        return builder
                .given(STUDENT_1_EXISTS)
                .uponReceiving("update an existing student")
                .method("PUT")
                .headers("Content-Type", "application/json")
                .path("/students/1")
                .body(newJsonBody(DslBodyFactory::studentSampleBody).build())
                .willRespondWith()
                .status(200)
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    studentSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact updateNonExistentStudent(PactDslWithProvider builder) {
        return builder
                .given(NO_STUDENTS_EXIST)
                .uponReceiving("update a non-existent student")
                .method("PUT")
                .headers("Content-Type", "application/json")
                .path("/students/1")
                .body(newJsonBody(DslBodyFactory::studentSampleBody).build())
                .willRespondWith()
                .status(404)
                .body(newJsonBody(object -> {
                    object.stringType("error", "Student not found");
                    object.stringType("message", "Student with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact deleteStudent(PactDslWithProvider builder) {
        return builder
                .given(STUDENT_1_EXISTS)
                .uponReceiving("delete an existing student")
                .method("DELETE")
                .path("/students/1")
                .willRespondWith()
                .status(204)
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact deleteNonExistentStudent(PactDslWithProvider builder) {
        return builder
                .given(NO_STUDENTS_EXIST)
                .uponReceiving("a request to delete a non-existent student")
                .method("DELETE")
                .path("/students/1")
                .willRespondWith()
                .status(404)
                .body(newJsonBody(object -> {
                    object.stringType("error", "Student not found");
                    object.stringType("message", "Student with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @BeforeEach
    void setup(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        studentService = new StudentService(restTemplate);
    }

    @Test
    @PactTestFor(pactMethod = "createStudent")
    void createStudentTest() {
        Student student = getStudentSample();

        Student createdStudent = studentService.createStudent(student);

        assertStudentDetails(student, createdStudent);
    }

    @Test
    @PactTestFor(pactMethod = "getStudentWithId1")
    void getStudentWhenStudentExist() {
        Student expected = getStudentSample();

        Student student = studentService.getStudent(1L);

        assertStudentDetails(expected, student);
    }

    @Test
    @PactTestFor(pactMethod = "getNonExistingStudent")
    void getNonExistingStudent() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> studentService.getStudent(1L));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    @PactTestFor(pactMethod = "getAllStudents")
    void getStudentsWhenStudentsExist() {
        Student expected = getStudentSample();

        List<Student> students = studentService.getStudents();

        assertFalse(students.isEmpty());
        assertEquals(2, students.size());
        students.forEach(student -> assertStudentDetails(expected, student));
    }

    @Test
    @PactTestFor(pactMethod = "getAllStudentsEmptyResponse")
    void getStudentsWhenNoStudentsExistTest() {
        List<Student> students = studentService.getStudents();

        assertTrue(students.isEmpty());
    }

    @Test
    @PactTestFor(pactMethod = "updateStudent")
    void updateStudentTest() {
        Student student = getStudentSample();

        Student updatedStudent = studentService.updateStudent(1L, student);

        assertStudentDetails(student, updatedStudent);
    }

    @Test
    @PactTestFor(pactMethod = "updateNonExistentStudent")
    void updateNonExistentStudentTest() {
        Student nonExistentStudent = getStudentSample();

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> studentService.updateStudent(1L, nonExistentStudent));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    @PactTestFor(pactMethod = "deleteStudent")
    void deleteStudentTest() {
        assertDoesNotThrow(() -> studentService.deleteStudent(1L));
    }

    @Test
    @PactTestFor(pactMethod = "deleteNonExistentStudent")
    void testDeleteNonExistentStudent() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> studentService.deleteStudent(1L));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }
}