package com.bluesphere.map.service;

import com.bluesphere.map.dto.WeatherApiResponse;
import com.bluesphere.map.model.WeatherRecord;
import com.bluesphere.map.repository.WeatherRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service for ingesting weather data from external API.
 * Runs on a scheduled basis to maintain a 24-hour forward-looking window.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherIngestionService {
    
    private final WeatherApiClient weatherApiClient;
    private final WeatherRecordRepository weatherRecordRepository;
    
    // Major logistics hubs in India (can be configured via database/config)
    private static final List<String> MONITORED_CITIES = Arrays.asList(
            "Mumbai", "Delhi", "Bangalore", "Kolkata", "Chennai",
            "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow"
    );
    
    /**
     * Scheduled task to fetch and store weather forecasts.
     * Runs every 6 hours as configured in application.properties.
     */
    @Scheduled(cron = "${weather.ingestion.cron}")
    @Transactional
    public void ingestWeatherData() {
        log.info("Starting scheduled weather data ingestion...");
        
        int totalRecordsProcessed = 0;
        int totalRecordsUpserted = 0;
        
        for (String city : MONITORED_CITIES) {
            try {
                WeatherApiResponse forecast = weatherApiClient.fetchForecast(city);
                
                if (forecast != null && forecast.getList() != null) {
                    int upserted = processForecastData(forecast);
                    totalRecordsProcessed += forecast.getList().size();
                    totalRecordsUpserted += upserted;
                }
                
                // Rate limiting - avoid hitting API limits
                Thread.sleep(1000);
                
            } catch (Exception e) {
                log.error("Error processing weather data for city: {}", city, e);
            }
        }
        
        // Cleanup old records (older than 48 hours)
        cleanupOldRecords();
        
        log.info("Weather ingestion completed. Processed: {}, Upserted: {}", 
                totalRecordsProcessed, totalRecordsUpserted);
    }
    
    /**
     * Process forecast data and perform upsert operations.
     */
    private int processForecastData(WeatherApiResponse forecast) {
        int upsertCount = 0;
        String cityId = String.valueOf(forecast.getCity().getId());
        String cityName = forecast.getCity().getName();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusHours(24);
        
        for (WeatherApiResponse.ForecastItem item : forecast.getList()) {
            LocalDateTime timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(item.getTimestamp()), 
                    ZoneId.systemDefault()
            );
            
            // Only store records within 24-hour window
            if (timestamp.isAfter(now) && timestamp.isBefore(cutoff)) {
                WeatherRecord record = upsertWeatherRecord(cityId, cityName, item, timestamp);
                if (record != null) {
                    upsertCount++;
                }
            }
        }
        
        return upsertCount;
    }
    
    /**
     * Upsert (Update or Insert) weather record.
     */
    private WeatherRecord upsertWeatherRecord(
            String cityId, 
            String cityName, 
            WeatherApiResponse.ForecastItem item, 
            LocalDateTime timestamp) {
        
        // Check if record exists
        Optional<WeatherRecord> existingRecord = 
                weatherRecordRepository.findByCityIdAndTimestamp(cityId, timestamp);
        
        WeatherRecord record;
        if (existingRecord.isPresent()) {
            // Update existing record
            record = existingRecord.get();
            log.debug("Updating existing record for {} at {}", cityName, timestamp);
        } else {
            // Create new record
            record = new WeatherRecord();
            record.setCityId(cityId);
            record.setCityName(cityName);
            record.setTimestamp(timestamp);
            log.debug("Creating new record for {} at {}", cityName, timestamp);
        }
        
        // Update weather data
        record.setTemperatureCelsius(item.getMain().getTemperature());
        
        // Calculate precipitation (rain + snow)
        double precipitation = 0.0;
        if (item.getRain() != null && item.getRain().getThreeHour() != null) {
            precipitation += item.getRain().getThreeHour();
        }
        if (item.getSnow() != null && item.getSnow().getThreeHour() != null) {
            precipitation += item.getSnow().getThreeHour();
        }
        record.setPrecipitationMm(precipitation);
        
        // Convert wind speed from m/s to km/h
        double windSpeedKmh = item.getWind().getSpeed() * 3.6;
        record.setWindSpeedKmh(windSpeedKmh);
        
        // Visibility in meters
        record.setVisibilityMeters(item.getVisibility() != null ? item.getVisibility().doubleValue() : 10000.0);
        
        // Weather description
        if (item.getWeather() != null && !item.getWeather().isEmpty()) {
            record.setWeatherDescription(item.getWeather().get(0).getDescription());
        }
        
        return weatherRecordRepository.save(record);
    }
    
    /**
     * Delete records older than 48 hours to keep database clean.
     */
    @Transactional
    public void cleanupOldRecords() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(48);
        weatherRecordRepository.deleteRecordsOlderThan(cutoff);
        log.info("Cleaned up records older than {}", cutoff);
    }
    
    /**
     * Manual trigger for testing (can be called via API endpoint).
     */
    public void triggerManualIngestion() {
        log.info("Manual ingestion triggered");
        ingestWeatherData();
    }
}
