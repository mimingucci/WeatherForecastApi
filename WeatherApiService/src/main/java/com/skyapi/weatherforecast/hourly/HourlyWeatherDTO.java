package com.skyapi.weatherforecast.hourly;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.skyapi.weatherforecast.common.HourlyWeather;

import jakarta.validation.constraints.NotBlank;
@JsonPropertyOrder({"hour_of_day", "temperature", "precipitation", "status"})
public class HourlyWeatherDTO {
	@JsonProperty("hour_of_day")
	@Range(min = 0, max = 23, message = "Hour of day must be between 0-23")
    private int hourOfDay;
    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 and 50 celsius degree")
    private int temperature;
    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 percentage")
    private int precipitation;
    @NotBlank(message = "State must not be empty")
    @Length(min = 3, max = 30, message = "Status must be between 3-50 characters")
    private String status;

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
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

    public HourlyWeatherDTO(int hourOfDay, int temperature, int precipitation, String status) {
        this.hourOfDay = hourOfDay;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.status = status;
    }

    public HourlyWeatherDTO() {
    }

    public HourlyWeatherDTO status(String status){
        setStatus(status);
        return this;
    }

    public HourlyWeatherDTO precipitation(int precipitation){
        this.setPrecipitation(precipitation);
        return this;
    }

    public HourlyWeatherDTO hourOfDay(int hour){
        setHourOfDay(hour);
        return this;
    }

    public HourlyWeatherDTO temperature(int temp){
        setTemperature(temp);
        return this;
    }
}
