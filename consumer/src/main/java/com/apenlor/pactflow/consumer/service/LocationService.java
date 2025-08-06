package com.apenlor.pactflow.consumer.service;

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
public class LocationService {

    private static final String BASE_URI_LOCATIONS = "/locations";
    private static final String URI_LOCATION_BY_ID = BASE_URI_LOCATIONS + "/{id}";

    private final RestTemplate restTemplate;

    @Autowired
    public LocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Location createLocation(Location location) {
        return restTemplate.postForObject(BASE_URI_LOCATIONS, location, Location.class);
    }

    public Location getLocation(Long id) {
        return restTemplate.getForObject(URI_LOCATION_BY_ID, Location.class, id);
    }

    public List<Location> getLocations() {
        return restTemplate.exchange(BASE_URI_LOCATIONS, HttpMethod.GET, null, new ParameterizedTypeReference<List<Location>>() {
        }).getBody();
    }

    public Location updateLocation(Long id, Location location) {
        HttpEntity<Location> entity = new HttpEntity<>(location, new HttpHeaders());
        return restTemplate.exchange(URI_LOCATION_BY_ID, HttpMethod.PUT, entity, Location.class, id).getBody();
    }

    public void deleteLocation(Long id) {
        restTemplate.delete(URI_LOCATION_BY_ID, id);
    }
}