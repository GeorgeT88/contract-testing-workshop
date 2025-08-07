package com.apenlor.pactflow.dog.dto;

import com.apenlor.pactflow.dog.entities.Breeds;

import java.util.List;

public record BreedsResponse(List<String> message, String status) {

    public static BreedsResponse fromEntity(Breeds breeds) {
        return new BreedsResponse(breeds.getMessage(), breeds.getStatus());
    }
};



