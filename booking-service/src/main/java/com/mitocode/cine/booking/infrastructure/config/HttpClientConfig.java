package com.mitocode.cine.booking.infrastructure.config;

import com.mitocode.cine.booking.infrastructure.security.JwtPropagationInterceptor;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    @Primary
    public RestClient.Builder defaultRestClient(ObservationRegistry observationRegistry,
                                                JwtPropagationInterceptor jwtInterceptor) {

        return RestClient.builder()
                .observationRegistry(observationRegistry)
                .requestInterceptor(jwtInterceptor);
    }
}