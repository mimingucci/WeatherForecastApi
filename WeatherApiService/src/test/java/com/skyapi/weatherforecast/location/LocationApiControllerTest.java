package com.skyapi.weatherforecast.location;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.Location;

@WebMvcTest(LocationApiController.class)
public class LocationApiControllerTest {
	private static final String END_POINT_PATH="/v1/locations";

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private LocationService service;
	
	@Test
	public void addLocationShouldReturn400BadRequest() throws Exception {
		Location location=new Location();
		String mappedLocation=mapper.writeValueAsString(location);
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(mappedLocation))
		       .andExpect(status().isBadRequest())
		       .andDo(print());
	}
}
