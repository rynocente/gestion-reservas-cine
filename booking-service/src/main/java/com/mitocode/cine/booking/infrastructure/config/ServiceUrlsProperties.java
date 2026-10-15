package com.mitocode.cine.booking.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "services")
public class ServiceUrlsProperties {
    private ServiceUrl movieService;
}