package com.apenlor.pactflow.consumer.utils;

import com.apenlor.pactflow.consumer.model.*;

import java.time.LocalDate;
import java.util.List;

public class FixtureFactory {

    private FixtureFactory() {
    }

    public static Student getStudentSample() {
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

        List<Course> courses = List.of(course, course);

        return Student.builder()
                .name("Fake name")
                .birth(LocalDate.of(2000, 1, 1))
                .credits(30)
                .email("some.email@whatever.com")
                .address(address)
                .enrolledCourses(courses)
                .build();
    }

    public static Teacher getTeacherSample() {
        Speciality speciality = Speciality.builder()
                .mainSubject("History")
                .secondarySubject("Geography")
                .qualification("Masters")
                .build();

        Course course = Course.builder()
                .courseName("World History 101")
                .professor("Dr. Historian")
                .credits(3)
                .build();

        List<Course> taughtCourses = List.of(course, course);

        return Teacher.builder()
                .name("Jane Doe")
                .hireDate(LocalDate.of(2024, 1, 1))
                .licenseNumber(54321)
                .email("jane.doe@example.com")
                .speciality(speciality)
                .taughtCourses(taughtCourses)
                .build();
    }

    public static Location getLocationSample() {

        return Location.builder()
                .name("Jane Doe")
                .location("New York")
                .test(1)
                .build();
    }
}