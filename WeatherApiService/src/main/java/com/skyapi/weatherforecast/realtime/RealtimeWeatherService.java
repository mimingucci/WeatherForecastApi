package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RealtimeWeatherService {
    private RealtimeWeatherRepository realtimeWeatherRepository;

    private LocationRepository locationRepository;

    public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepository, LocationRepository locationRepository) {
        this.realtimeWeatherRepository = realtimeWeatherRepository;
        this.locationRepository=locationRepository;
    }

    public RealtimeWeather findByLocation(Location location) throws LocationNotFoundException {
        RealtimeWeather realtimeWeather=realtimeWeatherRepository.findByCountryCodeAndCity(location.getCountryCode(), location.getCityName());
        if(realtimeWeather==null){
            throw new LocationNotFoundException("Not found location: "+location);
        }
        return realtimeWeather;
    }

    public RealtimeWeather getByLocationCode(String locationCode) throws LocationNotFoundException {
        RealtimeWeather realtime=realtimeWeatherRepository.findByLocationCode(locationCode);
        if(realtime==null){
            throw new LocationNotFoundException("Could not found location with code: "+locationCode);
        }
        return realtime;
    }
    
    public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
        return getByLocationCode(location.getCode());
    }

    public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather) throws LocationNotFoundException {
       Location location=locationRepository.findByCode(locationCode);
       if(location==null){
           throw new LocationNotFoundException("Could not found location with code: "+locationCode);
       }
       realtimeWeather.setLocation(location);
       realtimeWeather.setLastUpdated(new Date());
       if(location.getRealtimeWeather()==null){
           location.setRealtimeWeather(realtimeWeather);
           Location updatedLocation=locationRepository.save(location);
           return updatedLocation.getRealtimeWeather();
       }
       return realtimeWeatherRepository.save(realtimeWeather);
    }
}
