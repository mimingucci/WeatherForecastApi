package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HourlyWeatherService {

    private HourlyWeatherRepository hourlyWeatherRepo;
    private LocationRepository locationRepo;

    public HourlyWeatherService(LocationRepository locationRepo) {
        this.locationRepo = locationRepo;
    }

    public List<HourlyWeather> getByLocation(Location location, int currentHour) throws LocationNotFoundException {
        Location locationInDB=locationRepo.findByCountryCodeAndCityName(location.getCountryCode(), location.getCityName());
        if(locationInDB==null){
            throw new LocationNotFoundException("Could not found with given country code and city name");
        }
        return hourlyWeatherRepo.findByLocationCode(locationInDB.getCode(), currentHour);
    }

    public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour) throws LocationNotFoundException {
        Location locationInDB= locationRepo.findByCode(locationCode);
        if(locationInDB==null){
            throw new LocationNotFoundException("Could not found with given location code");
        }
        return hourlyWeatherRepo.findByLocationCode(locationCode, currentHour);
    }
}
