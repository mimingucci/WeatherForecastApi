package com.skyapi.weatherforecast.daily;

import java.util.List;

public class DailyWeatherListDTO {

	private String location;
	private List<DailyWeatherDTO> dailyForecast;
	public void addDailyWeatherDTO(DailyWeatherDTO dto) {
		this.dailyForecast.add(dto);
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public List<DailyWeatherDTO> getDailyForecast() {
		return dailyForecast;
	}
	public void setDailyForecast(List<DailyWeatherDTO> dailyForecast) {
		this.dailyForecast = dailyForecast;
	}
	public DailyWeatherListDTO(String location, List<DailyWeatherDTO> dailyForecast) {
		super();
		this.location = location;
		this.dailyForecast = dailyForecast;
	}
	public DailyWeatherListDTO() {
		super();
	}
	
	
}
