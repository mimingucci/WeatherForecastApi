package com.skyapi.weatherforecast.full;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class FullWeatherService {

	private LocationRepository repo;

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
	
	public Location get(String locationCode) {
		Location location=repo.findByCode(locationCode);
		if(location==null) {
			throw new LocationNotFoundException(locationCode);
		}
		return location;
	}
	
	public Location update(String locationCode, Location locationInRequest) {
		Location locationInDB=repo.findByCode(locationCode);
		if(locationInDB==null) {
			throw new LocationNotFoundException(locationCode);
		}
		RealtimeWeather realtimeWeather=locationInRequest.getRealtimeWeather();
		realtimeWeather.setLocation(locationInDB);
		List<DailyWeather> listDailyWeather=locationInRequest.getListDailyWeather();
		listDailyWeather.forEach(dw->dw.getId().setLocation(locationInDB));
		List<HourlyWeather> listHourlyWeather=locationInRequest.getListHourlyWeather();
		listHourlyWeather.forEach(hw->hw.getId().setLocation(locationInDB));
		locationInRequest.setCode(locationInDB.getCode());
		locationInRequest.setCountryCode(locationInDB.getCountryCode());
		locationInRequest.setCityName(locationInDB.getCityName());
		locationInRequest.setCountryName(locationInDB.getCountryName());
		locationInRequest.setEnabled(locationInDB.isEnabled());
		locationInRequest.setTrashed(locationInDB.isTrashed());
		locationInRequest.setRegionName(locationInDB.getRegionName());
		
		return repo.save(locationInRequest);
	}
}
