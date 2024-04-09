package com.skyapi.weatherforecast.daily;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/daily")
public class DailyWeatherApiController {

	@Autowired
	private DailyWeatherService dailyWeatherService;
	private GeolocationService geolocationService;
	private ModelMapper modelMapper;
	public DailyWeatherApiController(DailyWeatherService dailyWeatherService, GeolocationService geolocationService,
			ModelMapper modelMapper) {
		super();
		this.dailyWeatherService = dailyWeatherService;
		this.geolocationService = geolocationService;
		this.modelMapper = modelMapper;
	}
	
	@GetMapping
	public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) throws GeolocationException{
		String ipAddress=CommonUtility.getIPAddress(request);
		try {
			Location locationFromIP=geolocationService.getLocation(ipAddress);
			List<DailyWeather> dailyForecast=dailyWeatherService.getByLocation(locationFromIP);
			if(dailyForecast.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(listEntity2DTO(dailyForecast));
		} catch (GeolocationException e) {
			throw new GeolocationException(ipAddress);
		}
		
	}
	
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable("locationCode") String locationCode){
		List<DailyWeather> dailyForecast=dailyWeatherService.getByLocationCode(locationCode);
		if(dailyForecast.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listEntity2DTO(dailyForecast));
	}

	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateDailyForecast(@PathVariable("locationCode") String code, @RequestBody @Valid List<DailyWeatherDTO> listDTO) throws BadRequestException{
		if(listDTO.isEmpty()) {
			throw new BadRequestException("Daily forecast data cannot be empty");
		}
		List<DailyWeather> dailyWeather=listDTO2ListEntity(listDTO);
		List<DailyWeather> updatedForecast=dailyWeatherService.updateByLocationCode(code, dailyWeather);
		return ResponseEntity.ok(listEntity2DTO(updatedForecast));
	}
	
	private List<DailyWeather> listDTO2ListEntity(@Valid List<DailyWeatherDTO> listDTO) {
		List<DailyWeather> dailyWeather=new ArrayList<>();
		listDTO.forEach(dailyDTO->{
			dailyWeather.add(modelMapper.map(dailyDTO, DailyWeather.class));
		});
		return dailyWeather;
	}

	private DailyWeatherListDTO listEntity2DTO(List<DailyWeather> dailyForecast) {
		Location location=dailyForecast.get(0).getId().getLocation();
		DailyWeatherListDTO listDTO=new DailyWeatherListDTO();
		listDTO.setLocation(location.toString());
		dailyForecast.forEach(dailyWeather->{
			listDTO.addDailyWeatherDTO(modelMapper.map(dailyWeather, DailyWeatherDTO.class));
		});
		return listDTO;
	}
}
