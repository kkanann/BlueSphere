package com.bluesphere.map.service;

import com.bluesphere.map.model.RiskZone;
import com.bluesphere.map.model.WeatherRecord;
import com.bluesphere.map.repository.WeatherRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for calculating delivery risk based on weather conditions.
 * 
 * Risk Criteria:
 * - Heavy Rain: Precipitation > 10mm
 * - High Wind: Wind Speed > 40 km/h
 * - Poor Visibility: Visibility < 500m (Fog/Snow)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RiskCalculationService {
    
    private final WeatherRecordRepository weatherRecordRepository;
    
    // Risk thresholds
    private static final double HEAVY_RAIN_THRESHOLD = 10.0; // mm
    private static final double HIGH_WIND_THRESHOLD = 40.0; // km/h
    private static final double LOW_VISIBILITY_THRESHOLD = 500.0; // meters
    
    /**
     * Calculate and return all high-risk zones.
     * 
     * @return List of cities with high delivery risk
     */
    public List<RiskZone> calculateHighRiskZones() {
        log.info("Calculating high-risk zones...");
        
        LocalDateTime now = LocalDateTime.now();
        
        List<WeatherRecord> highRiskRecords = weatherRecordRepository.findHighRiskRecords(
                now,
                HEAVY_RAIN_THRESHOLD,
                HIGH_WIND_THRESHOLD,
                LOW_VISIBILITY_THRESHOLD
        );
        
        log.info("Found {} high-risk weather records", highRiskRecords.size());
        
        // Group by city and get the highest risk for each
        List<RiskZone> riskZones = highRiskRecords.stream()
                .collect(Collectors.groupingBy(WeatherRecord::getCityId))
                .values()
                .stream()
                .map(this::convertToRiskZone)
                .collect(Collectors.toList());
        
        log.info("Identified {} high-risk cities", riskZones.size());
        
        return riskZones;
    }
    
    /**
     * Convert weather records to risk zone DTO.
     * Takes the most severe conditions if multiple records exist for a city.
     */
    private RiskZone convertToRiskZone(List<WeatherRecord> records) {
        // Get the record with highest risk score
        WeatherRecord mostSevere = records.stream()
                .max((r1, r2) -> Double.compare(calculateRiskScore(r1), calculateRiskScore(r2)))
                .orElse(records.get(0));
        
        List<String> reasons = new ArrayList<>();
        
        if (mostSevere.getPrecipitationMm() > HEAVY_RAIN_THRESHOLD) {
            reasons.add(String.format("Heavy Rain (%.1fmm)", mostSevere.getPrecipitationMm()));
        }
        
        if (mostSevere.getWindSpeedKmh() > HIGH_WIND_THRESHOLD) {
            reasons.add(String.format("High Wind (%.1f km/h)", mostSevere.getWindSpeedKmh()));
        }
        
        if (mostSevere.getVisibilityMeters() < LOW_VISIBILITY_THRESHOLD) {
            reasons.add(String.format("Poor Visibility (%.0fm)", mostSevere.getVisibilityMeters()));
        }
        
        return RiskZone.builder()
                .cityId(mostSevere.getCityId())
                .cityName(mostSevere.getCityName())
                .riskLevel("HIGH")
                .precipitationMm(mostSevere.getPrecipitationMm())
                .windSpeedKmh(mostSevere.getWindSpeedKmh())
                .visibilityMeters(mostSevere.getVisibilityMeters())
                .weatherDescription(mostSevere.getWeatherDescription())
                .riskReasons(String.join(", ", reasons))
                .timestamp(mostSevere.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
    
    /**
     * Calculate a numeric risk score for comparison.
     * Higher score = higher risk.
     */
    private double calculateRiskScore(WeatherRecord record) {
        double score = 0.0;
        
        // Precipitation score (0-100)
        if (record.getPrecipitationMm() > HEAVY_RAIN_THRESHOLD) {
            score += Math.min(100, (record.getPrecipitationMm() / HEAVY_RAIN_THRESHOLD) * 50);
        }
        
        // Wind score (0-100)
        if (record.getWindSpeedKmh() > HIGH_WIND_THRESHOLD) {
            score += Math.min(100, (record.getWindSpeedKmh() / HIGH_WIND_THRESHOLD) * 50);
        }
        
        // Visibility score (0-100, inverted - lower visibility = higher score)
        if (record.getVisibilityMeters() < LOW_VISIBILITY_THRESHOLD) {
            score += Math.min(100, ((LOW_VISIBILITY_THRESHOLD - record.getVisibilityMeters()) / LOW_VISIBILITY_THRESHOLD) * 50);
        }
        
        return score;
    }
    
    /**
     * Get risk assessment for a specific city.
     */
    public RiskZone getRiskForCity(String cityId) {
        log.info("Calculating risk for city: {}", cityId);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(24);
        
        List<WeatherRecord> records = weatherRecordRepository.findRecordsInTimeWindow(now, endTime)
                .stream()
                .filter(r -> r.getCityId().equals(cityId))
                .collect(Collectors.toList());
        
        if (records.isEmpty()) {
            return null;
        }
        
        return convertToRiskZone(records);
    }
}
