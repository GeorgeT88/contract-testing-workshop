package com.sngular.pactflow.consumer.contracts;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.sngular.pactflow.consumer.model.Teacher;
import com.sngular.pactflow.consumer.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
class TeacherProviderTest {

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact getTeacherWithId1(PactDslWithProvider builder) {
        return builder.given("teacher 1 exists")
                .uponReceiving("get an existing student with ID 1")
                .path("/teachers/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(object -> {
                    object.stringType("id", "1");
                    object.stringType("name", "Fake name");
                    object.stringType("email", "some.email@sngular.com");
                    object.numberType("teacherNumber", 23);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact getAllTeachers(PactDslWithProvider builder) {
        return builder.given("teachers exist")
                .uponReceiving("get all teachers")
                .path("/teachers/")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonArrayMinLike(2, array ->
                        array.object(object -> {
                            object.stringType("id", "2");
                            object.stringType("name", "Another fake name");
                            object.stringType("email", "another.email@sngular.com");
                            object.numberType("teacherNumber", 24);
                        })
                ).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "teacher-provider")
    public V4Pact getAllTeachersEmptyResponse(PactDslWithProvider builder) {
        return builder.given("no teachers exist")
                .uponReceiving("get all teachers when no teacher exists")
                .path("/teachers/")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("[]")
                .toPact().asV4Pact().get();
    }

    @Test
    @PactTestFor(pactMethod = "getTeacherWithId1")
    void getTeacherWhenTeacherExist(MockServer mockServer) {
        Teacher expected = Teacher.builder()
                .id("1")
                .name("Fake name")
                .email("some.email@sngular.com")
                .teacherNumber(23).build();

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        Teacher teacher = new TeacherService(restTemplate).getTeacher("1");

        assertEquals(expected, teacher);
    }

    @Test
    @PactTestFor(pactMethod = "getAllTeachers")
    void getTeachersWhenTeachersExist(MockServer mockServer) {
        Teacher teacher = Teacher.builder()
                .id("2")
                .name("Another fake name")
                .email("another.email@sngular.com")
                .teacherNumber(24).build();

        List<Teacher> expected = List.of(teacher, teacher);

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        List<Teacher> teachers = new TeacherService(restTemplate).getTeachers();

        assertEquals(expected, teachers);
    }

    @Test
    @PactTestFor(pactMethod = "getAllTeachersEmptyResponse")
    void getTeachersWhenNoTeachersExistTest(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        List<Teacher> teachers = new TeacherService(restTemplate).getTeachers();

        assertEquals(Collections.emptyList(), teachers);
    }
}
