package com.skyapi.weatherforecast.full;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;
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
}
