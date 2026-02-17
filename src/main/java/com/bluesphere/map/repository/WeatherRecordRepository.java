package com.bluesphere.map.repository;

import com.bluesphere.map.model.WeatherRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for WeatherRecord entity.
 * Provides custom queries for risk analysis and data cleanup.
 */
@Repository
public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {
    
    /**
     * Find weather record by city and timestamp for upsert logic.
     */
    Optional<WeatherRecord> findByCityIdAndTimestamp(String cityId, LocalDateTime timestamp);
    
    /**
     * Find all records within the 24-hour forward-looking window.
     */
    @Query("SELECT w FROM WeatherRecord w WHERE w.timestamp BETWEEN :startTime AND :endTime")
    List<WeatherRecord> findRecordsInTimeWindow(
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * Find high-risk weather records based on delivery risk criteria.
     */
    @Query("SELECT w FROM WeatherRecord w WHERE " +
           "w.timestamp >= :currentTime AND " +
           "(w.precipitationMm > :maxPrecipitation OR " +
           "w.windSpeedKmh > :maxWindSpeed OR " +
           "w.visibilityMeters < :minVisibility)")
    List<WeatherRecord> findHighRiskRecords(
        @Param("currentTime") LocalDateTime currentTime,
        @Param("maxPrecipitation") Double maxPrecipitation,
        @Param("maxWindSpeed") Double maxWindSpeed,
        @Param("minVisibility") Double minVisibility
    );
    
    /**
     * Delete records older than specified time (cleanup old data).
     */
    @Modifying
    @Query("DELETE FROM WeatherRecord w WHERE w.timestamp < :cutoffTime")
    void deleteRecordsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Get latest record for each city.
     */
    @Query("SELECT w FROM WeatherRecord w WHERE w.timestamp = " +
           "(SELECT MAX(w2.timestamp) FROM WeatherRecord w2 WHERE w2.cityId = w.cityId)")
    List<WeatherRecord> findLatestRecordsPerCity();
}
