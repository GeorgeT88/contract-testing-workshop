package com.sngular.pactflow.teacher.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Teacher {

    @Id
    private String id;
    private String name;
    private long teacherNumber;
    private String email;

}
