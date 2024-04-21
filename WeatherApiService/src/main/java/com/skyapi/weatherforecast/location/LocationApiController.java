package com.skyapi.weatherforecast.location;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.daily.DailyWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherApiController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Tag(name = "Location", description = "APIs for location management (cities and regions in the world). Used for administrative purposes.")
@RestController
@RequestMapping("/v1/locations")
@Validated
public class LocationApiController {

	private LocationService service;

	private ModelMapper mapper;
	
	private Map<String, String> propertyMap = Map.ofEntries(
			Map.entry("code", "code"), 
			Map.entry("city_name", "cityName"),
			Map.entry("region_name", "regionName"),
			Map.entry("country_code", "countryCode"),
			Map.entry("country_name", "countryName"),
			Map.entry("enabled", "enabled")
			);

	public LocationApiController(LocationService service, ModelMapper mapper) {
		super();
		this.service = service;
		this.mapper=mapper;
	}
	
	@Operation(summary = "Adds a new location to be managed for weather forecast", description = "Clients use this API to put a new location into the system. Location code must be manually specified. Country code and country name based on ISO 3166")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful add operation. Location added. Returns details of the newly added location. The response contains links which clients can use to get realtime, hourly, daily and full weather forecast of the location.", content = {
            @Content(schema = @Schema(implementation = LocationDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", description = "Failed add operation. Request body rejected due to some fields have invalid values", content = {
            @Content(schema = @Schema()) }) })
    @PostMapping
    public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO dto) throws GeolocationException{
    	Location addedLocation=service.save(dto2Entity(dto));
//    	if(addedLocation==null) {
//    	   return ResponseEntity.badRequest().body(addedLocation);
//    	}

    	URI uri=URI.create("/v1/locations/"+addedLocation.getCode());
    	return ResponseEntity.created(uri).body(addLinks2Item(entity2DTO(addedLocation)));
    }
    
    @Deprecated
    public ResponseEntity<?> listLocations(){
    	List<Location> locations=service.list();
    	if(locations.isEmpty()) {
    		return ResponseEntity.noContent().build();
    	}
    	
    	return ResponseEntity.ok(listEntity2ListDTO(locations));
    }
    
    @Operation(summary = "Returns a list of managed locations - available for weather forecast", description = "Clients use this API to get a list of managed locations. Each location is uniquely identified by location code, for example 'LACA_US' represents Los Angeles city in California state, in the United States of America (US)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "successful operation. There are managed locations available.\r\n"
        		+ "The response contains locations listed in the specified page number, or in the first page if no page parameter specified.\r\n"
        		+ "The pagination details are described in the object named 'page'.\r\n"
        		+ "The '_links' object contains links which clients can use to retrieve data in the first, next, previous or last page.", content = {
            @Content(schema = @Schema(implementation = LocationDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "204", description = "	\r\n"
        		+ "No managed locations available", content = {
            @Content(schema = @Schema()) }) })
    @GetMapping
    public ResponseEntity<?> listLocations(@RequestParam(value = "page", required = false, defaultValue = "1") @Min(value = 1)Integer pageNum, 
    		@RequestParam(value = "size", required = false, defaultValue = "5") @Min(value = 5) @Max(value = 20) Integer pageSize, 
    		@RequestParam(value = "sort", required = false, defaultValue = "code") String sortField, 
            @RequestParam(value = "enabled", required = false, defaultValue = "") String enabled,
			
			@RequestParam(value = "region_name", required = false, defaultValue = "") String regionName,
			
			@RequestParam(value = "country_code", required = false, defaultValue = "") String countryCode) throws BadRequestException, GeolocationException{
    	sortField = validateSortField(sortField);
    	Map<String, Object> filterFields = getFilterFields(enabled, regionName, countryCode);
    	Page<Location> page = service.listByPage(pageNum - 1, pageSize, sortField, filterFields);
    	List<Location> locations = page.getContent();
    	if (locations.isEmpty()) {
    		return ResponseEntity.noContent().build();
    	}
    	return ResponseEntity.ok(addPageMetadataAndLinks2Collection(listEntity2ListDTO(locations), page, sortField, enabled, regionName, countryCode));
    }
    
    private CollectionModel<LocationDTO> addPageMetadataAndLinks2Collection(
			List<LocationDTO> listDTO, Page<Location> pageInfo, String sortField,
			String enabled, String regionName, String countryCode) throws BadRequestException, GeolocationException {
		
		String actualEnabled = "".equals(enabled) ? null : enabled;
		String actualRegionName = "".equals(regionName) ? null : regionName;
		String actualCountryCode = "".equals(countryCode) ? null : countryCode;
		
				 
		// add self link to each individual item
		for (LocationDTO dto : listDTO) {
			dto.add(linkTo(methodOn(LocationApiController.class).getLocation(dto.getCode())).withSelfRel());
		}
		
		int pageSize = pageInfo.getSize();
		int pageNum = pageInfo.getNumber() + 1;
		long totalElements = pageInfo.getTotalElements();
		int totalPages = pageInfo.getTotalPages();
		
		PageMetadata pageMetadata = new PageMetadata(pageSize, pageNum, totalElements);
		
		CollectionModel<LocationDTO> collectionModel = PagedModel.of(listDTO, pageMetadata);
		
		// add self link to collection
		collectionModel.add(linkTo(methodOn(LocationApiController.class)
								.listLocations(pageNum, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode))
									.withSelfRel());
		
		if (pageNum > 1) {
			// add link to first page if the current page is not the first one
			collectionModel.add(
					linkTo(methodOn(LocationApiController.class)
							.listLocations(1, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode))
								.withRel(IanaLinkRelations.FIRST));
			
			// add link to the previous page if the current page is not the first one
			collectionModel.add(
					linkTo(methodOn(LocationApiController.class)
							.listLocations(pageNum - 1, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode))
								.withRel(IanaLinkRelations.PREV));			
		}	
		
		if (pageNum < totalPages) {
			// add link to next page if the current page is not the last one
			collectionModel.add(
					linkTo(methodOn(LocationApiController.class)
							.listLocations(pageNum + 1, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode))
								.withRel(IanaLinkRelations.NEXT));			
			
			// add link to last page if the current page is not the last one
			collectionModel.add(
					linkTo(methodOn(LocationApiController.class)
							.listLocations(totalPages, pageSize, sortField, actualEnabled, actualRegionName, actualCountryCode))
								.withRel(IanaLinkRelations.LAST));					
		}
		
		
		return collectionModel;
		
	}
    
    @Operation(summary = "Returns details of a location", description = "Clients use this API to find an existing location in the database by a specific code")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful retrieval operation. Location found. The response contains links which clients can use to get realtime, hourly, daily and full weather forecast of the location.", content = {
            @Content(schema = @Schema(implementation = LocationDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "404", description = "No location found with the given code.", content = {
            @Content(schema = @Schema()) }) })
    @GetMapping("/{code}")
    public ResponseEntity<?> getLocation(@PathVariable("code") String code) throws GeolocationException{
    	Location location=service.get(code);
    	return ResponseEntity.ok(addLinks2Item(entity2DTO(location)));
    }
    
    @Operation(summary = "Updates an existing location", description = "Clients use this API to modify information of a specific location. Note that location code cannot be changed.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful update operation. Location updated. Returns details of the recently updated location. The response contains links which clients can use to get realtime, hourly, daily and full weather forecast of the location.", content = {
            @Content(schema = @Schema(implementation = LocationDTO.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", description = "Failed update operation. Request body rejected due to some fields have invalid values", content = {
            @Content(schema = @Schema()) }),
    @ApiResponse(responseCode = "404", description = "No location found with the given code.", content = {
            @Content(schema = @Schema()) }) })
    @PutMapping
    public ResponseEntity<?> updateLocation(@RequestBody @Valid Location location) throws GeolocationException{

			Location updatedLocation= service.update(location);
			return ResponseEntity.ok(addLinks2Item(entity2DTO(updatedLocation)));

    }
    
    @Operation(summary = "Removes an existing location found by a specific code", description = "Clients use this API to delete a specific location from database")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successful delete operation. Location removed.", content = {
            @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "404", description = "No location found with the given code.", content = {
            @Content(schema = @Schema()) }) })
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
	
	private Map<String, Object> getFilterFields(String enabled, String regionName, String countryCode) {
		Map<String, Object> filterFields = new HashMap<>();
		
		if (!"".equals(enabled)) {
			filterFields.put("enabled", Boolean.parseBoolean(enabled));
		}
		
		if (!"".equals(regionName)) {
			filterFields.put("regionName", regionName);
		}		
		
		if (!"".equals(countryCode)) {
			filterFields.put("countryCode", countryCode);
		}
		return filterFields;
	}
	
	private String validateSortField(String sortField) throws BadRequestException {
		String translatedSortField = sortField;
		
		String[] sortFields = sortField.split(",");
		
		
		if (sortFields.length > 1) { // sorted by multiple fields
			
			for (int i = 0; i < sortFields.length; i++) {
				String actualFieldName = sortFields[i].replace("-", "");
				
				if (!propertyMap.containsKey(actualFieldName)) {
					throw new BadRequestException("invalid sort field: " + actualFieldName);
				}
				
				translatedSortField = translatedSortField.replace(actualFieldName, propertyMap.get(actualFieldName));
			}
			
		} else { // sorted by a single field
			String actualFieldName = sortField.replace("-", "");
			if (!propertyMap.containsKey(actualFieldName)) {
				throw new BadRequestException("invalid sort field: " + actualFieldName);
			}
			
			translatedSortField = translatedSortField.replace(actualFieldName, propertyMap.get(actualFieldName));
		}
		return translatedSortField;
	}
	
    private LocationDTO addLinks2Item(LocationDTO dto) throws GeolocationException {
		
		dto.add(linkTo(
				methodOn(LocationApiController.class).getLocation(dto.getCode()))
					.withSelfRel());	
		
		dto.add(linkTo(
				methodOn(RealtimeWeatherApiController.class).getRealtimeWeatherByLocationCode(dto.getCode()))
					.withRel("realtime_weather"));	
		
		dto.add(linkTo(
				methodOn(HourlyWeatherApiController.class).listHourlyForecastByLocationCode(dto.getCode(), null))
					.withRel("hourly_forecast"));
		
		dto.add(linkTo(
				methodOn(DailyWeatherApiController.class).listDailyForecastByLocationCode(dto.getCode()))
					.withRel("daily_forecast"));
		
		dto.add(linkTo(
				methodOn(FullWeatherApiController.class).getFullWeatherByLocationCode(dto.getCode()))
					.withRel("full_forecast"));		
		
		return dto;
	}
}
