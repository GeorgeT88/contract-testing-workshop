package com.apenlor.pactflow.consumer.utils;

public class Regex {

    public static final String EMAIL = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
    public static final String POST_CREATION_LOCATION_HEADER = "^https?://[^/]+/students/[0-9]+$";

    private Regex() {
    }
}
