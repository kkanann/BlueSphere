package com.bluesphere.map.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing weather records for logistics risk analysis.
 * Stores weather data for cities to calculate delivery risks.
 */
@Entity
@Table(name = "weather_records", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"city_id", "timestamp"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "city_id", nullable = false)
    private String cityId;
    
    @Column(name = "city_name")
    private String cityName;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "precipitation_mm")
    private Double precipitationMm;
    
    @Column(name = "wind_speed_kmh")
    private Double windSpeedKmh;
    
    @Column(name = "visibility_meters")
    private Double visibilityMeters;
    
    @Column(name = "temperature_celsius")
    private Double temperatureCelsius;
    
    @Column(name = "weather_description")
    private String weatherDescription;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
