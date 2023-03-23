package com.skyapi.weatherforecast.hourly;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/hourly")
public class HourlyWeatherApiController {

    private HourlyWeatherService hourlyWeatherService;
    private GeolocationService geolocationService;

    public HourlyWeatherApiController(HourlyWeatherService hourlyWeatherService, GeolocationService geolocationService) {
        this.hourlyWeatherService = hourlyWeatherService;
        this.geolocationService = geolocationService;
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
}
