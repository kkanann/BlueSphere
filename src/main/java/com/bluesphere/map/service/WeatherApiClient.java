package com.bluesphere.map.service;

import com.bluesphere.map.dto.WeatherApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service for fetching weather data from OpenWeatherMap API.
 */
@Service
@Slf4j
public class WeatherApiClient {
    
    private final WebClient webClient;
    private final String apiKey;
    
    public WeatherApiClient(
            @Value("${weather.api.base-url}") String baseUrl,
            @Value("${weather.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
    }
    
    /**
     * Fetch 5-day weather forecast for a city.
     * 
     * @param cityName Name of the city
     * @return Weather forecast data
     */
    public WeatherApiResponse fetchForecast(String cityName) {
        try {
            log.info("Fetching weather forecast for city: {}", cityName);
            
            WeatherApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("q", cityName)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .retrieve()
                    .bodyToMono(WeatherApiResponse.class)
                    .block();
            
            log.info("Successfully fetched forecast for {}: {} data points", 
                    cityName, response != null ? response.getList().size() : 0);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error fetching weather data for city: {}", cityName, e);
            return null;
        }
    }
    
    /**
     * Fetch weather forecast by city ID.
     * 
     * @param cityId OpenWeatherMap city ID
     * @return Weather forecast data
     */
    public WeatherApiResponse fetchForecastById(String cityId) {
        try {
            log.info("Fetching weather forecast for city ID: {}", cityId);
            
            WeatherApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("id", cityId)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .retrieve()
                    .bodyToMono(WeatherApiResponse.class)
                    .block();
            
            log.info("Successfully fetched forecast for city ID {}", cityId);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error fetching weather data for city ID: {}", cityId, e);
            return null;
        }
    }
}
