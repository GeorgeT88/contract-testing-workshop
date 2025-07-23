package com.apenlor.pactflow.student.contracts;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.github.javafaker.Faker;
import com.apenlor.pactflow.student.controller.StudentController;
import com.apenlor.pactflow.student.exceptions.CustomExceptionHandler;
import com.apenlor.pactflow.student.model.Address;
import com.apenlor.pactflow.student.model.Course;
import com.apenlor.pactflow.student.model.Student;
import com.apenlor.pactflow.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@PactBroker()
@Provider("student-provider")
@SpringBootTest()
class StudentProviderVerificationTest {

    public static final String NO_STUDENTS_EXIST = "no students exist";
    public static final String STUDENT_1_EXISTS = "student with ID 1 exists";
    public static final String MULTIPLE_STUDENTS_EXISTS = "multiple students exist";

    @Autowired
    private StudentController studentController;

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    @SpyBean
    private StudentRepository studentRepository;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void setUp(PactVerificationContext context) {
        MockMvcTestTarget testTarget = new MockMvcTestTarget();
        testTarget.setControllers(studentController);
        testTarget.setControllerAdvices(customExceptionHandler);
        context.setTarget(testTarget);
    }

    @State(STUDENT_1_EXISTS)
    public void student1Exists() {
        Student one = createFakeStudent(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(one));
        when(studentRepository.findAll()).thenReturn(List.of(one));
    }

    @State(MULTIPLE_STUDENTS_EXISTS)
    public void studentsExist() {
        Student one = createFakeStudent(1L);
        Student two = createFakeStudent(2L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(one));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(two));
        when(studentRepository.findAll()).thenReturn(List.of(one, two));
    }

    @State(NO_STUDENTS_EXIST)
    public void noStudentExist() {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());
    }

    private Student createFakeStudent(Long id) {
        Faker faker = new Faker();

        Address address = Address.builder()
                .street(faker.address().streetAddress())
                .city(faker.address().city())
                .zipCode(faker.address().zipCode())
                .build();

        Course course = Course.builder()
                .courseName("Intro to " + faker.educator().course())
                .professor("Dr. " + faker.name().fullName())
                .credits(faker.number().randomDigit())
                .build();

        return Student.builder()
                .id(id)
                .name(faker.name().fullName())
                .birth(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .credits(faker.number().randomDigit())
                .email(faker.internet().emailAddress())
                .address(address)
                .enrolledCourses(Arrays.asList(course, course))
                .build();
    }
}
