# BlueSphere - Quick Start Guide

## 🚀 Get Started in 5 Minutes (Without API Key)

The application includes sample data for immediate testing!

### Step 1: Build the Project
```bash
./gradlew build
```

### Step 2: Run the Application
```bash
./gradlew bootRun
```

### Step 3: Test the API
Open your browser or use curl:

**Get High-Risk Zones:**
```bash
curl http://localhost:8080/api/v1/logistics/risk-zones
```

**Health Check:**
```bash
curl http://localhost:8080/api/v1/logistics/health
```

### Step 4: View Database
1. Open: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:bluesphere`
3. Username: `sa`
4. Password: (leave empty)
5. Click "Connect"

Run this query:
```sql
SELECT * FROM weather_records ORDER BY timestamp;
```

## 📊 Sample API Response

```json
{
  "timestamp": "2026-02-17T22:00:00",
  "totalHighRiskCities": 2,
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

## 🔑 Using Real Weather Data (Optional)

### Get Free API Key
1. Sign up: https://openweathermap.org/api
2. Copy your API key

### Set Environment Variable

**Windows PowerShell:**
```powershell
$env:OPENWEATHER_API_KEY="your_api_key_here"
./gradlew bootRun
```

**Linux/Mac:**
```bash
export OPENWEATHER_API_KEY="your_api_key_here"
./gradlew bootRun
```

### Trigger Data Ingestion
```bash
curl -X POST http://localhost:8080/api/v1/logistics/ingest
```

Wait 30-60 seconds, then check:
```bash
curl http://localhost:8080/api/v1/logistics/risk-zones
```

## 🎯 What to Test

1. **API Endpoints**
   - `/api/v1/logistics/risk-zones` - Get all high-risk cities
   - `/api/v1/logistics/risk-zones/{cityId}` - Get specific city risk
   - `/api/v1/logistics/health` - Health check

2. **Database Queries**
   ```sql
   -- View all records
   SELECT * FROM weather_records;
   
   -- High-risk records only
   SELECT * FROM weather_records 
   WHERE precipitation_mm > 10 
      OR wind_speed_kmh > 40 
      OR visibility_meters < 500;
   ```

3. **Power BI Integration**
   - Use URL: `http://localhost:8080/api/v1/logistics/risk-zones`
   - Select "riskZones" table
   - Create map visualization

## 🐛 Troubleshooting

**Port 8080 already in use?**
```bash
# Change port in application.properties
server.port=8081
```

**No data showing?**
- Sample data loads automatically in 'dev' profile
- Check logs for errors
- Verify database connection in H2 console

**Build fails?**
```bash
# Clean and rebuild
./gradlew clean build
```

## 📚 Next Steps

- Read full [README.md](README.md) for detailed documentation
- Customize monitored cities in `WeatherIngestionService.java`
- Adjust risk thresholds in `RiskCalculationService.java`
- Deploy to production with PostgreSQL

---

**Need help?** Check the logs in the console for detailed error messages.
