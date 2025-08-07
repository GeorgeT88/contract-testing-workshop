package com.apenlor.pactflow.dog.dto;

import com.apenlor.pactflow.dog.entities.Breeds;

import java.util.List;

public record BreedsResponse(List<String> message, String status) {
};



