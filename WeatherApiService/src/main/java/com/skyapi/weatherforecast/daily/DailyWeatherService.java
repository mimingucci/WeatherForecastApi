package com.skyapi.weatherforecast.daily;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class DailyWeatherService {
	private DailyWeatherRepository dailyWeatherRepository;
	
	private LocationRepository locationRepository;

	public DailyWeatherService(DailyWeatherRepository dailyWeatherRepository, LocationRepository locationRepository) {
		super();
		this.dailyWeatherRepository = dailyWeatherRepository;
		this.locationRepository = locationRepository;
	}
	
	public List<DailyWeather> getByLocation(Location location){
		String countryCode=location.getCountryCode();
		String cityName=location.getCityName();
		Location locationInDB=locationRepository.findByCountryCodeAndCityName(countryCode, cityName);
		if(locationInDB==null) {
			throw new LocationNotFoundException(countryCode, cityName);
		}
		return dailyWeatherRepository.findByLocationCode(locationInDB.getCode());
	}
	
	public List<DailyWeather> getByLocationCode(String locationCode){
		Location location=locationRepository.findByCode(locationCode);
		if(location==null) {
			throw new LocationNotFoundException(locationCode);
		}
		return dailyWeatherRepository.findByLocationCode(locationCode);
	}
	
	public List<DailyWeather> updateByLocationCode(String code, List<DailyWeather> dailyWeatherInRequest){
		Location location=locationRepository.findByCode(code);
		if(location==null) {
			throw new LocationNotFoundException(code);
		}
		for(DailyWeather daily : dailyWeatherInRequest) {
			daily.getId().setLocation(location);
		}
		List<DailyWeather> dailyWeatherInDB=location.getListDailyWeather();
		List<DailyWeather> dailyWeatherToBeRemoved=new ArrayList<>();
		for(DailyWeather forecast : dailyWeatherInDB) {
			if(!dailyWeatherInRequest.contains(forecast)) {
				dailyWeatherToBeRemoved.add(forecast.getShadowCopy());
			}
		}
		for(DailyWeather forecast : dailyWeatherToBeRemoved) {
			dailyWeatherInDB.remove(forecast);
		}
		return (List<DailyWeather>) dailyWeatherRepository.saveAll(dailyWeatherInRequest);
	}
}
