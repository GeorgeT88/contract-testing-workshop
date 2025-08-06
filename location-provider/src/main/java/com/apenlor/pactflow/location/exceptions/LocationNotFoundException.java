package com.apenlor.pactflow.location.exceptions;

public class LocationNotFoundException extends RuntimeException {

    public LocationNotFoundException(Long id) {
        super(String.format("Location with id %d does not exist", id));
    }
}

