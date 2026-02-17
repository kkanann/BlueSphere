package com.bluesphere.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO for OpenWeatherMap 5-day forecast API response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {
    
    private City city;
    private List<ForecastItem> list;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private Long id;
        private String name;
        private String country;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        @JsonProperty("dt")
        private Long timestamp;
        
        private Main main;
        private Wind wind;
        private Rain rain;
        private Snow snow;
        private List<Weather> weather;
        
        @JsonProperty("visibility")
        private Integer visibility;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        @JsonProperty("temp")
        private Double temperature;
        
        private Double humidity;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private Double speed; // m/s
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rain {
        @JsonProperty("3h")
        private Double threeHour; // mm in last 3 hours
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snow {
        @JsonProperty("3h")
        private Double threeHour; // mm in last 3 hours
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String main;
        private String description;
    }
}
