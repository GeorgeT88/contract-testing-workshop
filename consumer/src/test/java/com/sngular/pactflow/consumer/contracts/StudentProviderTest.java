package com.sngular.pactflow.consumer.contracts;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.sngular.pactflow.consumer.model.Student;
import com.sngular.pactflow.consumer.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
class StudentProviderTest {

  @Pact(consumer = "student-consumer", provider = "student-provider")
  public V4Pact getStudentWithId1(PactDslWithProvider builder) {
    return builder.given("student 1 exists")
                  .uponReceiving("get a existing student with ID 1")
                  .path("/students/1")
                  .method("GET")
                  .willRespondWith()
                  .status(200)
                  .headers(Map.of("Content-Type", "application/json"))
                  .body(newJsonBody(object -> {
                    object.stringType("id", "1");
                    object.stringType("name", "Fake name");
                    object.stringType("email", "some.email@sngular.com");
                    object.numberType("studentNumber", 23);
                  }).build())
                  .toPact().asV4Pact().get();
  }

  @Pact(consumer = "student-consumer", provider = "student-provider")
  public V4Pact getAllStudents(PactDslWithProvider builder) {
    return builder.given("students exist")
                  .uponReceiving("get all students")
                  .path("/students/")
                  .method("GET")
                  .willRespondWith()
                  .status(200)
                  .headers(Map.of("Content-Type", "application/json"))
                  .body(newJsonArrayMinLike(2, array ->
                                                   array.object(object -> {
                                                     object.stringType("id", "2");
                                                     object.stringType("name", "Another fake name");
                                                     object.stringType("email", "another.email@sngular.com");
                                                     object.numberType("studentNumber", 24);
                                                   })
                  ).build())
                  .toPact().asV4Pact().get();
  }

  @Pact(consumer = "student-consumer", provider = "student-provider")
  public V4Pact getAllStudentsEmptyResponse(PactDslWithProvider builder) {
    return builder.given("no students exist")
                  .uponReceiving("get all students when no student exists")
                  .path("/students/")
                  .method("GET")
                  .willRespondWith()
                  .status(200)
                  .headers(Map.of("Content-Type", "application/json"))
                  .body("[]")
                  .toPact().asV4Pact().get();
  }

  @Test
  @PactTestFor(pactMethod = "getStudentWithId1")
  void getStudentWhenStudentExist(MockServer mockServer) {
    Student expected = Student.builder()
                              .id("1")
                              .name("Fake name")
                              .email("some.email@sngular.com")
                              .studentNumber(23).build();

    RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
    Student student = new StudentService(restTemplate).getStudent("1");

    assertEquals(expected, student);
  }

  @Test
  @PactTestFor(pactMethod = "getAllStudents")
  void getStudentsWhenStudentsExist(MockServer mockServer) {
    Student student = Student.builder()
                             .id("2")
                             .name("Another fake name")
                             .email("another.email@sngular.com")
                             .studentNumber(24).build();

    List<Student> expected = List.of(student, student);

    RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
    List<Student> students = new StudentService(restTemplate).getStudents();

    assertEquals(expected, students);
  }

  @Test
  @PactTestFor(pactMethod = "getAllStudentsEmptyResponse")
  void getStudentsWhenNoStudentsExistTest(MockServer mockServer) {
    RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
    List<Student> students = new StudentService(restTemplate).getStudents();

    assertEquals(Collections.emptyList(), students);
  }
}
