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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Collections;
import java.util.List;

import static org.apache.logging.log4j.ThreadContext.put;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(value = false)
@AutoConfigureMockMvc
@SpringBootTest
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
        mockMvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect((ResultMatcher) status(HttpStatus.BAD_REQUEST))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String path=URI_PATH+"/NYC_USA";
        HourlyWeatherDTO dto1=new HourlyWeatherDTO()
                .hourOfDay(10)
                .precipitation(133)
                .temperature(700)
                .status("Cloudy");
        HourlyWeatherDTO dto2=new HourlyWeatherDTO()
                .hourOfDay(-1)
                .precipitation(60)
                .temperature(150)
                .status("");
        List<HourlyWeatherDTO> listDTO=List.of(dto1, dto2);
        String requestBody=mapper.writeValueAsString(listDTO);
        mockMvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect((ResultMatcher) badRequest())
                .andDo(print());
    }
}
