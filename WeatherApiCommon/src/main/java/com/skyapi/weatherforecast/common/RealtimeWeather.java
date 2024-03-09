package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "realtime_weather")
public class RealtimeWeather {
    @Id
    @Column(name = "location_code")
    private String locationCode;
    private int temperature;
    private int humidity;
    private int precipitation;
    private int windSpeed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealtimeWeather that)) return false;
        return getLocationCode().equals(that.getLocationCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocationCode());
    }

    @Column(length = 50)
    private String status;
    @JsonProperty("last_updated")
    private Date lastUpdated;
    @OneToOne
    @JoinColumn(name = "location_code")
    @MapsId
    private Location location;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
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
}
