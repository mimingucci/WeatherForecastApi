package com.skyapi.weatherforecast.location;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	
	@Deprecated
	public List<Location> list(){
		return repo.findUntrashed();
	}
	
	@Deprecated
	public Page<Location> listByPage(int pageNum, int pageSize, String sortField) {
		Sort sort = Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		return repo.findUntrashed(pageable);
	}
	
	public Page<Location> listByPage(int pageNum, int pageSize, String sortOption, Map<String, Object> filterFields) {
		Sort sort = createMultipleSorts(sortOption);
		
		Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
		
		return repo.listWithFilter(pageable, filterFields);
	}
	
	private Sort createMultipleSorts(String sortOption) {
		String[] sortFields = sortOption.split(",");
		
		Sort sort = null;
		
		if (sortFields.length > 1) { // sorted by multiple fields
			
			sort = createSingleSort(sortFields[0]);
			
			for (int i = 1; i < sortFields.length; i++) {
				
				sort = sort.and(createSingleSort(sortFields[i]));
			}
			
		} else { // sorted by a single field
			sort = createSingleSort(sortOption);
		}
		return sort;
	}	
	
	private Sort createSingleSort(String fieldName) {
		String actualFieldName = fieldName.replace("-", "");
		return fieldName.startsWith("-")
					? Sort.by(actualFieldName).descending() : Sort.by(actualFieldName).ascending();		
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
