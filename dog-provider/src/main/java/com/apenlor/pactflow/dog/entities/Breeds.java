package com.apenlor.pactflow.dog.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Breeds {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private List<String> message;

    private String status;
}
