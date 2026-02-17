package com.bluesphere.map.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Application configuration for BlueSphere.
 */
@Configuration
public class AppConfig {
    
    /**
     * WebClient bean for making HTTP requests.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
