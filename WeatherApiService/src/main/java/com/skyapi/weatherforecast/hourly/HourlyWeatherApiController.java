package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherDTO;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Hourly Forecast", description = "APIs for accessing and updating hourly weather forecast")
@RestController
@RequestMapping("/v1/hourly")
@Validated
public class HourlyWeatherApiController {

    private HourlyWeatherService hourlyWeatherService;
    private GeolocationService geolocationService;

    private ModelMapper mapper;

    public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService, GeolocationService geolocationService, ModelMapper mapper) {
        this.hourlyWeatherService = hourlyWeatherService;
        this.geolocationService = geolocationService;
        this.mapper=mapper;
    }

    @Operation(summary = "Returns weather forecast for upcoming hours in the current day, based on client's IP address", description = "Clients use this API to get weather forecast for upcoming hours in the current day. Location is determined automatically based on client's IP address.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful retrieval operation. A JSON object representing hourly forecast information available for the client's location - only data for the next hours is returned (based on value of the header X-Current-Hour). The response contains links which clients can use to get realtime, daily and full weather information.", content = {
            @Content(schema = @Schema(implementation = HourlyWeatherListDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "204", description = "No hourly forecast data available", content = {
            @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "404", description = "No managed location found for the client's IP address", content = {
                @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "400", description = "Error during geolocation process or invalid value of request header X-Current-Hour", content = { @Content(schema = @Schema()) }) })
    @GetMapping
    public ResponseEntity<?> listHourlyForecaseByIPAddress(HttpServletRequest request){
        String ipLocation= CommonUtility.getIPAddress(request);
        try {
        	int currentHour=Integer.parseInt(request.getHeader("X-Current-Hour"));
            Location locationByIP=geolocationService.getLocation(ipLocation);
            List<HourlyWeather> hourlyForecast=hourlyWeatherService.getByLocation(locationByIP, currentHour);
            if(hourlyForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(addLinksByIP(listEntity2DTO(hourlyForecast)));
        } catch (NumberFormatException | GeolocationException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Returns hourly weather forecast information based on location code.", description = "Clients use this API to get weather forecast for upcomping hours in the current day, for the given location code. Only data for the next hours is returned (based on value of the header X-Current-Hour).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful retrieval operation. A JSON object representing hourly forecast data available for the given location code. The response contains links which clients can use to get realtime, daily and full weather information.", content = {
            @Content(schema = @Schema(implementation = HourlyWeatherListDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "204", description = "No hourly forecast data available", content = {
            @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "404", description = "No managed location found for the given location code", content = {
                @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "400", description = "Invalid value of request header X-Current-Hour", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable("locationCode") String locationCode, HttpServletRequest request){
        try {
            int currentHour=Integer.parseInt(request.getHeader("X-Current-Hour"));
            List<HourlyWeather> hourlyForecast=hourlyWeatherService.getByLocationCode(locationCode, currentHour);
            if(hourlyForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(addLinksByLocation(listEntity2DTO(hourlyForecast), locationCode));
        } catch (LocationNotFoundException | GeolocationException e) {
            return ResponseEntity.notFound().build();
        }catch (NumberFormatException ex){
            return ResponseEntity.badRequest().build();
        }
    }

    private HourlyWeatherListDTO listEntity2DTO(List<HourlyWeather> hourlyForecast){
        Location location=hourlyForecast.get(0).getId().getLocation();
        HourlyWeatherListDTO listDto=new HourlyWeatherListDTO();
        listDto.setLocation(location.toString());
        hourlyForecast.forEach(hourlyWeather->{
            HourlyWeatherDTO dto=mapper.map(hourlyWeather, HourlyWeatherDTO.class);
            listDto.addWeatherHourlyDTO(dto);
        });
        return listDto;
    }

    @Operation(summary = "Updates hourly weather forecast information for a specific location", description = "Clients use this API to update weather forecast for upcoming hours in the current day, for the given location code. Successful update operation will replace all existing data.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful update operation. Hourly weather forecast data updated successfully - all previous data is replaced. The response contains links which clients can use to get realtime, daily and full weather information.", content = {
            @Content(schema = @Schema(implementation = HourlyWeatherListDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "404", description = "no managed location found for the given code", content = {
                @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "400", description = "bad request. Request body contains empty array (no data) or there are some invalid values of fields in hourly forecast information.", content = { @Content(schema = @Schema()) }) })
    
    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode, @RequestBody @Valid List<HourlyWeatherDTO> listDTO) throws BadRequestException {
        if(listDTO.isEmpty()){
            throw new BadRequestException("List hourly weather data can not be empty");
        }
        listDTO.forEach(System.out::println);
        List<HourlyWeather> listHourlyWeather=listDTO2listEntity(listDTO);
        try {
            List<HourlyWeather> updatedHourlyWeather=hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);
            return ResponseEntity.ok(listEntity2DTO(updatedHourlyWeather));
        }catch (LocationNotFoundException ex){
            return ResponseEntity.notFound().build();
        }
    }

    private List<HourlyWeather> listDTO2listEntity(List<HourlyWeatherDTO> listDTO){
        List<HourlyWeather> listEntity=new ArrayList<>();
        listDTO.forEach(dto->{
            listEntity.add(mapper.map(dto, HourlyWeather.class));
        });

        return listEntity;
    }
    
    private HourlyWeatherListDTO addLinksByIP(HourlyWeatherListDTO dto) throws GeolocationException {
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HourlyWeatherApiController.class).listHourlyForecaseByIPAddress(null)).withSelfRel());
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByIPAddress(null)).withRel("realtime_weather"));
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DailyWeatherApiController.class).listDailyForecastByIPAddress(null)).withRel("daily_weather"));
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByIPAddress(null)).withRel("full_weather"));
    	return dto;
    }
    
    private HourlyWeatherListDTO addLinksByLocation(HourlyWeatherListDTO dto, String locationCode) throws GeolocationException {
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(locationCode, null)).withSelfRel());
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(locationCode)).withRel("realtime_weather"));
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(locationCode)).withRel("daily_weather"));
    	dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode)).withRel("full_weather"));
    	return dto;
    }

}
