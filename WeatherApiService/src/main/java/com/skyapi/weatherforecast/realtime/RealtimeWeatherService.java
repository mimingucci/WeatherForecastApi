package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RealtimeWeatherService {
    private RealtimeWeatherRepository realtimeWeatherRepository;

    public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepository) {
        this.realtimeWeatherRepository = realtimeWeatherRepository;
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
}
