package com.apenlor.pactflow.consumer.service;

import com.apenlor.pactflow.consumer.model.Breeds;
import com.apenlor.pactflow.consumer.model.RandomDogImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DogService {

    private final RestTemplate restTemplate;

    @Autowired
    public DogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Breeds getBreeds() {
        return restTemplate.getForObject("/dogs", Breeds.class);
    }

    public RandomDogImage getRandomDogImage() {
        return restTemplate.getForObject("/dogs/random", RandomDogImage.class);
    }

}