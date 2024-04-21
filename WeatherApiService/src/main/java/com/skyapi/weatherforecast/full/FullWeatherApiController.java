package com.skyapi.weatherforecast.full;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/*
@Operation(summary = "", description = "")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "", content = {
        @Content(schema = @Schema(implementation = HourlyWeatherListDTO.class), mediaType = "application/json") }),
    @ApiResponse(responseCode = "204", description = "", content = {
        @Content(schema = @Schema()) }),
    @ApiResponse(responseCode = "400", description = "", content = {
            @Content(schema = @Schema()) }),
    @ApiResponse(responseCode = "404", description = "", content = { @Content(schema = @Schema()) }) })  
*/

@Tag(name = "Full Weather Forecast", description = "APIs for accessing and updating full weather information (including realtime, hourly and daily forecast)")
@RestController
@RequestMapping("/v1/full")
public class FullWeatherApiController {

	private GeolocationService locationService;
	private FullWeatherService weatherService;
	private ModelMapper mapper;
	private FullWeatherModelAssembler modelAssembler;

	public FullWeatherApiController(GeolocationService locationService, FullWeatherService weatherService,
			ModelMapper mapper, FullWeatherModelAssembler modelAssembler) {
		super();
		this.locationService = locationService;
		this.weatherService = weatherService;
		this.mapper = mapper;
		this.modelAssembler = modelAssembler;
	}

	@Operation(summary = "Returns full weather forecast information based on client's IP address", description = "Clients use this API to get full weather information about weather of a location determined based on client's IP address")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "successful retrieval operation. A JSON object represents full weather forecast information, which is aggregation of realtime weather, hourly forecast and daily forecast", content = {
	        @Content(schema = @Schema(implementation = FullWeatherDTO.class), mediaType = "application/json") }),
	    @ApiResponse(responseCode = "400", description = "bad request. Could not determine client's IP address", content = {
	            @Content(schema = @Schema()) }),
	    @ApiResponse(responseCode = "404", description = "No managed location found for the client's IP address", content = { @Content(schema = @Schema()) }) }) 
	@GetMapping
	public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) throws GeolocationException{
		String ipAddress=CommonUtility.getIPAddress(request);
		Location locationFromIP=locationService.getLocation(ipAddress);
		Location locationFromDB=weatherService.getByLocation(locationFromIP);
		return ResponseEntity.ok(modelAssembler.toModel(entity2DTO(locationFromDB)));
	}

	private FullWeatherDTO entity2DTO(Location entity) {
		FullWeatherDTO dto=mapper.map(entity, FullWeatherDTO.class);
		dto.getRealtimeWeather().setLocation(null);
		return dto;
	}
	
	private Location dto2Entity(FullWeatherDTO dto) {
		return mapper.map(dto, Location.class);
	}
	
	@Operation(summary = "Returns full weather forecast information for the location specified by the given code", description = "Clients use this API to get weather information including realtime, hourly and daily forecast")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "successful retrieval operation. A JSON object represents full weather forecast information, which is aggregation of realtime weather, hourly forecast and daily forecast.", content = {
	        @Content(schema = @Schema(implementation = FullWeatherDTO.class), mediaType = "application/json") }),
	    @ApiResponse(responseCode = "404", description = "No managed location found for the given location code", content = { @Content(schema = @Schema()) }) }) 
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable String locationCode){
		Location locationInDB=weatherService.get(locationCode);
		return ResponseEntity.ok(entity2DTO(locationInDB));
	}
	
	@Operation(summary = "Updates full weather forecast information of the location specified by the given code", description = "Clients use this API to update realtime, hourly and daily forecast of a location at once (full weather information)")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "successful update operation. Full weather forecast data updated successfully", content = {
	        @Content(schema = @Schema(implementation = FullWeatherDTO.class), mediaType = "application/json") }),
	    @ApiResponse(responseCode = "400", description = "Bad request. Request body contains some invalid values of fields in realtime weather, hourly forecast or daily forecast", content = {
	            @Content(schema = @Schema()) }),
	    @ApiResponse(responseCode = "404", description = "no managed location found for the given code", content = { @Content(schema = @Schema()) }) }) 
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
	
	private EntityModel<FullWeatherDTO> addLinksByLocation(FullWeatherDTO dto, String locationCode){
		return EntityModel.of(dto)
				          .add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(locationCode)).withSelfRel());
	}
}
