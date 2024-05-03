package com.sngular.pactflow.consumer.contracts;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sngular.pactflow.consumer.model.Address;
import com.sngular.pactflow.consumer.model.Course;
import com.sngular.pactflow.consumer.model.Student;
import com.sngular.pactflow.consumer.service.StudentService;
import com.sngular.pactflow.consumer.utils.Regex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
class StudentProviderTest {

    public static final String NO_STUDENTS_EXIST = "no students exist";
    public static final String STUDENT_1_EXISTS = "student with ID 1 exists";
    public static final String MULTIPLE_STUDENTS_EXISTS = "multiple students exist";

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact createStudent(PactDslWithProvider builder) {
        return builder
                .given(NO_STUDENTS_EXIST)
                .uponReceiving("create a student")
                .method("POST")
                .headers("Content-Type", "application/json")
                .path("/students/")
                .body(newJsonBody((object) -> {
                    object.stringType("name", "Fake name");
                    object.date("birth", "yyyy-MM-dd", LocalDate.parse("2000-01-01"));
                    object.numberType("credits", 30);
                    object.stringMatcher("email", Regex.EMAIL, "some.email@sngular.com");
                    object.object("address", address -> {
                        address.stringType("street", "123 Main St");
                        address.stringType("city", "AnyTown");
                        address.stringType("zipCode", "12345");
                    });
                    object.minArrayLike("enrolledCourses", 1, course -> {
                        course.stringType("courseName", "Introduction to Computer Science");
                        course.stringType("professor", "Dr. Tech");
                        course.numberType("credits", 3);
                    });
                }).build())
                .willRespondWith()
                .status(201)
                .matchHeader("Location", Regex.POST_CREATION_LOCATION_HEADER, "/students/1")
                .body(newJsonBody((object) -> {
                    object.numberType("id", 1L);
                    object.stringType("name", "Fake name");
                    object.date("birth", "yyyy-MM-dd", LocalDate.parse("2000-01-01"));
                    object.numberType("credits", 30);
                    object.stringMatcher("email", Regex.EMAIL, "some.email@sngular.com");
                    object.object("address", address -> {
                        address.stringType("street", "123 Main St");
                        address.stringType("city", "AnyTown");
                        address.stringType("zipCode", "12345");
                    });
                    object.minArrayLike("enrolledCourses", 1, course -> {
                        course.stringType("courseName", "Introduction to Computer Science");
                        course.stringType("professor", "Dr. Tech");
                        course.numberType("credits", 3);
                    });
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact getStudentWithId1(PactDslWithProvider builder) {
        return builder.given(STUDENT_1_EXISTS)
                .uponReceiving("get an existing student")
                .path("/students/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody((object) -> {
                    object.numberType("id", 1L);
                    object.stringType("name", "Fake name");
                    object.stringMatcher("email", Regex.EMAIL, "some.email@sngular.com");
                    object.date("birth", "yyyy-MM-dd", LocalDate.parse("2000-01-01"));
                    object.numberType("credits", 30);
                    object.object("address", address -> {
                        address.stringType("street", "123 Main St");
                        address.stringType("city", "AnyTown");
                        address.stringType("zipCode", "12345");
                    });
                    object.minArrayLike("enrolledCourses", 1, course -> {
                        course.stringType("courseName", "Intro to Computer Science");
                        course.stringType("professor", "Dr. Tech");
                        course.numberType("credits", 3);
                    });
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
                .body(newJsonBody((object) -> {
                    object.stringType("error", "Student not found");
                    object.stringType("message", "Student with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }


    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact getAllStudents(PactDslWithProvider builder) {
        return builder.given(MULTIPLE_STUDENTS_EXISTS)
                .uponReceiving("get all students")
                .path("/students/")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonArrayMinLike(2, array -> array.object((object) -> {
                    object.numberType("id", 2L);
                    object.stringType("name", "Another fake name");
                    object.stringMatcher("email", Regex.EMAIL, "another.email@sngular.com");
                    object.date("birth", "yyyy-MM-dd", LocalDate.parse("1999-02-02"));
                    object.numberType("credits", 25);
                    object.object("address", address -> {
                        address.stringType("street", "456 Elm St");
                        address.stringType("city", "AnyOtherTown");
                        address.stringType("zipCode", "67890");
                    });
                    object.minArrayLike("enrolledCourses", 1, course -> {
                        course.stringType("courseName", "Advanced Mathematics");
                        course.stringType("professor", "Dr. Maths");
                        course.numberType("credits", 4);
                    });
                })).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "student-provider")
    public V4Pact getAllStudentsEmptyResponse(PactDslWithProvider builder) {
        return builder.given(NO_STUDENTS_EXIST)
                .uponReceiving("get all students when no students exists")
                .path("/students/")
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
                .body(newJsonBody((object) -> {
                    object.stringType("name", "Updated Fake name");
                    object.date("birth", "yyyy-MM-dd", LocalDate.parse("2000-01-01"));
                    object.numberType("credits", 35);
                    object.stringMatcher("email", Regex.EMAIL, "updated.email@sngular.com");
                    object.object("address", address -> {
                        address.stringType("street", "123 Main St");
                        address.stringType("city", "AnyTown");
                        address.stringType("zipCode", "12345");
                    });
                    object.minArrayLike("enrolledCourses", 1, course -> {
                        course.stringType("courseName", "Advanced Computer Science");
                        course.stringType("professor", "Dr. Advanced Tech");
                        course.numberType("credits", 4);
                    });
                }).build())
                .willRespondWith()
                .status(200)
                .body(newJsonBody((object) -> {
                    object.numberType("id", 1L);
                    object.stringType("name", "Updated Fake name");
                    object.date("birth", "yyyy-MM-dd", LocalDate.parse("2000-01-01"));
                    object.numberType("credits", 35);
                    object.stringMatcher("email", Regex.EMAIL, "updated.email@sngular.com");
                    object.object("address", address -> {
                        address.stringType("street", "123 Main St");
                        address.stringType("city", "AnyTown");
                        address.stringType("zipCode", "12345");
                    });
                    object.minArrayLike("enrolledCourses", 1, course -> {
                        course.stringType("courseName", "Advanced Computer Science");
                        course.stringType("professor", "Dr. Advanced Tech");
                        course.numberType("credits", 4);
                    });
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
                .body(newJsonBody((object) -> {
                    object.stringType("name", "Non-existent Student");
                    object.date("birth", "yyyy-MM-dd", LocalDate.parse("2000-01-01"));
                    object.numberType("credits", 30);
                    object.stringMatcher("email", Regex.EMAIL, "nonexistent.email@sngular.com");
                    object.object("address", address -> {
                        address.stringType("street", "404 Unknown St");
                        address.stringType("city", "Nowhere");
                        address.stringType("zipCode", "00000");
                    });
                    object.array("enrolledCourses");
                }).build())
                .willRespondWith()
                .status(404)
                .body(newJsonBody((object) -> {
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
                .body(newJsonBody((object) -> {
                    object.stringType("error", "Student not found");
                    object.stringType("message", "Student with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }


    @Test
    @PactTestFor(pactMethod = "createStudent")
    void createStudentTest(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);

        Address address = Address.builder()
                .street("123 Main St")
                .city("AnyTown")
                .zipCode("12345")
                .build();

        Course course = Course.builder()
                .courseName("Introduction to Computer Science")
                .professor("Dr. Tech")
                .credits(3)
                .build();

        List<Course> courses = List.of(course);

        Student student = Student.builder()
                .name("Fake name")
                .birth(LocalDate.of(2000, 1, 1))
                .credits(30)
                .email("some.email@sngular.com")
                .address(address)
                .enrolledCourses(courses)
                .build();

        Student createdStudent = studentService.createStudent(student);

        assertNotNull(createdStudent);
        assertNotNull(createdStudent.getId());
        assertEquals("Fake name", createdStudent.getName());
        assertEquals(LocalDate.of(2000, 1, 1), createdStudent.getBirth());
        assertEquals(30, createdStudent.getCredits());
        assertEquals("some.email@sngular.com", createdStudent.getEmail());
        assertEquals(address, createdStudent.getAddress());
        assertEquals(courses, createdStudent.getEnrolledCourses());
    }

    @Test
    @PactTestFor(pactMethod = "getStudentWithId1")
    void getStudentWhenStudentExist(MockServer mockServer) {
        Address address = new Address("123 Main St", "AnyTown", "12345");
        List<Course> courses = List.of(new Course("Intro to Computer Science", "Dr. Tech", 3));
        Student expected = Student.builder()
                .id(1L)
                .name("Fake name")
                .email("some.email@sngular.com")
                .birth(LocalDate.parse("2000-01-01"))
                .credits(30)
                .address(address)
                .enrolledCourses(courses)
                .build();

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);
        Student student = studentService.getStudent(1L);

        assertEquals(expected, student);
    }

    @Test
    @PactTestFor(pactMethod = "getNonExistingStudent")
    void getNonExistingStudent(MockServer mockServer) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> studentService.getStudent(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(exception.getResponseBodyAsString());
        assertTrue(rootNode.has("error"));
        assertTrue(rootNode.get("error").isTextual());
        assertTrue(rootNode.has("message"));
        assertTrue(rootNode.get("message").isTextual());
    }


    @Test
    @PactTestFor(pactMethod = "getAllStudents")
    void getStudentsWhenStudentsExist(MockServer mockServer) {
        Address address = new Address("456 Elm St", "AnyOtherTown", "67890");
        List<Course> courses = List.of(new Course("Advanced Mathematics", "Dr. Maths", 4));
        Student student = Student.builder()
                .id(2L)
                .name("Another fake name")
                .email("another.email@sngular.com")
                .birth(LocalDate.parse("1999-02-02"))
                .credits(25)
                .address(address)
                .enrolledCourses(courses)
                .build();
        List<Student> expected = List.of(student, student);

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);
        List<Student> students = studentService.getStudents();

        assertEquals(expected, students);
    }


    @Test
    @PactTestFor(pactMethod = "getAllStudentsEmptyResponse")
    void getStudentsWhenNoStudentsExistTest(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);
        List<Student> students = studentService.getStudents();

        assertEquals(Collections.emptyList(), students);
    }

    @Test
    @PactTestFor(pactMethod = "updateStudent")
    void updateStudentTest(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);

        Address address = Address.builder()
                .street("123 Main St")
                .city("AnyTown")
                .zipCode("12345")
                .build();

        Course course = Course.builder()
                .courseName("Advanced Computer Science")
                .professor("Dr. Advanced Tech")
                .credits(4)
                .build();

        List<Course> courses = List.of(course);

        Student student = Student.builder()
                .name("Updated Fake name")
                .birth(LocalDate.of(2000, 1, 1))
                .credits(35)
                .email("updated.email@sngular.com")
                .address(address)
                .enrolledCourses(courses)
                .build();

        Student updatedStudent = studentService.updateStudent(1L, student);

        assertNotNull(updatedStudent);
        assertNotNull(updatedStudent.getId());
        assertEquals("Updated Fake name", updatedStudent.getName());
        assertEquals(LocalDate.of(2000, 1, 1), updatedStudent.getBirth());
        assertEquals(35, updatedStudent.getCredits());
        assertEquals("updated.email@sngular.com", updatedStudent.getEmail());
        assertEquals(address, updatedStudent.getAddress());
        assertEquals(courses, updatedStudent.getEnrolledCourses());
    }

    @Test
    @PactTestFor(pactMethod = "updateNonExistentStudent")
    void updateNonExistentStudentTest(MockServer mockServer) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);

        Address address = Address.builder()
                .street("404 Unknown St")
                .city("Nowhere")
                .zipCode("00000")
                .build();

        Student nonExistentStudent = Student.builder()
                .name("Non-existent Student")
                .birth(LocalDate.of(2000, 1, 1))
                .credits(30)
                .email("nonexistent.email@sngular.com")
                .address(address)
                .enrolledCourses(Collections.emptyList())
                .build();

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> studentService.updateStudent(1L, nonExistentStudent));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(exception.getResponseBodyAsString());
        assertTrue(rootNode.has("error"));
        assertTrue(rootNode.get("error").isTextual());
        assertTrue(rootNode.has("message"));
        assertTrue(rootNode.get("message").isTextual());
    }

    @Test
    @PactTestFor(pactMethod = "deleteStudent")
    void deleteStudentTest(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);

        assertDoesNotThrow(() -> studentService.deleteStudent(1L));
    }

    @Test
    @PactTestFor(pactMethod = "deleteNonExistentStudent")
    void testDeleteNonExistentStudent(MockServer mockServer) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        StudentService studentService = new StudentService(restTemplate);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> studentService.deleteStudent(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(exception.getResponseBodyAsString());
        assertTrue(rootNode.has("error"));
        assertTrue(rootNode.get("error").isTextual());
        assertTrue(rootNode.has("message"));
        assertTrue(rootNode.get("message").isTextual());
    }

}
