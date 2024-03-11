package com.skyapi.weatherforecast.full;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/full")
public class FullWeatherApiController {

	private GeolocationService locationService;
	private FullWeatherService weatherService;
	private ModelMapper mapper;
	
	public FullWeatherApiController(GeolocationService locationService, FullWeatherService weatherService,
			ModelMapper mapper) {
		super();
		this.locationService = locationService;
		this.weatherService = weatherService;
		this.mapper = mapper;
	}

	@GetMapping
	public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) throws GeolocationException{
		String ipAddress=CommonUtility.getIPAddress(request);
		Location locationFromIP=locationService.getLocation(ipAddress);
		Location locationFromDB=weatherService.getByLocation(locationFromIP);
		return ResponseEntity.ok(entity2DTO(locationFromDB));
	}


	private FullWeatherDTO entity2DTO(Location entity) {
		FullWeatherDTO dto=mapper.map(entity, FullWeatherDTO.class);
		dto.getRealtimeWeather().setLocation(null);
		return dto;
	}
	
	private Location dto2Entity(FullWeatherDTO dto) {
		return mapper.map(dto, Location.class);
	}
	
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable String locationCode){
		Location locationInDB=weatherService.get(locationCode);
		return ResponseEntity.ok(entity2DTO(locationInDB));
	}
	
	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateFullWeather(@PathVariable String locationCode, @RequestBody @Valid FullWeatherDTO dto) throws BadRequestException{
		if(dto.getListHourlyWeather().isEmpty()) {
			throw new BadRequestException("Hourly Weather data must not be empty");
		}
		if(dto.getListDailyWeather().isEmpty()) {
			throw new BadRequestException("Daily Weather data must not be empty");			
		}
		Location locationInRequest=dto2Entity(dto);
		Location updatedLocation=weatherService.update(locationCode, locationInRequest);
		return ResponseEntity.ok(entity2DTO(updatedLocation));
	}
}
