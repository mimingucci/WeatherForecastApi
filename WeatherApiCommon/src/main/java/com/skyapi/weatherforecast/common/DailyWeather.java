package com.skyapi.weatherforecast.common;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "weather_daily")
public class DailyWeather {
	@EmbeddedId
	private DailyWeatherId id=new DailyWeatherId();
	
	private int minTemp;
	private int maxTemp;
	private int precipitation;
	@Column(length = 50)
	private String status;
	public DailyWeatherId getId() {
		return id;
	}
	public void setId(DailyWeatherId id) {
		this.id = id;
	}
	public int getMinTemp() {
		return minTemp;
	}
	public void setMinTemp(int minTemp) {
		this.minTemp = minTemp;
	}
	public int getMaxTemp() {
		return maxTemp;
	}
	public void setMaxTemp(int maxTemp) {
		this.maxTemp = maxTemp;
	}
	public int getPrecipitation() {
		return precipitation;
	}
	public void setPrecipitation(int precipitation) {
		this.precipitation = precipitation;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public DailyWeather(DailyWeatherId id, int minTemp, int maxTemp, int precipitation, String status) {
		super();
		this.id = id;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.precipitation = precipitation;
		this.status = status;
	}
	public DailyWeather() {
		super();
	}
	
	public DailyWeather precipitation(int precipitation) {
		setPrecipitation(precipitation);
		return this;
	}

	public DailyWeather status(String status) {
		setStatus(status);
		return this;
	}
	
	public DailyWeather location(Location location) {
		this.id.setLocation(location);
		return this;
	}
	
	public DailyWeather dayOfMonth(int day) {
		this.id.setDayOfMonth(day);
		return this;
	}
	
	public DailyWeather month(int month) {
		this.id.setMonth(month);
		return this;
	}
	
	public DailyWeather minTemp(int temp) {
		setMinTemp(temp);
		return this;
	}
	
	public DailyWeather maxTemp(int temp) {
		setMaxTemp(temp);
		return this;
	}
}
