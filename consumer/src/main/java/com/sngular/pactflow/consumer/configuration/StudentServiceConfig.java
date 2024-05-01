package com.sngular.pactflow.consumer.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StudentServiceConfig {

    @Bean
    RestTemplate studentRestTemplate(@Value("${provider.port:8082}") int port) {
        return new RestTemplateBuilder().rootUri(String.format("http://localhost:%d", port)).build();
    }
}
