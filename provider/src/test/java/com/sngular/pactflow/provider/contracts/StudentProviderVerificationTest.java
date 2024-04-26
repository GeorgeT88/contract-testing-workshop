package com.sngular.pactflow.provider.contracts;

import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import com.github.javafaker.Faker;
import com.sngular.pactflow.provider.controller.StudentController;
import com.sngular.pactflow.provider.model.Student;
import com.sngular.pactflow.provider.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@PactBroker
@Provider("student-provider")
@ExtendWith(MockitoExtension.class)
class StudentProviderVerificationTest {

  @InjectMocks
  private StudentController studentController;

  @Mock
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
    context.setTarget(testTarget);
  }

  @State("student 1 exists")
  public void student1Exists() {
    when(studentRepository.findById("1")).thenReturn(Optional.of(createFakeStudent("1")));
  }

  @State("students exist")
  public void studentsExist() {
    when(studentRepository.findAll()).thenReturn(List.of(createFakeStudent("1"), createFakeStudent("2")));
  }

  @State("no students exist")
  public void noStudentExist() {
    when(studentRepository.findAll()).thenReturn(Collections.emptyList());
  }

  private Student createFakeStudent(String s) {
    Faker faker = new Faker();
    return Student.builder()
                  .id(s)
                  .name(faker.name().firstName())
                  .studentNumber(faker.number().randomNumber())
                  .email(faker.internet().emailAddress()).build();
  }
}
