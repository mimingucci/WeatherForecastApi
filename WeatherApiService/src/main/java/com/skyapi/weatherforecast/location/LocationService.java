package com.skyapi.weatherforecast.location;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LocationService {

	private LocationRepository repo;

	public LocationService(LocationRepository repo) {
		super();
		this.repo = repo;
	}
	
	public Location save(Location location) {
		return repo.save(location);
	}
	
	public List<Location> list(){
		return repo.findUntrashed();
	}
	
	public Location get(String code) {
		return repo.findByCode(code);
	}
	
	public Location update(Location locationInRequest) throws LocationNotFoundException {
		Location locationInDb=repo.findByCode(locationInRequest.getCode());
		if(locationInDb==null) {
			throw new LocationNotFoundException("Could not found location with given code: "+locationInRequest.getCode());
		}
		
		locationInDb.setCityName(locationInRequest.getCityName());
		locationInDb.setCountryCode(locationInRequest.getCountryCode());
		locationInDb.setCountryName(locationInRequest.getCountryName());
		locationInDb.setRegionName(locationInRequest.getRegionName());
		locationInDb.setEnabled(locationInRequest.isEnabled());
		
		return repo.save(locationInDb);
	}
	
	public void delete(String code) throws LocationNotFoundException {
		Location location=repo.findByCode(code);
		if(location==null) {
			throw new LocationNotFoundException("Cound not found location with given code: "+code);
		}
		repo.trashByCode(code);
	}
}
