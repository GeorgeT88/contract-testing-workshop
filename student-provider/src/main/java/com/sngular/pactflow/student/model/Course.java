package com.sngular.pactflow.student.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @Column(name = "course_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String courseName;

    private String professor;

    private Integer credits;

    @ManyToMany(mappedBy = "enrolledCourses")
    private List<Student> students;

}