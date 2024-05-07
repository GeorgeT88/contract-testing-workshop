package com.sngular.pactflow.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Speciality {

    private String mainSubject;

    private String secondarySubject;

    private String qualification;

}
