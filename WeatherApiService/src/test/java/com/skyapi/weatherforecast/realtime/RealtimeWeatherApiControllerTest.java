package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
@AutoConfigureMockMvc
@SpringBootTest
public class RealtimeWeatherApiControllerTest {
    private static final String END_POINT_PATH="/v1/realtime";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private GeolocationService geolocationService;

    @MockBean
    private RealtimeWeatherService realtimeWeatherService;

    @Test
    public void testGetShouldReturn400Status() throws Exception {
        Location location=new Location();
        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetShouldReturn404Status() throws Exception {
        Location location=new Location();
        Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        Mockito.when(realtimeWeatherService.findByLocation(location)).thenThrow(LocationNotFoundException.class);
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    
    @Test
    public void testGetShouldReturnStatus200OK() throws Exception {
    	Location location=new Location();
    	location.code("HCM_VN");
    	location.setCityName("Ho Chi Minh");
    	location.setCountryCode("VN");
    	location.setCountryName("Viet Nam");
    	
    	RealtimeWeather realtime=new RealtimeWeather();
    	realtime.setTemperature(40);
    	realtime.setHumidity(32);
    	realtime.setPrecipitation(40);
    	realtime.setLastUpdated(new Date());
    	realtime.setStatus("Sunny");
    	realtime.setWindSpeed(5);
    	
    	realtime.setLocation(location);
    	location.setRealtimeWeather(realtime);
    	
    	Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
    	Mockito.when(realtimeWeatherService.findByLocation(location)).thenReturn(realtime);
    	
    	mockMvc.perform(get(END_POINT_PATH))
    	.andExpect(status().isOk())
    	.andExpect((ResultMatcher) jsonPath("$._links.self.href", is("http://localhost/v1/realtime")))
    	.andDo(print());
    }
    
    
    @Test
    public void testGetByLocationShouldReturnStatus200OK() throws Exception {
    	String locationCode="HCM_VN";
    	String requestURI=END_POINT_PATH+"/"+locationCode;
    	Location location=new Location();
    	location.code("HCM_VN");
    	location.setCityName("Ho Chi Minh");
    	location.setCountryCode("VN");
    	location.setCountryName("Viet Nam");
    	
    	RealtimeWeather realtime=new RealtimeWeather();
    	realtime.setTemperature(40);
    	realtime.setHumidity(32);
    	realtime.setPrecipitation(40);
    	realtime.setLastUpdated(new Date());
    	realtime.setStatus("Sunny");
    	realtime.setWindSpeed(5);
    	
    	realtime.setLocation(location);
    	location.setRealtimeWeather(realtime);
    	
    	Mockito.when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
    	Mockito.when(realtimeWeatherService.getByLocationCode(locationCode)).thenReturn(realtime);
    	
    	mockMvc.perform(get(requestURI))
    	.andExpect(status().isOk())
    	.andExpect((ResultMatcher) jsonPath("$._links.self.href", is("http://localhost/v1/realtime/"+locationCode)))
    	.andDo(print());
    }
    
    @Test
    public void testUpdateShouldReturnStatus200OK() throws Exception {
    	String locationCode="HCM_VN";
    	String requestURI=END_POINT_PATH+"/"+locationCode;
    	Location location=new Location();
    	location.setCode("HCM_VN");
    	location.setCityName("Ho Chi Minh");
    	location.setCountryCode("VN");
    	location.setCountryName("Viet Nam");
    	
    	RealtimeWeather realtime=new RealtimeWeather();
    	realtime.setTemperature(40);
    	realtime.setHumidity(32);
    	realtime.setPrecipitation(40);
    	realtime.setLastUpdated(new Date());
    	realtime.setStatus("Sunny");
    	realtime.setWindSpeed(5);
    	realtime.setLocationCode(locationCode);
    	
    	realtime.setLocation(location);
    	location.setRealtimeWeather(realtime);
    	
    	RealtimeWeatherDTO dto=new RealtimeWeatherDTO();
    	dto.setTemperature(40);
    	dto.setHumidity(32);
    	dto.setPrecipitation(40);
    	dto.setStatus("Sunny");
    	dto.setWindSpeed(5);
    	
    	String bodyContent=mapper.writeValueAsString(dto);
    	Mockito.when(realtimeWeatherService.update(locationCode, realtime)).thenReturn(realtime);
    	
    	mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
    	.andExpect(status().isOk())
    	.andExpect((ResultMatcher) jsonPath("$._links.self.href", is("http://localhost/v1/realtime/"+locationCode)))
    	.andDo(print());
    }
}
