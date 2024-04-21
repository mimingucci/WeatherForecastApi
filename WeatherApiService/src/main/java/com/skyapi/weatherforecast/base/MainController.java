package com.skyapi.weatherforecast.base;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.location.LocationApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Weather Apis", description = "Root Directory")
@RestController
public class MainController {

	@Operation(summary = "Base URI", tags = "get")
	@GetMapping("/")
	public ResponseEntity<RootEntity> handleBaseURI() throws GeolocationException{
		return ResponseEntity.ok(createRootEntity());
	}
	
	private RootEntity createRootEntity() throws GeolocationException {
		RootEntity entity=new RootEntity();

		String locationsUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LocationApiController.class).listLocations()).toString();
		entity.setLocationsUrl(locationsUrl);
		
		String locationByCodeUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LocationApiController.class).getLocation(null)).toString();		
		entity.setLocationByCodeUrl(locationByCodeUrl);
		
		String realtimeWeatherByIpUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null)).toString();				
		entity.setRealtimeWeatherByIpUrl(realtimeWeatherByIpUrl);
		
		String realtimeWeatherByCodeUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(null)).toString();				
		entity.setRealtimeWeatherByCodeUrl(realtimeWeatherByCodeUrl);
		
		String hourlyWeatherByIpUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HourlyWeatherApiController.class).listHourlyForecaseByIPAddress(null)).toString();				
		entity.setHourlyForecastByIpUrl(hourlyWeatherByIpUrl);
		
		String hourlyWeatherByCodeUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(null, null)).toString();				
		entity.setHourlyForecastByCodeUrl(hourlyWeatherByCodeUrl);
		
		String dailyForecastByIpUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).toString();				
		entity.setDailyForecastByIpUrl(dailyForecastByIpUrl);
		
		String dailyForecastByCodeUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(null)).toString();				
		entity.setDailyForecastByCodeUrl(dailyForecastByCodeUrl);
		
		String fullWeatherByIpUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).toString();				
		entity.setFullWeatherByIpUrl(fullWeatherByIpUrl);
		
		String fullWeatherByCodeUrl=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(null)).toString();				
		entity.setFullWeatherByCodeUrl(fullWeatherByCodeUrl);
		return entity;
	}
}
