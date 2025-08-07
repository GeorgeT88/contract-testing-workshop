package com.apenlor.pactflow.consumer.service;

import com.apenlor.pactflow.consumer.model.Breeds;
import com.apenlor.pactflow.consumer.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DogService {

    private static final String BASE_URI_LOCATIONS = "/locations";
    private static final String URI_LOCATION_BY_ID = BASE_URI_LOCATIONS + "/{id}";

    private final RestTemplate restTemplate;

    @Autowired
    public DogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Breeds getBreeds() {
        return restTemplate.getForObject(URI_LOCATION_BY_ID, Breeds.class);
    }

}