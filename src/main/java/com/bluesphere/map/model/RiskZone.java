package com.bluesphere.map.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a city with high delivery risk.
 * Used for API responses to Power BI dashboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskZone {
    
    private String cityId;
    private String cityName;
    private String riskLevel;
    private Double precipitationMm;
    private Double windSpeedKmh;
    private Double visibilityMeters;
    private String weatherDescription;
    private String riskReasons;
    private String timestamp;
}
