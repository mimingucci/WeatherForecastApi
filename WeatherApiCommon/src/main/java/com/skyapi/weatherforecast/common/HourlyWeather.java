package com.skyapi.weatherforecast.common;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "weather_hourly")
public class HourlyWeather {
    @EmbeddedId
    private HourlyWeatherId id = new HourlyWeatherId();

    private int temperature;
    private int precipitation;
    private String status;

    public HourlyWeatherId getId() {
        return id;
    }

    public void setId(HourlyWeatherId id) {
        this.id = id;
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

    public HourlyWeather temperature(int temp){
        setTemperature(temp);
        return this;
    }

    public HourlyWeather id(Location location, int hourOfDay){
        this.id.setLocation(location);
        this.id.setHourOfDay(hourOfDay);
        return this;
    }

    public HourlyWeather status(String status){
        setStatus(status);
        return this;
    }

    public HourlyWeather precipitation(int precipitation){
        this.setPrecipitation(precipitation);
        return this;
    }

    public HourlyWeather location(Location location){
        this.id.setLocation(location);
        return this;
    }

    public HourlyWeather hourOfDay(int hour){
        this.id.setHourOfDay(hour);
        return this;
    }

}
