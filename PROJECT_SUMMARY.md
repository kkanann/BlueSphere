# BlueSphere Project Summary

## 🎯 Project Overview

**BlueSphere** is a weather-based logistics risk platform that helps supply chain operations identify high-risk delivery zones in real-time. The system ingests weather forecasts, analyzes risk factors, and provides REST APIs for dashboard integration.

## 💡 Problem Solved

### Target Users
1. **E-commerce Companies** (Amazon, Flipkart)
   - Problem: Last-mile delivery delays cause customer refunds
   - Solution: Predict unreachable zip codes due to weather

2. **Cold-Chain Logistics** (Pharma, Dairy)
   - Problem: Extreme heat spoils temperature-sensitive cargo
   - Solution: Identify heatwave corridors for route planning

3. **Trucking Fleet Owners**
   - Problem: Accidents in bad weather increase insurance costs
   - Solution: Safety-first routing with risk zone alerts

4. **Manufacturing Plants**
   - Problem: Raw material delays stop production lines
   - Solution: 48-hour advance storm warnings at ports

## 🏗️ Technical Architecture

### Technology Stack
- **Backend**: Spring Boot 4.0.2
- **Database**: H2 (dev) / PostgreSQL (prod)
- **API Client**: WebClient (Spring WebFlux)
- **Data Source**: OpenWeatherMap API
- **Scheduling**: Spring @Scheduled
- **ORM**: Spring Data JPA

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                    External Data Source                      │
│                  OpenWeatherMap 5-Day API                    │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ HTTP GET (every 6 hours)
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   Ingestion Service                          │
│  - Fetches forecast for 10 cities                           │
│  - Extracts 24-hour window                                   │
│  - Performs UPSERT (update or insert)                        │
│  - Cleans up old data (>48 hours)                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Save/Update
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Database Layer                          │
│                    weather_records table                     │
│  Columns: city_id, timestamp, precipitation_mm,             │
│           wind_speed_kmh, visibility_meters                  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Query
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  Risk Calculation Service                    │
│  Flags HIGH RISK if:                                         │
│  - Precipitation > 10mm (Heavy Rain)                         │
│  - Wind Speed > 40 km/h (High Wind)                          │
│  - Visibility < 500m (Fog/Snow)                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Return RiskZone DTOs
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      REST API Layer                          │
│  GET /api/v1/logistics/risk-zones                           │
│  GET /api/v1/logistics/risk-zones/{cityId}                  │
│  POST /api/v1/logistics/ingest                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ JSON Response
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Power BI Dashboard                        │
│  - Map visualization of risk zones                           │
│  - Real-time risk metrics                                    │
│  - Historical trend analysis                                 │
└─────────────────────────────────────────────────────────────┘
```

## 📁 Project Structure

```
sphere/
├── src/main/java/com/bluesphere/map/
│   ├── SphereApplication.java          # Main application class
│   ├── config/
│   │   └── AppConfig.java              # Configuration beans
│   ├── controller/
│   │   └── LogisticsRiskController.java # REST API endpoints
│   ├── dto/
│   │   └── WeatherApiResponse.java     # API response DTOs
│   ├── model/
│   │   ├── WeatherRecord.java          # JPA entity
│   │   └── RiskZone.java               # Response DTO
│   ├── repository/
│   │   └── WeatherRecordRepository.java # Database queries
│   └── service/
│       ├── WeatherApiClient.java       # External API client
│       ├── WeatherIngestionService.java # Scheduled ingestion
│       ├── RiskCalculationService.java # Risk logic
│       └── SampleDataLoader.java       # Test data (dev only)
├── src/main/resources/
│   ├── application.properties          # Configuration
│   └── schema.sql                      # Database schema
├── build.gradle                        # Dependencies
├── README.md                           # Full documentation
├── QUICKSTART.md                       # 5-minute guide
├── SETUP.md                            # Installation guide
└── PROJECT_SUMMARY.md                  # This file
```

## 🔑 Key Features

### 1. Automated Data Ingestion
- **Scheduled Task**: Runs every 6 hours (configurable)
- **Cities Monitored**: 10 major Indian logistics hubs
- **Data Window**: Maintains 24-hour forward-looking forecast
- **Upsert Logic**: Updates existing records or inserts new ones
- **Auto Cleanup**: Deletes records older than 48 hours

### 2. Risk Calculation Engine
- **Multi-Factor Analysis**: Precipitation, wind, visibility
- **Risk Scoring**: Weighted algorithm for severity ranking
- **City Grouping**: Aggregates multiple forecasts per city
- **Threshold-Based**: Configurable risk thresholds

### 3. REST API
- **Power BI Ready**: JSON format optimized for BI tools
- **CORS Enabled**: Cross-origin requests allowed
- **Health Monitoring**: Built-in health check endpoint
- **Manual Trigger**: On-demand ingestion for testing

### 4. Development Features
- **Sample Data**: Auto-loads test data in dev mode
- **H2 Console**: In-memory database with web UI
- **Detailed Logging**: DEBUG level for troubleshooting
- **Hot Reload**: DevTools for rapid development

## 📊 Data Model

### WeatherRecord Entity
| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key |
| cityId | String | OpenWeatherMap city ID |
| cityName | String | Human-readable city name |
| timestamp | LocalDateTime | Forecast timestamp |
| precipitationMm | Double | Rainfall in mm (3-hour window) |
| windSpeedKmh | Double | Wind speed in km/h |
| visibilityMeters | Double | Visibility in meters |
| temperatureCelsius | Double | Temperature in °C |
| weatherDescription | String | Weather condition text |
| createdAt | LocalDateTime | Record creation time |
| updatedAt | LocalDateTime | Last update time |

### RiskZone DTO (API Response)
| Field | Type | Description |
|-------|------|-------------|
| cityId | String | City identifier |
| cityName | String | City name |
| riskLevel | String | "HIGH" |
| precipitationMm | Double | Current precipitation |
| windSpeedKmh | Double | Current wind speed |
| visibilityMeters | Double | Current visibility |
| weatherDescription | String | Weather condition |
| riskReasons | String | Comma-separated risk factors |
| timestamp | String | ISO timestamp |

## 🔧 Configuration

### Environment Variables
```bash
OPENWEATHER_API_KEY=your_api_key_here
SPRING_PROFILES_ACTIVE=dev
```

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:h2:mem:bluesphere

# Scheduling
weather.ingestion.cron=0 0 */6 * * *

# API
weather.api.base-url=https://api.openweathermap.org/data/2.5
```

### Risk Thresholds (Code)
```java
HEAVY_RAIN_THRESHOLD = 10.0 mm
HIGH_WIND_THRESHOLD = 40.0 km/h
LOW_VISIBILITY_THRESHOLD = 500.0 meters
```

## 🚀 Deployment Options

### Development
- **Database**: H2 in-memory
- **Profile**: `dev`
- **Sample Data**: Auto-loaded
- **Console**: Enabled at `/h2-console`

### Production
- **Database**: PostgreSQL
- **Profile**: `prod`
- **API Key**: Required (environment variable)
- **Monitoring**: Spring Actuator (optional)

## 📈 Future Enhancements

### Phase 2: Real-Time Alerts
- WebSocket notifications
- Email/SMS integration
- Slack/Teams webhooks

### Phase 3: Machine Learning
- Delivery delay prediction
- Route optimization AI
- Historical pattern analysis

### Phase 4: Extended Data
- Traffic data integration
- Road condition APIs
- Accident history correlation

### Phase 5: Advanced Analytics
- Cost impact calculator
- Insurance premium estimator
- Carbon footprint tracker

## 🎓 Learning Outcomes

This project demonstrates:
- ✅ Spring Boot REST API development
- ✅ Scheduled task execution
- ✅ External API integration (WebClient)
- ✅ JPA/Hibernate ORM
- ✅ Database design and querying
- ✅ DTO pattern implementation
- ✅ Service layer architecture
- ✅ Error handling and logging
- ✅ Configuration management
- ✅ Development vs production profiles

## 📚 Documentation Files

1. **README.md** - Comprehensive documentation
2. **QUICKSTART.md** - 5-minute getting started guide
3. **SETUP.md** - Detailed installation instructions
4. **PROJECT_SUMMARY.md** - This overview document

## 🤝 Contributing

To extend this project:
1. Add more cities to `MONITORED_CITIES` list
2. Integrate additional weather APIs
3. Implement custom risk scoring algorithms
4. Add authentication/authorization
5. Create frontend dashboard

## 📄 License

Educational and demonstration purposes.

---

**Built with Spring Boot 4.0.2 | Java 21 | H2/PostgreSQL**
