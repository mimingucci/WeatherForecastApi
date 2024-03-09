package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyapi.weatherforecast.common.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

public class RealtimeWeatherDTO {
	@JsonInclude(value = Include.NON_NULL)
    private String location;
    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 and 50 celsius degree")
    private int temperature;
    @Range(min = 0, max = 100, message = "Humidity must be in the range of 0 to 100 percentage")
    private int humidity;
    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 percentage")
    private int precipitation;
    @JsonProperty("wind_speed")
    @Range(min = 0, max = 200, message = "Wind Speed must be in the range of 0 to 200 km/h")
    private int windSpeed;
    @NotBlank(message = "State must not be empty")
    @Length(min = 3, max = 30, message = "Status must be between 3-50 characters")
    private String status;
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date lastUpdated;

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(int precipitation) {
        this.precipitation = precipitation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
