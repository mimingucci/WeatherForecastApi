package com.skyapi.weatherforecast.location;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.AbstractLocationService;
import com.skyapi.weatherforecast.common.Location;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LocationService extends AbstractLocationService{

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
	
	public Location update(Location locationInRequest)  {
		Location locationInDb=repo.findByCode(locationInRequest.getCode());
		if(locationInDb==null) {
			throw new LocationNotFoundException(locationInRequest.getCode());
		}
		
		locationInDb.copyFieldsFrom(locationInRequest);
		
		return repo.save(locationInDb);
	}
	
	public void delete(String code)  {
		Location location=repo.findByCode(code);
		if(location==null) {
			throw new LocationNotFoundException(code);
		}
		repo.trashByCode(code);
	}
}
