package com.skyapi.weatherforecast.location;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.common.Location;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {

	private LocationService service;

	private ModelMapper mapper;

	public LocationApiController(LocationService service, ModelMapper mapper) {
		super();
		this.service = service;
		this.mapper=mapper;
	}
	
    @PostMapping
    public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO dto){
    	Location addedLocation=service.save(dto2Entity(dto));
//    	if(addedLocation==null) {
//    	   return ResponseEntity.badRequest().body(addedLocation);
//    	}

    	URI uri=URI.create("/v1/locations/"+addedLocation.getCode());
    	return ResponseEntity.created(uri).body(entity2DTO(addedLocation));
    }
    
    @GetMapping
    public ResponseEntity<?> listLocations(){
    	List<Location> locations=service.list();
    	if(locations.isEmpty()) {
    		return ResponseEntity.noContent().build();
    	}
    	
    	return ResponseEntity.ok(listEntity2ListDTO(locations));
    }
    
    @GetMapping("/{code}")
    public ResponseEntity<?> getLocation(@PathVariable("code") String code){
    	Location location=service.get(code);
    	return ResponseEntity.ok(entity2DTO(location));
    }
    
    @PutMapping
    public ResponseEntity<?> updateLocation(@RequestBody @Valid Location location){

			Location updatedLocation= service.update(location);
			return ResponseEntity.ok(entity2DTO(updatedLocation));

    }
    
    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteLocation(@PathVariable("code") String code){
			service.delete(code);
			return ResponseEntity.noContent().build();

    }

	private Location dto2Entity(LocationDTO dto){
		return mapper.map(dto, Location.class);
	}

	private LocationDTO entity2DTO(Location entity){
		return mapper.map(entity, LocationDTO.class);
	}

	private List<LocationDTO> listEntity2ListDTO(List<Location> locations){
		return locations.stream().map(entity->mapper.map(entity, LocationDTO.class)).collect(Collectors.toList());
	}
}
