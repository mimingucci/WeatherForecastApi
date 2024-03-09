package com.skyapi.weatherforecast.full;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;

import jakarta.servlet.http.HttpServletRequest;

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
}
