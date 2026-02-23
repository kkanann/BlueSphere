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

## contributing

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
