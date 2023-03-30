package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
public class HourlyWeatherApiController {

    private HourlyWeatherService hourlyWeatherService;
    private GeolocationService geolocationService;

    private ModelMapper mapper;

    public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService, GeolocationService geolocationService, ModelMapper mapper) {
        this.hourlyWeatherService = hourlyWeatherService;
        this.geolocationService = geolocationService;
        this.mapper=mapper;
    }

    @GetMapping
    public ResponseEntity<?> listHourlyForecaseByIPAddress(HttpServletRequest request){
        String ipLocation= CommonUtility.getIPAddress(request);
        int currentHour=Integer.parseInt(request.getHeader("X-Current-Hour"));
        try {
            Location locationByIP=geolocationService.getLocation(ipLocation);
            List<HourlyWeather> hourlyForecast=hourlyWeatherService.getByLocation(locationByIP, currentHour);
            if(hourlyForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(hourlyForecast);
        } catch (GeolocationException e) {
            return ResponseEntity.badRequest().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByLocationCode(@PathVariable("locationCode") String locationCode, HttpServletRequest request){
        try {
            int currentHour=Integer.parseInt(request.getHeader("X-Current-Hour"));
            List<HourlyWeather> hourlyForecast=hourlyWeatherService.getByLocationCode(locationCode, currentHour);
            if(hourlyForecast.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));
        } catch (LocationNotFoundException e) {
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

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode, @RequestBody List<HourlyWeatherDTO> listDTO){
        if(listDTO.isEmpty()){
            return new ResponseEntity<>("List hourly weather data can not be empty", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.accepted().build();
    }


}
