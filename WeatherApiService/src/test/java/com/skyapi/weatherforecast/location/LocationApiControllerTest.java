package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
	
	@Test
	public void addLocationShouldReturn201Created() throws Exception {
		Location location =new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setCountryName("America");
		location.setEnabled(true);
		location.setRegionName("New York");
		
		Mockito.when(service.save(location)).thenReturn(location);
		String bodyContent=mapper.writeValueAsString(location);
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
		       .andExpect(status().isCreated())
		       .andExpect(jsonPath("$.city_name", is("New York City")))
		       .andDo(print());
	}
	
	@Test
	public void testListLocationsShouldReturnNoContent() throws Exception {
		Mockito.when(service.list()).thenReturn(Collections.emptyList());
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent());
	}
	
	@Test
	public void testListLocationsShouldReturnStatus200isOk() throws Exception {
		List<Location> locations=service.list();
		Mockito.when(service.list()).thenReturn(locations);
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk());
	}
	
	@Test
	public void testGetLocation() throws Exception {
		Location location =new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setCountryName("America");
		location.setEnabled(true);
		location.setRegionName("New York");
		
		Mockito.when(service.get("NYC_USA")).thenReturn(location);
		mockMvc.perform(get(END_POINT_PATH+"/NYC_USA")).andExpect(status().isOk()).andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturnNotFound() throws Exception {
		Location location =new Location();
		location.setCode("ABCD");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setCountryName("America");
		location.setEnabled(true);
		location.setRegionName("New York");
		Mockito.when(service.update(location)).thenThrow(new LocationNotFoundException("Not found location"));
		String locationAsString=mapper.writeValueAsString(location);
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(locationAsString))
		       .andExpect(status().isNotFound())
		       .andDo(print());
	}
	
	@Test
	public void testUpdateShouldReturnBadRequest() throws Exception {
		Location location =new Location();
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setCountryName("America");
		location.setEnabled(true);
		location.setRegionName("New York");
		String locationAsString=mapper.writeValueAsString(location);
		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(locationAsString))
		       .andExpect(status().isBadRequest())
		       .andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception {
		Mockito.doThrow(LocationNotFoundException.class).when(service).delete("DELHI_IN");;
		mockMvc.perform(delete(END_POINT_PATH+"/DELHI_IN")).andExpect(status().isNotFound()).andDo(print());
	}
	
	@Test
	public void testDeleteShouldReturn204NoContent() throws Exception {
		Mockito.doNothing().when(service).delete("DELHI_IN");
		mockMvc.perform(delete(END_POINT_PATH+"/DELHI_IN"))
		       .andExpect(status().isNoContent())
		       .andDo(print());
	}

	@Test
	public void testValidateRequestBodyLocationCode() throws Exception {
		Location location =new Location();
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setCountryName("America");
		location.setEnabled(true);
		location.setRegionName("New York");
		String locationAsString=mapper.writeValueAsString(location);
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(locationAsString))
				.andExpect(status().isBadRequest())
				.andExpect((ResultMatcher) content().contentType("application/json"))
				.andExpect(jsonPath("$.errors", is("Location code cannot be null")))
				.andDo(print());
	}

	@Test
	public void testValidateRequestBodyAllInvalid() throws Exception {
		Location location=new Location();
		String bodyContent=mapper.writeValueAsString(location);
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
				.andExpect(status().isBadRequest())
				//.andExpect(content().contentType("application/json"))
				.andDo(print());
	}

	@Test
	public void testDeleteShouldReturn404() throws Exception {
		String code="LACA_USA";
		String requestURI=END_POINT_PATH+"/"+code;
		LocationNotFoundException ex=new LocationNotFoundException(code);
		Mockito.doThrow(ex).when(service).delete(code);
		mockMvc.perform(delete(requestURI))
				.andExpect(status().isNotFound());
	}



}
