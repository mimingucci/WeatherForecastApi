package com.skyapi.weatherforecast.full;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationService;

public class FullWeatherApiControllerTests {

	private static String END_POINT_PATH="/v1/full";
	private static final String REQUEST_CONTENT_TYPE="application/json";
	private static final String RESPONSE_CONTENT_TYPE="application/json";
	
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	@MockBean private FullWeatherService weatherService;
	@MockBean private GeolocationService locationService;
}
