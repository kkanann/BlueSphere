package com.bluesphere.map.controller;

import com.bluesphere.map.model.RiskZone;
import com.bluesphere.map.service.RiskCalculationService;
import com.bluesphere.map.service.WeatherIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API controller for logistics risk analysis.
 * Provides endpoints for Power BI dashboard integration.
 */
@RestController
@RequestMapping("/api/v1/logistics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow Power BI to access
public class LogisticsRiskController {
    
    private final RiskCalculationService riskCalculationService;
    private final WeatherIngestionService weatherIngestionService;
    
    /**
     * Get all high-risk zones for delivery operations.
     * Primary endpoint for Power BI dashboard.
     * 
     * @return List of cities with high delivery risk
     */
    @GetMapping("/risk-zones")
    public ResponseEntity<Map<String, Object>> getHighRiskZones() {
        log.info("API request: GET /api/v1/logistics/risk-zones");
        
        List<RiskZone> riskZones = riskCalculationService.calculateHighRiskZones();
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("totalHighRiskCities", riskZones.size());
        response.put("riskZones", riskZones);
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get risk assessment for a specific city.
     * 
     * @param cityId City identifier
     * @return Risk zone information for the city
     */
    @GetMapping("/risk-zones/{cityId}")
    public ResponseEntity<RiskZone> getRiskForCity(@PathVariable String cityId) {
        log.info("API request: GET /api/v1/logistics/risk-zones/{}", cityId);
        
        RiskZone riskZone = riskCalculationService.getRiskForCity(cityId);
        
        if (riskZone == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(riskZone);
    }
    
    /**
     * Manually trigger weather data ingestion.
     * Useful for testing and on-demand updates.
     * 
     * @return Status message
     */
    @PostMapping("/ingest")
    public ResponseEntity<Map<String, String>> triggerIngestion() {
        log.info("API request: POST /api/v1/logistics/ingest");
        
        // Run ingestion asynchronously
        new Thread(() -> weatherIngestionService.triggerManualIngestion()).start();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "Ingestion started");
        response.put("message", "Weather data ingestion has been triggered. Check logs for progress.");
        
        return ResponseEntity.accepted().body(response);
    }
    
    /**
     * Health check endpoint.
     * 
     * @return API status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "BlueSphere Logistics Risk API");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }
}
