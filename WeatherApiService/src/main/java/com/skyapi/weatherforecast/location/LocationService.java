package com.skyapi.weatherforecast.location;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;

@Service
public class LocationService {

	private LocationRepository repo;

	public LocationService(LocationRepository repo) {
		super();
		this.repo = repo;
	}
	
	public Location save(Location location) {
		return repo.save(location);
	}
}
