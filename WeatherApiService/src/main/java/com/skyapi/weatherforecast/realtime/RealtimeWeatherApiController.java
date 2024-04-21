package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "Realtime Weather", description = "APIs for accessing and updating realtime weather data")
@RestController
@RequestMapping( path = "/v1/realtime", produces = "application/json")
public class RealtimeWeatherApiController {
    public static final Logger LOGGER= LoggerFactory.getLogger(RealtimeWeatherApiController.class);
    private RealtimeWeatherService realtimeWeatherService;
    private GeolocationService geolocationService;

    private ModelMapper mapper;

    public RealtimeWeatherApiController(RealtimeWeatherService realtimeWeatherService, GeolocationService geolocationService, ModelMapper mapper) {
        this.realtimeWeatherService = realtimeWeatherService;
        this.geolocationService = geolocationService;
        this.mapper = mapper;
    }

    @Operation(summary = "Get Realtime Weather By Ip Address", description = "Returns the current weather information of the location based on client's IP address")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful retrieval operation. Realtime data available for the client's location. The response contains links which clients can use to get hourly, daily and full weather forecast of the same location.", content = {
            @Content(schema = @Schema(implementation = RealtimeWeatherDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", description = "bad request. Could not determine client's IP address", content = {
            @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "404", description = "No managed location found for the client's IP address", content = { @Content(schema = @Schema()) }) })
    @GetMapping
    public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request){
        try {
        	String ipAddress= CommonUtility.getIPAddress(request);
            Location location=geolocationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather=realtimeWeatherService.findByLocation(location);
            RealtimeWeatherDTO dto= mapper.map(realtimeWeather, RealtimeWeatherDTO.class);
            return ResponseEntity.ok(addLinksByIP(dto));
        } catch (GeolocationException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Returns the current weather information of a specific location identified by the given code", description = "Clients use this API to get realtime weather data of a specific location by the given code")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful retrieval operation. Realtime data available for the given location. The response contains links which clients can use to get hourly, daily and full weather forecast of the same location.", content = {
            @Content(schema = @Schema(implementation = RealtimeWeatherDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "404", description = "No managed location found for the given code", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable(name = "locationCode") String locationCode) throws GeolocationException{

            RealtimeWeather realtime=realtimeWeatherService.getByLocationCode(locationCode);
            RealtimeWeatherDTO dto=mapper.map(realtime, RealtimeWeatherDTO.class);
            return ResponseEntity.ok(addLinksByLocation(dto, locationCode));

    }

    @Operation(summary = "Updates realtime weather data based on location code", description = "Clients use this API to update current weather information for a specific location")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful update operation. Realtime weather data updated successfully. The response contains links which clients can use to get hourly, daily and full weather forecast of the same location.", content = {
            @Content(schema = @Schema(implementation = RealtimeWeatherDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "404", description = "No managed location found for the given code", content = { @Content(schema = @Schema()) }) })
    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealtimeWeather(@PathVariable("locationCode") String locationCode, @RequestBody @Valid RealtimeWeatherDTO dto) throws LocationNotFoundException, GeolocationException {
            RealtimeWeather realtimeWeather=dto2Entity(dto);
    	    realtimeWeather.setLocationCode(locationCode);

            RealtimeWeather updatedRealtimeWeather=realtimeWeatherService.update(locationCode, realtimeWeather);
            RealtimeWeatherDTO updatedDto=entity2DTO(updatedRealtimeWeather);
            return ResponseEntity.ok(addLinksByLocation(updatedDto, locationCode));

    }
    
    private RealtimeWeatherDTO entity2DTO (RealtimeWeather realtime) {
    	return mapper.map(realtime, RealtimeWeatherDTO.class);
    }
    
    private RealtimeWeather dto2Entity(RealtimeWeatherDTO dto) {
    	return mapper.map(dto, RealtimeWeather.class);
    }
    
    private RealtimeWeatherDTO addLinksByIP(RealtimeWeatherDTO dto) throws GeolocationException {
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null)).withSelfRel());
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HourlyWeatherApiController.class).listHourlyForecaseByIPAddress(null)).withRel("hourly_forecast"));
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).withRel("daily_forecast"));
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).withRel("full_forecast"));    	
    	return dto;
    }
    
    private RealtimeWeatherDTO addLinksByLocation(RealtimeWeatherDTO dto, String locationCode) throws GeolocationException {
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(locationCode)).withSelfRel());
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null)).withRel("hourly_forecast"));
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode)).withRel("daily_forecast"));
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode)).withRel("full_forecast"));    	
    	return dto;
    }
}
