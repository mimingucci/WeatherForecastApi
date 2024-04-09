package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(value = false)
@AutoConfigureMockMvc
@SpringBootTest
public class HourlyWeatherApiControllerTest {
    public static final String END_POINT_PATH="/v1/hourly";
    @Autowired
    public MockMvc mockMvc;
    
    @MockBean
    private HourlyWeatherService hourlyWeatherService;
    	
    @MockBean
    private GeolocationService locationService;
    
    @Test
    public void testGetByIPShouldReturn400BadRequest() throws Exception {
    	mockMvc.perform(get(END_POINT_PATH))
    	       .andExpect(status().isBadRequest())
    	       .andDo(print());
    }
    
    @Test
    public void testGetByIpShouldReturn400BadRequestBecauseGeoLocationException() throws Exception {
    	Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);
    	mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour", "g"))
    	.andExpect(status().isBadRequest())
    	.andDo(print());
    }
    
    
    @Test
    public void testGetByIpShouldReturn204NoContent() throws Exception {
    	Location location=new Location().code("DELHI_IN");
    	Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
    	Mockito.when(hourlyWeatherService.getByLocation(location, 9)).thenReturn(new ArrayList<>());
    	mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour", String.valueOf(9)))
    	.andExpect(status().isNoContent())
    	.andDo(print());
    }
    
    
    @Test
    public void testGetByIpShouldReturn200OK() throws Exception {
    	int currentHour=9;
    	Location location=new Location().code("NYC_USA");
    	location.setCityName("New York City");
    	location.setRegionName("New York");
    	location.setCountryCode("US");
    	location.setCountryName("United States of America");
    	
    	HourlyWeather hourlyWeather1=new HourlyWeather()
    			.location(location)
    			.hourOfDay(1)
    			.temperature(20)
    			.status("Sunny")
    			.precipitation(50);
    	
    	HourlyWeather hourlyWeather2=new HourlyWeather()
    			.location(location)
    			.hourOfDay(2)
    			.temperature(18)
    			.status("Sunny")
    			.precipitation(60);
    	
    	String expectedLocation=location.toString();
    	
    	Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
    	when(hourlyWeatherService.getByLocation(location, 9)).thenReturn(List.of(hourlyWeather1, hourlyWeather2));
    	mockMvc.perform(get(END_POINT_PATH).header("X-Current-Hour", String.valueOf(9)))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.location", is(expectedLocation)))
    	.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(1)))
    	.andDo(print());
    }
    
//    @Test
//    public void testUpdateShouldReturn400BadRequest() throws Exception {
//        String path=END_POINT_PATH+"/NYC_USA";
//        List<HourlyWeatherDTO> listDTO= Collections.emptyList();
//        String requestBody=mapper.writeValueAsString(listDTO);
//        mockMvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(requestBody))
//                .andExpect((ResultMatcher) status(HttpStatus.BAD_REQUEST))
//                .andDo(print());
//    }
//
//    @Test
//    public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
//        String path=URI_PATH+"/NYC_USA";
//        HourlyWeatherDTO dto1=new HourlyWeatherDTO()
//                .hourOfDay(10)
//                .precipitation(133)
//                .temperature(700)
//                .status("Cloudy");
//        HourlyWeatherDTO dto2=new HourlyWeatherDTO()
//                .hourOfDay(-1)
//                .precipitation(60)
//                .temperature(150)
//                .status("");
//        List<HourlyWeatherDTO> listDTO=List.of(dto1, dto2);
//        String requestBody=mapper.writeValueAsString(listDTO);
//        mockMvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(requestBody))
//                .andExpect((ResultMatcher) badRequest())
//                .andDo(print());
//    }
}
