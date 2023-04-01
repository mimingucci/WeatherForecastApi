package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HourlyWeatherService {

    private HourlyWeatherRepository hourlyWeatherRepo;
    private LocationRepository locationRepo;

    public HourlyWeatherService(LocationRepository locationRepo, HourlyWeatherRepository hourlyWeatherRepo) {
        this.locationRepo = locationRepo;
        this.hourlyWeatherRepo=hourlyWeatherRepo;
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

    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyForecastInRequest) throws LocationNotFoundException {
        Location location=locationRepo.findByCode(locationCode);
        if(location==null){
            throw new LocationNotFoundException("Could not found with given location code");
        }
        hourlyForecastInRequest.forEach(item->item.getId().setLocation(location));
        List<HourlyWeather> listHourlyWeatherInDB=location.getListHourlyWeather();
        List<HourlyWeather> listHourlyWeatherToBeRemoved=new ArrayList<>();
        for(HourlyWeather item : listHourlyWeatherInDB){
            if(!hourlyForecastInRequest.contains(item)){
                listHourlyWeatherToBeRemoved.add(item.getShallowCopy());
            }
        }
        for(HourlyWeather item : listHourlyWeatherToBeRemoved){
            listHourlyWeatherInDB.remove(item);
        }
        return (List<HourlyWeather>) hourlyWeatherRepo.saveAll(hourlyForecastInRequest);
    }
}
