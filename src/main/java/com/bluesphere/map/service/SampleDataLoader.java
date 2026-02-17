package com.bluesphere.map.service;

import com.bluesphere.map.model.WeatherRecord;
import com.bluesphere.map.repository.WeatherRecordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Service to load sample data for testing.
 * Only active in 'dev' profile.
 */
@Service
@Profile("dev")
@Slf4j
@RequiredArgsConstructor
public class SampleDataLoader {
    
    private final WeatherRecordRepository weatherRecordRepository;
    private final Random random = new Random();
    
    @PostConstruct
    public void loadSampleData() {
        log.info("Loading sample weather data for testing...");
        
        List<String> cities = Arrays.asList(
                "1275339:Mumbai",
                "1273294:Delhi", 
                "1277333:Bangalore",
                "1275004:Kolkata",
                "1264527:Chennai"
        );
        
        LocalDateTime now = LocalDateTime.now();
        int recordsCreated = 0;
        
        for (String cityData : cities) {
            String[] parts = cityData.split(":");
            String cityId = parts[0];
            String cityName = parts[1];
            
            // Create records for next 24 hours (every 3 hours)
            for (int hour = 3; hour <= 24; hour += 3) {
                LocalDateTime timestamp = now.plusHours(hour);
                
                WeatherRecord record = WeatherRecord.builder()
                        .cityId(cityId)
                        .cityName(cityName)
                        .timestamp(timestamp)
                        .precipitationMm(generatePrecipitation())
                        .windSpeedKmh(generateWindSpeed())
                        .visibilityMeters(generateVisibility())
                        .temperatureCelsius(25.0 + random.nextDouble() * 10)
                        .weatherDescription(getRandomWeatherDescription())
                        .build();
                
                weatherRecordRepository.save(record);
                recordsCreated++;
            }
        }
        
        log.info("Sample data loaded: {} weather records created", recordsCreated);
    }
    
    private double generatePrecipitation() {
        // 30% chance of heavy rain (>10mm)
        if (random.nextDouble() < 0.3) {
            return 10.0 + random.nextDouble() * 20.0; // 10-30mm
        }
        return random.nextDouble() * 8.0; // 0-8mm
    }
    
    private double generateWindSpeed() {
        // 20% chance of high wind (>40 km/h)
        if (random.nextDouble() < 0.2) {
            return 40.0 + random.nextDouble() * 30.0; // 40-70 km/h
        }
        return 10.0 + random.nextDouble() * 25.0; // 10-35 km/h
    }
    
    private double generateVisibility() {
        // 15% chance of poor visibility (<500m)
        if (random.nextDouble() < 0.15) {
            return 100.0 + random.nextDouble() * 400.0; // 100-500m
        }
        return 1000.0 + random.nextDouble() * 9000.0; // 1000-10000m
    }
    
    private String getRandomWeatherDescription() {
        String[] descriptions = {
                "clear sky", "few clouds", "scattered clouds",
                "broken clouds", "light rain", "moderate rain",
                "heavy rain", "mist", "fog", "strong wind"
        };
        return descriptions[random.nextInt(descriptions.length)];
    }
}
