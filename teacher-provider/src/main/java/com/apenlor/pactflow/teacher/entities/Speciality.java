package com.apenlor.pactflow.teacher.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Speciality {

    private String mainSubject;

    private String secondarySubject;

    private String qualification;

}
