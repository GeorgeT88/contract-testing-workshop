package com.apenlor.pactflow.consumer.contracts;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.apenlor.pactflow.consumer.model.Breeds;
import com.apenlor.pactflow.consumer.model.RandomDogImage;
import com.apenlor.pactflow.consumer.service.DogService;
import com.apenlor.pactflow.consumer.utils.DslBodyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static com.apenlor.pactflow.consumer.utils.FixtureFactory.getBreedsSample;
import static com.apenlor.pactflow.consumer.utils.FixtureFactory.getRandomDogImageSample;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
class DogProviderTest {

    private DogService dogService;

    @Pact(consumer = "consumer", provider = "dog-provider")
    public V4Pact getHoundSubBreeds(PactDslWithProvider builder) {
        return builder.given("multiple dog sub-breeds exist")
                .uponReceiving("get all dog sub-breeds")
                .path("/dogs")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(DslBodyFactory::breedsSampleBody).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "dog-provider")
    public V4Pact getRandomDogImage(PactDslWithProvider builder) {
        return builder.given("random dog images exist")
                .uponReceiving("get random dog image")
                .path("/dogs/random")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .stringType("message", "success")
                        .stringType("status", "success"))
                .toPact().asV4Pact().get();
    }

    @BeforeEach
    void setup(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        dogService = new DogService(restTemplate);
    }

    @Test
    @PactTestFor(pactMethod = "getHoundSubBreeds")
    void getHoundSubBreeds() {
        Breeds expected = getBreedsSample();

        Breeds breeds = dogService.getBreeds();

        assertFalse(breeds.toString().isEmpty());
        assertEquals(7, breeds.getMessage().size());
        assertEquals(expected.getMessage().get(0), breeds.getMessage().get(0));
        assertEquals(expected.getStatus(), breeds.getStatus());
    }

    @Test
    @PactTestFor(pactMethod = "getRandomDogImage")
    void getRandomDogImage() {
        RandomDogImage expected = getRandomDogImageSample();

        RandomDogImage breeds = dogService.getRandomDogImage();

        assertFalse(breeds.toString().isEmpty());
        assertEquals(expected.getMessage(), breeds.getMessage());
        assertEquals(expected.getStatus(), breeds.getStatus());
    }
}