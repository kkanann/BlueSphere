# BlueSphere - Setup & Installation Guide

## ⚠️ Prerequisites

### 1. Install Java Development Kit (JDK)

**Option A: Install Java 21 (Recommended for compatibility)**

1. Download from: https://adoptium.net/
2. Choose: **Eclipse Temurin 21 (LTS)**
3. Install and note the installation path (e.g., `C:\Program Files\Eclipse Adoptium\jdk-21.0.x`)

**Option B: Install Java 17 (Also compatible)**

1. Download from: https://adoptium.net/
2. Choose: **Eclipse Temurin 17 (LTS)**

### 2. Set JAVA_HOME Environment Variable

**Windows:**

1. Open **System Properties** → **Advanced** → **Environment Variables**
2. Under **System Variables**, click **New**
3. Variable name: `JAVA_HOME`
4. Variable value: `C:\Program Files\Eclipse Adoptium\jdk-21.0.x` (your Java installation path)
5. Click **OK**

6. Edit the **Path** variable:
   - Click **Edit** on the **Path** variable
   - Click **New**
   - Add: `%JAVA_HOME%\bin`
   - Click **OK**

**Verify Installation:**
```powershell
# Open a NEW PowerShell window
java -version
# Should show: openjdk version "21.x.x" or similar
```

### 3. Verify Gradle (Included in Project)

The project includes Gradle Wrapper, so no separate installation needed!

```powershell
./gradlew --version
```

## 🚀 Build and Run

### Step 1: Build the Project
```powershell
./gradlew clean build
```

This will:
- Download dependencies
- Compile Java code
- Run tests
- Create executable JAR

### Step 2: Run the Application

**Option A: Using Gradle**
```powershell
./gradlew bootRun
```

**Option B: Using JAR**
```powershell
java -jar build/libs/sphere-0.0.1-SNAPSHOT.jar
```

### Step 3: Verify Application is Running

Open browser: http://localhost:8080/api/v1/logistics/health

Expected response:
```json
{
  "status": "UP",
  "service": "BlueSphere Logistics Risk API",
  "version": "1.0.0"
}
```

## 🔧 Configuration

### Update build.gradle for Java 17/21

If you have Java 17 or 21 instead of 25, update `build.gradle`:

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)  // or 17
    }
}
```

## 🧪 Testing Without Building

If you can't build yet, here's what the application does:

### Architecture Overview
```
┌─────────────────────────────────────────────────────────┐
│                   BlueSphere Platform                    │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐      ┌──────────────┐                │
│  │ OpenWeather  │─────▶│  Ingestion   │                │
│  │     API      │      │   Service    │                │
│  └──────────────┘      └──────┬───────┘                │
│                               │                         │
│                               ▼                         │
│                        ┌──────────────┐                │
│                        │   Database   │                │
│                        │  (H2/Postgres)│               │
│                        └──────┬───────┘                │
│                               │                         │
│                               ▼                         │
│                        ┌──────────────┐                │
│                        │     Risk     │                │
│                        │ Calculation  │                │
│                        └──────┬───────┘                │
│                               │                         │
│                               ▼                         │
│                        ┌──────────────┐                │
│                        │  REST API    │                │
│                        │  Endpoints   │                │
│                        └──────┬───────┘                │
│                               │                         │
└───────────────────────────────┼─────────────────────────┘
                                │
                                ▼
                         ┌──────────────┐
                         │   Power BI   │
                         │  Dashboard   │
                         └──────────────┘
```

### Key Components Created

1. **Model Layer**
   - `WeatherRecord.java` - JPA entity for weather data
   - `RiskZone.java` - DTO for API responses

2. **Repository Layer**
   - `WeatherRecordRepository.java` - Database queries

3. **Service Layer**
   - `WeatherApiClient.java` - Fetches data from OpenWeatherMap
   - `WeatherIngestionService.java` - Scheduled data ingestion
   - `RiskCalculationService.java` - Risk analysis logic
   - `SampleDataLoader.java` - Test data generator

4. **Controller Layer**
   - `LogisticsRiskController.java` - REST API endpoints

## 📊 Database Schema

```sql
weather_records
├── id (BIGINT, PK)
├── city_id (VARCHAR)
├── city_name (VARCHAR)
├── timestamp (TIMESTAMP)
├── precipitation_mm (DOUBLE)
├── wind_speed_kmh (DOUBLE)
├── visibility_meters (DOUBLE)
├── temperature_celsius (DOUBLE)
├── weather_description (VARCHAR)
├── created_at (TIMESTAMP)
└── updated_at (TIMESTAMP)
```

## 🎯 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/logistics/risk-zones` | GET | Get all high-risk cities |
| `/api/v1/logistics/risk-zones/{cityId}` | GET | Get risk for specific city |
| `/api/v1/logistics/ingest` | POST | Trigger manual data ingestion |
| `/api/v1/logistics/health` | GET | Health check |
| `/h2-console` | GET | Database console (dev only) |

## 🔍 Risk Calculation Logic

```java
HIGH RISK if any condition is true:
- Precipitation > 10mm (Heavy Rain)
- Wind Speed > 40 km/h (High Wind)
- Visibility < 500m (Fog/Snow)
```

## 📈 Sample Power BI Integration

### DAX Measures
```dax
TotalHighRiskCities = COUNTROWS(RiskZones)

AvgPrecipitation = AVERAGE(RiskZones[precipitationMm])

RiskScore = 
    IF(RiskZones[precipitationMm] > 10, 1, 0) +
    IF(RiskZones[windSpeedKmh] > 40, 1, 0) +
    IF(RiskZones[visibilityMeters] < 500, 1, 0)
```

## 🐛 Common Issues

### Issue: "JAVA_HOME is not set"
**Solution:** Follow the Java installation steps above

### Issue: "Port 8080 already in use"
**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: "API returns empty risk zones"
**Solution:** 
- Sample data loads automatically in dev mode
- Or trigger ingestion: `POST /api/v1/logistics/ingest`
- Check database: http://localhost:8080/h2-console

### Issue: "Build fails with compilation errors"
**Solution:**
```powershell
./gradlew clean build --refresh-dependencies
```

## 🚀 Next Steps

1. ✅ Install Java
2. ✅ Build the project
3. ✅ Run the application
4. ✅ Test API endpoints
5. ✅ View data in H2 console
6. ✅ Get OpenWeatherMap API key (optional)
7. ✅ Connect Power BI dashboard

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [OpenWeatherMap API Docs](https://openweathermap.org/api)
- [Power BI REST API Connector](https://learn.microsoft.com/en-us/power-bi/connect-data/desktop-connect-to-web)
- [H2 Database Documentation](https://www.h2database.com/)

---

**Need Help?** Check the application logs for detailed error messages.
