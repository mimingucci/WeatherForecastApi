package com.skyapi.weatherforecast.daily;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherListDTO;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Daily Forecast", description = "APIs for accessing and updating daily weather forecast")
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
	
	@Operation(summary = "Returns daily weather forecast information for the location based on client's IP address", description = "Clients use this API to get forecast about weather in the upcoming days. Location is determined automatically based on client's IP address.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful retrieval operation. A JSON object representing daily forecast data available for the client's location. The response contains links which clients can use to get realtime, hourly and full weather forecast information.", content = {
            @Content(schema = @Schema(implementation = DailyWeatherListDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "204", description = "No daily forecast data available", content = {
            @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "400", description = "bad request. Could not determine client's IP address", content = {
                @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "404", description = "No managed location found for the client's IP address", content = { @Content(schema = @Schema()) }) })  
	@GetMapping
	public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) throws GeolocationException{
		String ipAddress=CommonUtility.getIPAddress(request);
		try {
			Location locationFromIP=geolocationService.getLocation(ipAddress);
			List<DailyWeather> dailyForecast=dailyWeatherService.getByLocation(locationFromIP);
			if(dailyForecast.isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(addLinksByIP(listEntity2DTO(dailyForecast)));
		} catch (GeolocationException e) {
			throw new GeolocationException(ipAddress);
		}
		
	}
	
	@Operation(summary = "Returns daily weather forecast information for a specific location code", description = "Clients use this API to get forecast about weather in the upcoming days, for the given location code")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful retrieval operation. A JSON object representing daily forecast data available for the given location code. The response contains links which clients can use to get realtime, hourly and full weather forecast information.", content = {
            @Content(schema = @Schema(implementation = DailyWeatherListDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "204", description = "No daily forecast data available", content = {
            @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "404", description = "No managed location found for the given location code", content = { @Content(schema = @Schema()) }) }) 	
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable("locationCode") String locationCode){
		List<DailyWeather> dailyForecast=dailyWeatherService.getByLocationCode(locationCode);
		if(dailyForecast.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(listEntity2DTO(dailyForecast));
	}
	
	@Operation(summary = "Update daily weather forecast information for a location specified by the given location code.", description = "Clients use this API to update data about weather forecast information in the upcoming days, based on given location code. Successful update operation will replace all existing data.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful update operation. Daily weather forecast data updated successfully - all previous data is replaced. The response contains links which clients can use to get realtime, hourly and full weather forecast information.", content = {
            @Content(schema = @Schema(implementation = DailyWeatherListDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", description = "Bad request. Request body contains empty array (no data) or there are some invalid values of fields in daily forecast information.", content = {
                @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "404", description = "no managed location found for the given code", content = { @Content(schema = @Schema()) }) }) 
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
	
	private EntityModel<DailyWeatherListDTO> addLinksByIP(DailyWeatherListDTO dto) throws GeolocationException{
		EntityModel<DailyWeatherListDTO> entityModel=EntityModel.of(dto);
		entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).withSelfRel());
    	entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HourlyWeatherApiController.class).listHourlyForecaseByIPAddress(null)).withRel("hourly_forecast"));
    	entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null)).withRel("realtime_forecast"));
    	entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).withRel("full_forecast"));    	
		return entityModel;
	}
	
	private EntityModel<DailyWeatherListDTO> addLinksByLocation(DailyWeatherListDTO dto, String locationCode) throws GeolocationException{
		EntityModel<DailyWeatherListDTO> entityModel=EntityModel.of(dto);
		entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode)).withSelfRel());
    	entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null)).withRel("hourly_forecast"));
    	entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(locationCode)).withRel("realtime_forecast"));
    	entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode)).withRel("full_forecast"));    	
		return entityModel;
	}
	
}
