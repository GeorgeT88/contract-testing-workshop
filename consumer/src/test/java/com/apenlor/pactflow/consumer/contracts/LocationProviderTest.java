package com.apenlor.pactflow.consumer.contracts;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.apenlor.pactflow.consumer.model.Location;
import com.apenlor.pactflow.consumer.service.LocationService;
import com.apenlor.pactflow.consumer.utils.DslBodyFactory;
import com.apenlor.pactflow.consumer.utils.Regex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static com.apenlor.pactflow.consumer.utils.Assertions.assertHttpClientError;
import static com.apenlor.pactflow.consumer.utils.Assertions.assertLocationDetails;
import static com.apenlor.pactflow.consumer.utils.DslBodyFactory.locationSampleBody;
import static com.apenlor.pactflow.consumer.utils.FixtureFactory.getLocationSample;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
class LocationProviderTest {

    public static final String NO_LOCATIONS_EXIST = "no locations exist";
    public static final String LOCATION_1_EXISTS = "location with ID 1 exists";
    public static final String MULTIPLE_LOCATIONS_EXISTS = "multiple locations exist";

    private LocationService locationService;

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact createLocation(PactDslWithProvider builder) {
        return builder
                .given(NO_LOCATIONS_EXIST)
                .uponReceiving("create a location")
                .method("POST")
                .headers("Content-Type", "application/json")
                .path("/locations")
                .body(newJsonBody(DslBodyFactory::locationSampleBody).build())
                .willRespondWith()
                .status(201)
                .matchHeader("Location", Regex.POST_CREATION_LOCATION_HEADER, "/locations/1")
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    locationSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact getLocationWithId1(PactDslWithProvider builder) {
        return builder.given(LOCATION_1_EXISTS)
                .uponReceiving("get an existing location")
                .path("/locations/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    locationSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact getNonExistinglocation(PactDslWithProvider builder) {
        return builder.given(NO_LOCATIONS_EXIST)
                .uponReceiving("get a non-existing location")
                .path("/locations/1")
                .method("GET")
                .willRespondWith()
                .status(404)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonBody(object -> {
                    object.stringType("error", "Location not found");
                    object.stringType("message", "Location with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact getAllLocations(PactDslWithProvider builder) {
        return builder.given(MULTIPLE_LOCATIONS_EXISTS)
                .uponReceiving("get all locations")
                .path("/locations")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(newJsonArrayMinLike(2, array -> array.object(object -> {
                    object.numberType("id", 1L);
                    locationSampleBody(object);
                })).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact getAllLocationsEmptyResponse(PactDslWithProvider builder) {
        return builder.given("NO_LOCATIONS_EXIST")
                .uponReceiving("get all locations when no locations exist")
                .path("/locations")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body("[]")
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact updateLocation(PactDslWithProvider builder) {
        return builder
                .given(LOCATION_1_EXISTS)
                .uponReceiving("update an existing location")
                .method("PUT")
                .headers("Content-Type", "application/json")
                .path("/locations/1")
                .body(newJsonBody(DslBodyFactory::locationSampleBody).build())
                .willRespondWith()
                .status(200)
                .body(newJsonBody(object -> {
                    object.numberType("id", 1L);
                    locationSampleBody(object);
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact updateNonExistentLocation(PactDslWithProvider builder) {
        return builder
                .given(MULTIPLE_LOCATIONS_EXISTS)
                .uponReceiving("update a non-existent location")
                .method("PUT")
                .headers("Content-Type", "application/json")
                .path("/locations/1")
                .body(newJsonBody(DslBodyFactory::locationSampleBody).build())
                .willRespondWith()
                .status(404)
                .body(newJsonBody(object -> {
                    object.stringType("error", "location not found");
                    object.stringType("message", "location with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact deleteLocation(PactDslWithProvider builder) {
        return builder
                .given(LOCATION_1_EXISTS)
                .uponReceiving("delete an existing location")
                .method("DELETE")
                .path("/locations/1")
                .willRespondWith()
                .status(204)
                .toPact().asV4Pact().get();
    }

    @Pact(consumer = "consumer", provider = "location-provider")
    public V4Pact deleteNonExistentLocation(PactDslWithProvider builder) {
        return builder
                .given(MULTIPLE_LOCATIONS_EXISTS)
                .uponReceiving("a request to delete a non-existent location")
                .method("DELETE")
                .path("/locations/1")
                .willRespondWith()
                .status(404)
                .body(newJsonBody(object -> {
                    object.stringType("error", "location not found");
                    object.stringType("message", "location with id 1 does not exist");
                }).build())
                .toPact().asV4Pact().get();
    }

    @BeforeEach
    void setup(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        locationService = new LocationService(restTemplate);
    }

    @Test
    @PactTestFor(pactMethod = "createLocation")
    void createLocationTest() {
        Location location = getLocationSample();

        Location createdLocation = locationService.createLocation(location);

        assertLocationDetails(location, createdLocation);
    }

    @Test
    @PactTestFor(pactMethod = "getLocationWithId1")
    void getLocationWhenLocationExists() {
        Location expectedLocation = getLocationSample();

        Location location = locationService.getLocation(1L);

        assertLocationDetails(expectedLocation, location);
    }

    @Test
    @PactTestFor(pactMethod = "getNonExistingLocation")
    void getNonExistingLocation() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> locationService.getLocation(1L));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    @PactTestFor(pactMethod = "getAllLocations")
    void getAllLocationsWhenLocationsExist() {
        Location expected = getLocationSample();

        List<Location> locations = locationService.getLocations();

        assertFalse(locations.isEmpty());
        assertEquals(2, locations.size());
        locations.forEach(location -> assertLocationDetails(expected, location));
    }

    @Test
    @PactTestFor(pactMethod = "getAllLocationsEmptyResponse")
    void getAllLocationsWhenNoLocationrsExist() {
        List<Location> locations = locationService.getLocations();

        assertTrue(locations.isEmpty());
    }

    @Test
    @PactTestFor(pactMethod = "updateLocation")
    void updateLocationTest() {
        Location location = getLocationSample();

        Location updatedLocation = locationService.updateLocation(1L, location);

        assertLocationDetails(location, updatedLocation);
    }

    @Test
    @PactTestFor(pactMethod = "updateNonExistentLocation")
    void updateNonExistentLocationTest() {
        Location nonExistentLocation = getLocationSample();

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> locationService.updateLocation(1L, nonExistentLocation));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }

    @Test
    @PactTestFor(pactMethod = "deleteLocation")
    void deleteLocationTest() {
        assertDoesNotThrow(() -> locationService.deleteLocation(1L));
    }

    @Test
    @PactTestFor(pactMethod = "deleteNonExistentLocation")
    void testDeleteNonExistentLocation() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> locationService.deleteLocation(1L));

        assertHttpClientError(exception, HttpStatus.NOT_FOUND);
    }
}