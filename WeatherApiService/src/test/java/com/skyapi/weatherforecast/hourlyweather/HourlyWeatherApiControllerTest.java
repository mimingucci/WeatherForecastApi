package com.skyapi.weatherforecast.hourlyweather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.hourly.HourlyWeatherApiController;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.skyapi.weatherforecast.location.LocationApiController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.apache.logging.log4j.ThreadContext.put;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(HourlyWeatherApiControllerTest.class)
public class HourlyWeatherApiControllerTest {
    public static final String URI_PATH="/v1/hourly";
    @Autowired
    public MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testUpdateShouldReturn400BadRequest() throws Exception {
        String path=URI_PATH+"/NYC_USA";
        List<HourlyWeatherDTO> listDTO= Collections.emptyList();
        String requestBody=mapper.writeValueAsString(listDTO);
        mockMvc.perform(put(path).contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest());
    }

}
