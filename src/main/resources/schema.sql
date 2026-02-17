-- BlueSphere Database Schema
-- This schema is automatically created by JPA, but provided here for reference

-- Weather Records Table
CREATE TABLE IF NOT EXISTS weather_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    city_id VARCHAR(255) NOT NULL,
    city_name VARCHAR(255),
    timestamp TIMESTAMP NOT NULL,
    precipitation_mm DOUBLE PRECISION,
    wind_speed_kmh DOUBLE PRECISION,
    visibility_meters DOUBLE PRECISION,
    temperature_celsius DOUBLE PRECISION,
    weather_description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT uk_city_timestamp UNIQUE (city_id, timestamp)
);

-- Indexes for performance
CREATE INDEX idx_city_id ON weather_records(city_id);
CREATE INDEX idx_timestamp ON weather_records(timestamp);
CREATE INDEX idx_city_timestamp ON weather_records(city_id, timestamp);

-- Index for risk queries
CREATE INDEX idx_risk_conditions ON weather_records(
    precipitation_mm, 
    wind_speed_kmh, 
    visibility_meters
) WHERE timestamp >= CURRENT_TIMESTAMP;

-- Sample queries for testing

-- 1. Get all high-risk records
SELECT 
    city_name,
    timestamp,
    precipitation_mm,
    wind_speed_kmh,
    visibility_meters,
    weather_description
FROM weather_records
WHERE timestamp >= CURRENT_TIMESTAMP
  AND (
      precipitation_mm > 10.0 
      OR wind_speed_kmh > 40.0 
      OR visibility_meters < 500.0
  )
ORDER BY timestamp;

-- 2. Get latest record per city
SELECT w1.*
FROM weather_records w1
INNER JOIN (
    SELECT city_id, MAX(timestamp) as max_timestamp
    FROM weather_records
    GROUP BY city_id
) w2 ON w1.city_id = w2.city_id AND w1.timestamp = w2.max_timestamp;

-- 3. Count records by city
SELECT 
    city_name,
    COUNT(*) as record_count,
    MIN(timestamp) as earliest,
    MAX(timestamp) as latest
FROM weather_records
GROUP BY city_name
ORDER BY record_count DESC;

-- 4. Cleanup old records (older than 48 hours)
DELETE FROM weather_records
WHERE timestamp < CURRENT_TIMESTAMP - INTERVAL '48' HOUR;
