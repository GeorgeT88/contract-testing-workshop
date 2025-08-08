package com.apenlor.pactflow.consumer.utils;

import au.com.dius.pact.consumer.dsl.LambdaDslObject;

import java.time.LocalDate;
import java.util.List;

public class DslBodyFactory {

    private DslBodyFactory() {
    }

    public static void studentSampleBody(LambdaDslObject object) {
        object.stringType("name", "Fake name");
        object.date("birth", "yyyy-MM-dd", LocalDate.parse("2000-01-01"));
        object.numberType("credits", 30);
        object.stringMatcher("email", Regex.EMAIL, "some.email@whatever.com");
        object.object("address", address -> {
            address.stringType("street", "123 Main St");
            address.stringType("city", "AnyTown");
            address.stringType("zipCode", "12345");
        });
        object.minArrayLike("enrolledCourses", 2, course -> {
            course.stringType("courseName", "Introduction to Computer Science");
            course.stringType("professor", "Dr. Tech");
            course.numberType("credits", 3);
        });
    }

    public static void teacherSampleBody(LambdaDslObject object) {
        object.stringType("name", "Jane Doe");
        object.date("hireDate", "yyyy-MM-dd", LocalDate.parse("2024-01-01"));
        object.numberType("licenseNumber", 54321);
        object.stringMatcher("email", Regex.EMAIL, "jane.doe@example.com");
        object.object("speciality", speciality -> {
            speciality.stringType("mainSubject", "History");
            speciality.stringType("secondarySubject", "Geography");
            speciality.stringType("qualification", "Masters");
        });
        object.minArrayLike("taughtCourses", 2, course -> {
            course.stringType("courseName", "World History 101");
            course.stringType("professor", "Dr. Historian");
            course.numberType("credits", 3);
        });
    }

    public static void locationSampleBody(LambdaDslObject object) {
        object.stringType("name", "Jane Doe");
        object.stringType("location", "New York");
        object.numberType("test", 1);

    }

    public static void breedsSampleBody(LambdaDslObject object) {
        object.array("message", array -> array.stringType("afghan")
                .stringType("basset")
                .stringType("blood")
                .stringType("english")
                .stringType("ibizan")
                .stringType("plott")
                .stringType("walker"));
        object.stringType("status", "success");
    }

    public static void randomDogImageBody(LambdaDslObject object) {
        object.stringType("message", "success");
        object.stringType("status", "success");
    }
}
