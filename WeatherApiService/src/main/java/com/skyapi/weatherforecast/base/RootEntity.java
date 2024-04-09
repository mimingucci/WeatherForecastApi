package com.skyapi.weatherforecast.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"locations_url", "location_by_code_url", "realtime_weather_by_ip_url",
   	"realtime_weather_by_code_url", "hourly_forecast_by_ip_url", "hourly_forecast_by_code_url",
   	"daily_forecast_by_ip_url", "daily_forecast_by_code_url", 
   	"full_weather_by_ip_url", "full_weather_by_code_url"
})
public class RootEntity {
	
    @JsonProperty("locations_url")
	private String locationsUrl;
    
    @JsonProperty("location_by_code_url")	
	private String locationByCodeUrl;
	
    @JsonProperty("realtime_weather_by_ip_url")
	private String realtimeWeatherByIpUrl;
    
    @JsonProperty("realtime_weather_by_code_url")
    private String realtimeWeatherByCodeUrl;

    @JsonProperty("hourly_forecast_by_ip_url")
    private String hourlyForecastByIpUrl;
    
    @JsonProperty("hourly_forecast_by_code_url")
    private String hourlyForecastByCodeUrl;

    @JsonProperty("daily_forecast_by_ip_url")
    private String dailyForecastByIpUrl;

    @JsonProperty("daily_forecast_by_code_url")
    private String dailyForecastByCodeUrl;

    @JsonProperty("full_weather_by_code_url")
    private String fullWeatherByCodeUrl;
    
    @JsonProperty("full_weather_by_ip_url")
    private String fullWeatherByIpUrl;


	public String getLocationsUrl() {
		return locationsUrl;
	}

	public void setLocationsUrl(String locationsUrl) {
		this.locationsUrl = locationsUrl;
	}

	public String getLocationByCodeUrl() {
		return locationByCodeUrl;
	}

	public void setLocationByCodeUrl(String locationByCodeUrl) {
		this.locationByCodeUrl = locationByCodeUrl;
	}

	public String getRealtimeWeatherByIpUrl() {
		return realtimeWeatherByIpUrl;
	}

	public void setRealtimeWeatherByIpUrl(String realtimeWeatherByIpUrl) {
		this.realtimeWeatherByIpUrl = realtimeWeatherByIpUrl;
	}

	public String getRealtimeWeatherByCodeUrl() {
		return realtimeWeatherByCodeUrl;
	}

	public void setRealtimeWeatherByCodeUrl(String realtimeWeatherByCodeUrl) {
		this.realtimeWeatherByCodeUrl = realtimeWeatherByCodeUrl;
	}

	public String getHourlyForecastByIpUrl() {
		return hourlyForecastByIpUrl;
	}

	public void setHourlyForecastByIpUrl(String hourlyForecastByIpUrl) {
		this.hourlyForecastByIpUrl = hourlyForecastByIpUrl;
	}

	public String getHourlyForecastByCodeUrl() {
		return hourlyForecastByCodeUrl;
	}

	public void setHourlyForecastByCodeUrl(String hourlyForecastByCodeUrl) {
		this.hourlyForecastByCodeUrl = hourlyForecastByCodeUrl;
	}

	public String getDailyForecastByIpUrl() {
		return dailyForecastByIpUrl;
	}

	public void setDailyForecastByIpUrl(String dailyForecastByIpUrl) {
		this.dailyForecastByIpUrl = dailyForecastByIpUrl;
	}

	public String getDailyForecastByCodeUrl() {
		return dailyForecastByCodeUrl;
	}

	public void setDailyForecastByCodeUrl(String dailyForecastByCodeUrl) {
		this.dailyForecastByCodeUrl = dailyForecastByCodeUrl;
	}

	public String getFullWeatherByCodeUrl() {
		return fullWeatherByCodeUrl;
	}

	public void setFullWeatherByCodeUrl(String fullWeatherByCodeUrl) {
		this.fullWeatherByCodeUrl = fullWeatherByCodeUrl;
	}

	public String getFullWeatherByIpUrl() {
		return fullWeatherByIpUrl;
	}

	public void setFullWeatherByIpUrl(String fullWeatherByIpUrl) {
		this.fullWeatherByIpUrl = fullWeatherByIpUrl;
	}
}
