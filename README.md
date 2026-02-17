# BlueSphere - Weather-Based Logistics Risk Platform

A Spring Boot application that provides real-time weather-based delivery risk analysis for logistics operations. The system ingests weather forecasts, analyzes risk factors, and exposes REST APIs for Power BI dashboard integration.

## 🎯 Problem Statement

**Who uses this?**
- E-commerce companies (Amazon, Flipkart) - Predict delivery delays
- Cold-chain logistics (Pharma, Dairy) - Prevent spoilage from extreme weather
- Trucking fleet owners - Safety-first routing and insurance risk management
- Manufacturing plants - Plan for supply chain disruptions

**What problem does it solve?**
- Proactive identification of high-risk delivery zones
- Real-time weather-based route optimization
- Reduced insurance claims and accidents
- Better customer communication about delivery delays

## 🏗️ Architecture

### Data Pipeline
```
Weather API (OpenWeatherMap) 
    ↓ (Scheduled every 6 hours)
Ingestion Service 
    ↓ (Upsert to maintain 24-hour window)
SQL Database (H2/PostgreSQL)
    ↓ (Risk analysis)
Risk Calculation Service
    ↓ (REST API)
Power BI Dashboard
```

### Risk Criteria
A city is flagged as **HIGH RISK** if:
- **Heavy Rain**: Precipitation > 10mm
- **High Wind**: Wind Speed > 40 km/h  
- **Poor Visibility**: Visibility < 500m (Fog/Snow)

## 🚀 Quick Start

### Prerequisites
- Java 25 (or compatible JDK)
- Gradle 8.x
- OpenWeatherMap API Key (free tier available)

### 1. Get OpenWeatherMap API Key
1. Sign up at [OpenWeatherMap](https://openweathermap.org/api)
2. Get your free API key from the dashboard
3. Free tier includes: 1,000 calls/day, 5-day forecast

### 2. Configure API Key

**Option A: Environment Variable (Recommended)**
```bash
# Windows PowerShell
$env:OPENWEATHER_API_KEY="your_api_key_here"

# Linux/Mac
export OPENWEATHER_API_KEY="your_api_key_here"
```

**Option B: Update application.properties**
```properties
weather.api.key=your_api_key_here
```

### 3. Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 📊 API Endpoints

### 1. Get High-Risk Zones (Power BI Integration)
```http
GET /api/v1/logistics/risk-zones
```

**Response:**
```json
{
  "timestamp": "2026-02-17T21:58:00",
  "totalHighRiskCities": 3,
  "status": "success",
  "riskZones": [
    {
      "cityId": "1275339",
      "cityName": "Mumbai",
      "riskLevel": "HIGH",
      "precipitationMm": 15.5,
      "windSpeedKmh": 45.2,
      "visibilityMeters": 300.0,
      "weatherDescription": "heavy rain",
      "riskReasons": "Heavy Rain (15.5mm), High Wind (45.2 km/h), Poor Visibility (300m)",
      "timestamp": "2026-02-18T03:00:00"
    }
  ]
}
```

### 2. Get Risk for Specific City
```http
GET /api/v1/logistics/risk-zones/{cityId}
```

### 3. Manual Data Ingestion (Testing)
```http
POST /api/v1/logistics/ingest
```

### 4. Health Check
```http
GET /api/v1/logistics/health
```

## 🗄️ Database

### H2 Console (Development)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:bluesphere`
- Username: `sa`
- Password: (leave empty)

### Schema
```sql
CREATE TABLE weather_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    city_id VARCHAR(255) NOT NULL,
    city_name VARCHAR(255),
    timestamp TIMESTAMP NOT NULL,
    precipitation_mm DOUBLE,
    wind_speed_kmh DOUBLE,
    visibility_meters DOUBLE,
    temperature_celsius DOUBLE,
    weather_description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    UNIQUE(city_id, timestamp)
);
```

## ⚙️ Configuration

### Monitored Cities
Currently monitoring 10 major Indian logistics hubs:
- Mumbai, Delhi, Bangalore, Kolkata, Chennai
- Hyderabad, Pune, Ahmedabad, Jaipur, Lucknow

To add more cities, edit `WeatherIngestionService.java`:
```java
private static final List<String> MONITORED_CITIES = Arrays.asList(
    "Mumbai", "Delhi", "YourCity"
);
```

### Ingestion Schedule
Default: Every 6 hours (`0 0 */6 * * *`)

Change in `application.properties`:
```properties
weather.ingestion.cron=0 0 */6 * * *
```

### Risk Thresholds
Modify in `RiskCalculationService.java`:
```java
private static final double HEAVY_RAIN_THRESHOLD = 10.0; // mm
private static final double HIGH_WIND_THRESHOLD = 40.0; // km/h
private static final double LOW_VISIBILITY_THRESHOLD = 500.0; // meters
```

## 🔄 Data Flow

1. **Scheduled Ingestion** (every 6 hours)
   - Fetches 5-day forecast for each monitored city
   - Extracts next 24-hour window
   - Performs upsert (update existing or insert new)
   - Cleans up records older than 48 hours

2. **Risk Calculation** (on-demand via API)
   - Queries weather records for next 24 hours
   - Applies risk criteria
   - Groups by city and selects most severe conditions
   - Returns high-risk zones

## 📈 Power BI Integration

### Connect to API
1. In Power BI Desktop, select **Get Data** → **Web**
2. Enter URL: `http://localhost:8080/api/v1/logistics/risk-zones`
3. Select **riskZones** table
4. Create visualizations:
   - Map visual with city locations
   - Table showing risk reasons
   - Gauge for total high-risk cities
   - Time series of risk trends

### Refresh Schedule
- Set up automatic refresh in Power BI Service
- Recommended: Every 6 hours (aligned with ingestion)

## 🚢 Production Deployment

### Switch to PostgreSQL
1. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bluesphere
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

2. Disable H2 console:
```properties
spring.h2.console.enabled=false
```

### Environment Variables
```bash
OPENWEATHER_API_KEY=your_production_key
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/bluesphere
SPRING_DATASOURCE_USERNAME=db_user
SPRING_DATASOURCE_PASSWORD=db_password
```

## 🧪 Testing

### Manual Ingestion Test
```bash
curl -X POST http://localhost:8080/api/v1/logistics/ingest
```

### Check Risk Zones
```bash
curl http://localhost:8080/api/v1/logistics/risk-zones
```

### View Database
1. Open H2 Console: `http://localhost:8080/h2-console`
2. Run query:
```sql
SELECT * FROM weather_records 
WHERE timestamp > CURRENT_TIMESTAMP 
ORDER BY timestamp;
```

## 📝 Future Enhancements

1. **Real-time Alerts**
   - WebSocket notifications for sudden weather changes
   - Email/SMS alerts for critical risk zones

2. **Machine Learning**
   - Predict delivery delays using historical data
   - Route optimization algorithms

3. **Additional Data Sources**
   - Traffic data integration
   - Road condition APIs
   - Historical accident data

4. **Advanced Analytics**
   - Heatwave corridor detection
   - Seasonal pattern analysis
   - Cost impact calculations

## 🤝 Contributing

This is a demonstration project. To extend:
1. Add more cities to monitoring list
2. Integrate additional weather APIs
3. Implement custom risk scoring models
4. Add authentication for production use

## 📄 License

This project is for educational and demonstration purposes.

## 🆘 Troubleshooting

### "API key not found" error
- Ensure `OPENWEATHER_API_KEY` environment variable is set
- Or update `application.properties` with your key

### No data in database
- Trigger manual ingestion: `POST /api/v1/logistics/ingest`
- Check logs for API errors
- Verify API key is valid

### Empty risk zones
- Data ingestion may not have run yet
- Weather conditions may not meet risk thresholds
- Check database: `SELECT * FROM weather_records`

---

**Built with ❤️ for safer, smarter logistics**
