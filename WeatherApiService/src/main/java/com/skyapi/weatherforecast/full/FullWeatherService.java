package com.skyapi.weatherforecast.full;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.AbstractLocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FullWeatherService extends AbstractLocationService{


	public FullWeatherService(LocationRepository repo) {
		super();
		this.repo = repo;
	}
	
	public Location getByLocation(Location locationFromIP) {
		Location locationInDB=repo.findByCountryCodeAndCityName(locationFromIP.getCountryCode(), locationFromIP.getCityName());
		if(locationInDB==null) {
			throw new LocationNotFoundException(locationFromIP.getCountryCode(), locationFromIP.getCityName());
		}
		return locationInDB;
	}
	
	
	public Location update(String locationCode, Location locationInRequest) {
		Location locationInDB=repo.findByCode(locationCode);
		if(locationInDB==null) {
			throw new LocationNotFoundException(locationCode);
		}
		setLocationForWeatherData(locationInRequest, locationInDB);
		saveRealtimeWeatherIfNotExistBefore(locationInRequest, locationInDB);
		locationInRequest.copyAllFieldsFrom(locationInDB);
		
		return repo.save(locationInRequest);
	}

	private void saveRealtimeWeatherIfNotExistBefore(Location locationInRequest, Location locationInDB) {
		if(locationInDB.getRealtimeWeather()==null) {
			locationInDB.setRealtimeWeather(locationInRequest.getRealtimeWeather());
			repo.save(locationInDB);
		}
	}

	private void setLocationForWeatherData(Location locationInRequest, Location locationInDB) {
		RealtimeWeather realtimeWeather=locationInRequest.getRealtimeWeather();
		realtimeWeather.setLocation(locationInDB);
		realtimeWeather.setLastUpdated(new Date());
		
		List<DailyWeather> listDailyWeather=locationInRequest.getListDailyWeather();
		listDailyWeather.forEach(dw->dw.getId().setLocation(locationInDB));
		List<HourlyWeather> listHourlyWeather=locationInRequest.getListHourlyWeather();
		listHourlyWeather.forEach(hw->hw.getId().setLocation(locationInDB));
	}
}
