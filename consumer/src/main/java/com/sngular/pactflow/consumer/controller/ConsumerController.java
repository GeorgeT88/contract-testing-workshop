package com.sngular.pactflow.consumer.controller;

import com.sngular.pactflow.consumer.model.Student;
import com.sngular.pactflow.consumer.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/consumer")
@Slf4j
public class ConsumerController {

  private final StudentService studentService;

  public ConsumerController(StudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping(value = "/test")
  public Student testIntegration() {
    try {
      return studentService.getStudent("1");
    } catch (HttpClientErrorException ex) {
      log.error("It's broken --> {}", ex.getMessage());
      throw ex;
    }
  }
}
