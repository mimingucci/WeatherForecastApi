package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HourlyWeatherRepository extends CrudRepository<HourlyWeather, HourlyWeatherId> {

    @Query("""
           select h from HourlyWeather h where h.id.location.code = ?1 and 
           h.id.hourOfDay > ?2 and h.id.location.trashed = false 
           """)
    public List<HourlyWeather> findByLocationCode(String locationCode, int currentHour);
}
