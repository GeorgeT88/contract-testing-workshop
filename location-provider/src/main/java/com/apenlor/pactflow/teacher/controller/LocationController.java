package com.apenlor.pactflow.teacher.controller;

import com.apenlor.pactflow.teacher.annotations.TechnicalDebt;
import com.apenlor.pactflow.teacher.entities.Location;
import com.apenlor.pactflow.teacher.exceptions.ErrorDetails;
import com.apenlor.pactflow.teacher.exceptions.LocationNotFoundException;
import com.apenlor.pactflow.teacher.repository.LocationRepository;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/locations")
@Slf4j
@TechnicalDebt(
        issue = "Controller is using JPA entities directly for request/response bodies.",
        solution = "Introduce Data Transfer Objects (DTOs) for API contracts. " +
                "Create LocationRequestDTO and LocationResponseDTO classes and use a " +
                "mapper (e.g., MapStruct) to convert between DTOs and the Location entity. " +
                "This was omitted to simplify the setup for the contract testing workshop."
)
public class LocationController {

    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "201", description = "Location created successfully",
            content = @Content(schema = @Schema(implementation = Location.class)),
            headers = @Header(name = "Location", description = "URL of the created Location", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        log.info("Creating location: {}", location);
        Location createdLocation = locationRepository.save(location);
        URI locations = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdLocation.getId())
                .toUri();
        return ResponseEntity.created(locations).body(createdLocation);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Location> getLocations() {
        log.info("Retrieving all locations");
        return StreamSupport.stream(locationRepository.findAll().spliterator(), false).toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Location found",
            content = @Content(schema = @Schema(implementation = Location.class)))
    @ApiResponse(responseCode = "404", description = "Location not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public Location getLocation(@PathVariable Long id) {
        log.info("Retrieving location by ID: {}", id);
        return getLocationById(id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200", description = "Location updated successfully",
            content = @Content(schema = @Schema(implementation = Location.class)))
    @ApiResponse(responseCode = "404", description = "Location not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public ResponseEntity<Location> updateLocation(@PathVariable Long id, @RequestBody Location location) {
        log.info("Updating location: {}", location);
        location.setId(getLocationById(id).getId());
        return ResponseEntity.ok(locationRepository.save(location));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Location deleted")
    @ApiResponse(responseCode = "404", description = "Location not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        log.info("Deleting location by ID: {}", id);
        locationRepository.deleteById(getLocationById(id).getId());
        return ResponseEntity.noContent().build();
    }

    private Location getLocationById(final Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));
    }

}
