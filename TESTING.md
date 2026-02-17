# BlueSphere API Testing Guide

## 🧪 Testing Methods

### Method 1: Browser (Easiest)
Simply open these URLs in your browser:

1. **Health Check**
   ```
   http://localhost:8080/api/v1/logistics/health
   ```

2. **Get All Risk Zones**
   ```
   http://localhost:8080/api/v1/logistics/risk-zones
   ```

3. **H2 Database Console**
   ```
   http://localhost:8080/h2-console
   ```

### Method 2: PowerShell (Windows)

```powershell
# Health Check
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/logistics/health" -Method GET

# Get All Risk Zones
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/logistics/risk-zones" -Method GET

# Get Risk for Mumbai (City ID: 1275339)
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/logistics/risk-zones/1275339" -Method GET

# Trigger Manual Ingestion
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/logistics/ingest" -Method POST

# Pretty Print JSON
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/logistics/risk-zones" -Method GET | ConvertTo-Json -Depth 10
```

### Method 3: curl (If installed)

```bash
# Health Check
curl http://localhost:8080/api/v1/logistics/health

# Get All Risk Zones
curl http://localhost:8080/api/v1/logistics/risk-zones

# Get Risk for Mumbai
curl http://localhost:8080/api/v1/logistics/risk-zones/1275339

# Trigger Manual Ingestion
curl -X POST http://localhost:8080/api/v1/logistics/ingest

# Pretty Print (with jq)
curl http://localhost:8080/api/v1/logistics/risk-zones | jq
```

### Method 4: Postman
1. Import `BlueSphere_API.postman_collection.json`
2. Run the collection
3. View responses in Postman UI

## 📊 Expected Responses

### Health Check Response
```json
{
  "status": "UP",
  "service": "BlueSphere Logistics Risk API",
  "version": "1.0.0"
}
```

### Risk Zones Response (Sample Data)
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
    },
    {
      "cityId": "1273294",
      "cityName": "Delhi",
      "riskLevel": "HIGH",
      "precipitationMm": 2.5,
      "windSpeedKmh": 52.3,
      "visibilityMeters": 8000.0,
      "weatherDescription": "strong wind",
      "riskReasons": "High Wind (52.3 km/h)",
      "timestamp": "2026-02-18T06:00:00"
    }
  ]
}
```

### Empty Risk Zones (No High Risk)
```json
{
  "timestamp": "2026-02-17T22:00:00",
  "totalHighRiskCities": 0,
  "status": "success",
  "riskZones": []
}
```

### Ingestion Trigger Response
```json
{
  "status": "Ingestion started",
  "message": "Weather data ingestion has been triggered. Check logs for progress."
}
```

## 🗄️ Database Testing

### Access H2 Console
1. Open: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:bluesphere`
3. Username: `sa`
4. Password: (leave empty)
5. Click "Connect"

### Sample SQL Queries

```sql
-- View all weather records
SELECT * FROM weather_records ORDER BY timestamp;

-- Count records by city
SELECT city_name, COUNT(*) as count 
FROM weather_records 
GROUP BY city_name;

-- View only high-risk records
SELECT 
    city_name,
    timestamp,
    precipitation_mm,
    wind_speed_kmh,
    visibility_meters,
    weather_description
FROM weather_records
WHERE 
    precipitation_mm > 10 
    OR wind_speed_kmh > 40 
    OR visibility_meters < 500
ORDER BY timestamp;

-- Get latest record per city
SELECT w1.*
FROM weather_records w1
INNER JOIN (
    SELECT city_id, MAX(timestamp) as max_ts
    FROM weather_records
    GROUP BY city_id
) w2 ON w1.city_id = w2.city_id AND w1.timestamp = w2.max_ts;

-- Check data freshness
SELECT 
    city_name,
    MIN(timestamp) as earliest_forecast,
    MAX(timestamp) as latest_forecast,
    COUNT(*) as total_records
FROM weather_records
GROUP BY city_name;
```

## 🔍 Verification Checklist

### ✅ Application Startup
- [ ] Application starts without errors
- [ ] Port 8080 is accessible
- [ ] Sample data loads (check logs for "Sample data loaded")
- [ ] No database connection errors

### ✅ API Endpoints
- [ ] Health check returns "UP"
- [ ] Risk zones endpoint returns data
- [ ] Response format matches expected JSON
- [ ] CORS headers present (for Power BI)

### ✅ Database
- [ ] H2 console accessible
- [ ] weather_records table exists
- [ ] Sample data visible in table
- [ ] Queries execute successfully

### ✅ Risk Calculation
- [ ] High-risk cities identified correctly
- [ ] Risk reasons populated
- [ ] Thresholds applied properly
- [ ] Multiple risk factors combined

## 🐛 Troubleshooting Tests

### Test 1: Check if Application is Running
```powershell
# Should return connection, not "connection refused"
Test-NetConnection -ComputerName localhost -Port 8080
```

### Test 2: Check Logs for Errors
Look for these in console output:
- ✅ "Started SphereApplication"
- ✅ "Sample data loaded: X weather records created"
- ❌ Any "ERROR" or "Exception" messages

### Test 3: Verify Database
```sql
-- Should return > 0
SELECT COUNT(*) FROM weather_records;
```

### Test 4: Test Risk Logic
```sql
-- Should return records with risk conditions
SELECT * FROM weather_records 
WHERE precipitation_mm > 10 
   OR wind_speed_kmh > 40 
   OR visibility_meters < 500;
```

## 📈 Performance Testing

### Load Test (Simple)
```powershell
# Make 100 requests
1..100 | ForEach-Object {
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/logistics/risk-zones" -Method GET
}
```

### Response Time Test
```powershell
Measure-Command {
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/logistics/risk-zones" -Method GET
}
```

Expected: < 500ms for risk zones endpoint

## 🎯 Integration Testing Scenarios

### Scenario 1: Fresh Start
1. Start application
2. Wait 5 seconds
3. Call `/risk-zones`
4. Verify sample data returned

### Scenario 2: Manual Ingestion
1. Set `OPENWEATHER_API_KEY` environment variable
2. Call `POST /ingest`
3. Wait 30 seconds
4. Call `/risk-zones`
5. Verify real weather data

### Scenario 3: Specific City Query
1. Call `/risk-zones` to get city IDs
2. Pick a city ID from response
3. Call `/risk-zones/{cityId}`
4. Verify single city data returned

### Scenario 4: Database Persistence
1. Call `/risk-zones` and note count
2. Restart application
3. Call `/risk-zones` again
4. Note: H2 in-memory DB will reset (expected)

## 📊 Power BI Testing

### Step 1: Connect to API
1. Power BI Desktop → Get Data → Web
2. URL: `http://localhost:8080/api/v1/logistics/risk-zones`
3. Click OK

### Step 2: Transform Data
1. Expand "riskZones" column
2. Select all fields
3. Click "Load"

### Step 3: Create Visualizations
1. **Map**: Use cityName for location
2. **Table**: Show all risk fields
3. **Card**: Show totalHighRiskCities
4. **Gauge**: Show risk score

### Step 4: Set Refresh
1. File → Options → Data Load
2. Set refresh interval: 6 hours
3. Publish to Power BI Service

## 🔄 Continuous Testing

### Watch Mode (Auto-refresh)
```powershell
# Refresh every 10 seconds
while ($true) {
    Clear-Host
    Write-Host "=== BlueSphere Risk Zones ===" -ForegroundColor Cyan
    Write-Host "Time: $(Get-Date)" -ForegroundColor Yellow
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/logistics/risk-zones" | ConvertTo-Json -Depth 10
    Start-Sleep -Seconds 10
}
```

## 📝 Test Results Template

```
=== BlueSphere Test Results ===
Date: _______________
Tester: _______________

✅ Application Startup: PASS / FAIL
✅ Health Check: PASS / FAIL
✅ Risk Zones Endpoint: PASS / FAIL
✅ Database Access: PASS / FAIL
✅ Sample Data Loaded: PASS / FAIL
✅ Risk Calculation: PASS / FAIL
✅ Manual Ingestion: PASS / FAIL

Notes:
_________________________________
_________________________________
```

---

**Happy Testing! 🚀**
