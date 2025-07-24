package com.apenlor.pactflow.consumer.contracts;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.apenlor.pactflow.consumer.model.Teacher;
import com.apenlor.pactflow.consumer.service.TeacherService;
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
import static com.apenlor.pactflow.consumer.utils.Assertions.assertTeacherDetails;
import static com.apenlor.pactflow.consumer.utils.DslBodyFactory.teacherSampleBody;
import static com.apenlor.pactflow.consumer.utils.FixtureFactory.getTeacherSample;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
class TeacherProviderTest {

    public static final String NO_TEACHERS_EXIST = "no teachers exist";
    public static final String TEACHER_1_EXISTS = "teacher with ID 1 exists";
    public static final String MULTIPLE_TEACHERS_EXISTS = "multiple teachers exist";

    private TeacherService teacherService;

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact createTeacher(PactDslWithProvider builder) {
        return builder
                .given(NO_TEACHERS_EXIST)
                .uponReceiving("create a teacher")
                .method("POST")
                .headers("Content-Type", "application/json")
                .path("/teachers")
                .body(newJsonBody(DslBodyFactory::teacherSampleBody).build())
                .willRespondWith()
                .status(201)
                .matchHeader("Location", Regex.POST_CREATION_LOCATION_HEADER, "/teachers/1")
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    teacherSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact getTeacherWithId1(PactDslWithProvider builder) {
        return builder.given(TEACHER_1_EXISTS)
                .uponReceiving("get an existing teacher")
                .path("/teachers/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    teacherSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact getNonExistingTeacher(PactDslWithProvider builder) {
        return builder.given(NO_TEACHERS_EXIST)
                .uponReceiving("get a non-existing teacher")
                .path("/teachers/1")
                .method("GET")
                .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(object -> {
                    object.stringType("error", "Teacher not found");
                    object.stringType("message", "Teacher with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact getAllTeachers(PactDslWithProvider builder) {
        return builder.given(MULTIPLE_TEACHERS_EXISTS)
                .uponReceiving("get all teachers")
                .path("/teachers")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonArrayMinLike(2, array -> array.object(object -> {
                    object.numberType("id", 1L);
                    teacherSampleBody(object);
                })).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact getAllTeachersEmptyResponse(PactDslWithProvider builder) {
        return builder.given("NO_TEACHERS_EXIST")
                .uponReceiving("get all teachers when no teachers exist")
                .path("/teachers")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("[]")
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact updateTeacher(PactDslWithProvider builder) {
        return builder
                .given(TEACHER_1_EXISTS)
                .uponReceiving("update an existing teacher")
                .method("PUT")
                .headers("Content-Type", "application/json")
                .path("/teachers/1")
                .body(newJsonBody(DslBodyFactory::teacherSampleBody).build())
                .willRespondWith()
                .status(200)
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    teacherSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact updateNonExistentTeacher(PactDslWithProvider builder) {
        return builder
                .given(NO_TEACHERS_EXIST)
                .uponReceiving("update a non-existent teacher")
                .method("PUT")
                .headers("Content-Type", "application/json")
                .path("/teachers/1")
                .body(newJsonBody(DslBodyFactory::teacherSampleBody).build())
                .willRespondWith()
                .status(404)
                .body(newJsonBody(object -> {
                    object.stringType("error", "Teacher not found");
                    object.stringType("message", "Teacher with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact deleteTeacher(PactDslWithProvider builder) {
        return builder
                .given(TEACHER_1_EXISTS)
                .uponReceiving("delete an existing teacher")
                .method("DELETE")
                .path("/teachers/1")
                .willRespondWith()
                .status(204)
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact deleteNonExistentTeacher(PactDslWithProvider builder) {
        return builder
                .given(NO_TEACHERS_EXIST)
                .uponReceiving("a request to delete a non-existent teacher")
                .method("DELETE")
                .path("/teachers/1")
                .willRespondWith()
                .status(404)
                .body(newJsonBody(object -> {
                    object.stringType("error", "Teacher not found");
                    object.stringType("message", "Teacher with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @BeforeEach
    void setup(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        teacherService = new TeacherService(restTemplate);
    }

    @Test
    @PactTestFor(pactMethod = "createTeacher")
    void createTeacherTest() {
        Teacher teacher = getTeacherSample();

        Teacher createdTeacher = teacherService.createTeacher(teacher);

        assertTeacherDetails(teacher, createdTeacher);
    }

    @Test
    @PactTestFor(pactMethod = "getTeacherWithId1")
    void getTeacherWhenTeacherExists() {
        Teacher expectedTeacher = getTeacherSample();

        Teacher teacher = teacherService.getTeacher(1L);

        assertTeacherDetails(expectedTeacher, teacher);
    }

    @Test
    @PactTestFor(pactMethod = "getNonExistingTeacher")
    void getNonExistingTeacher() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> teacherService.getTeacher(1L));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    @PactTestFor(pactMethod = "getAllTeachers")
    void getAllTeachersWhenTeachersExist() {
        Teacher expected = getTeacherSample();

        List<Teacher> teachers = teacherService.getTeachers();

        assertFalse(teachers.isEmpty());
        assertEquals(2, teachers.size());
        teachers.forEach(teacher -> assertTeacherDetails(expected, teacher));
    }

    @Test
    @PactTestFor(pactMethod = "getAllTeachersEmptyResponse")
    void getAllTeachersWhenNoTeachersExist() {
        List<Teacher> teachers = teacherService.getTeachers();

        assertTrue(teachers.isEmpty());
    }

    @Test
    @PactTestFor(pactMethod = "updateTeacher")
    void updateTeacherTest() {
        Teacher teacher = getTeacherSample();

        Teacher updatedTeacher = teacherService.updateTeacher(1L, teacher);

        assertTeacherDetails(teacher, updatedTeacher);
    }

    @Test
    @PactTestFor(pactMethod = "updateNonExistentTeacher")
    void updateNonExistentTeacherTest() {
        Teacher nonExistentTeacher = getTeacherSample();

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> teacherService.updateTeacher(1L, nonExistentTeacher));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    @PactTestFor(pactMethod = "deleteTeacher")
    void deleteTeacherTest() {
        assertDoesNotThrow(() -> teacherService.deleteTeacher(1L));
    }

    @Test
    @PactTestFor(pactMethod = "deleteNonExistentTeacher")
    void testDeleteNonExistentTeacher() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> teacherService.deleteTeacher(1L));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }
}