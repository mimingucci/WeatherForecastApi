package com.skyapi.weatherforecast.hourly;

public class HourlyWeatherDTO {
    private int hourOfDay;
    private int temperature;
    private int precipitation;
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
}
