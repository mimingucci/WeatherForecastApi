package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request){
        String ipAddress= CommonUtility.getIPAddress(request);
        try {
            Location location=geolocationService.getLocation(ipAddress);
            RealtimeWeather realtimeWeather=realtimeWeatherService.findByLocation(location);
            RealtimeWeatherDTO dto= mapper.map(realtimeWeather, RealtimeWeatherDTO.class);
            return ResponseEntity.ok(dto);
        } catch (GeolocationException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable(name = "locationCode") String locationCode){

            RealtimeWeather realtime=realtimeWeatherService.getByLocationCode(locationCode);
            RealtimeWeatherDTO dto=mapper.map(realtime, RealtimeWeatherDTO.class);
            return ResponseEntity.ok(dto);

    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealtimeWeather(@PathVariable("locationCode") String locationCode, @RequestBody @Valid RealtimeWeather realtimeWeather) throws LocationNotFoundException {
        realtimeWeather.setLocationCode(locationCode);

            RealtimeWeather updatedRealtimeWeather=realtimeWeatherService.update(locationCode, realtimeWeather);
            RealtimeWeatherDTO dto=mapper.map(updatedRealtimeWeather, RealtimeWeatherDTO.class);
            return ResponseEntity.ok(dto);

    }
}
