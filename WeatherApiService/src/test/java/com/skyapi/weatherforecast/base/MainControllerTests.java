package com.skyapi.weatherforecast.base;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@AutoConfigureMockMvc
@SpringBootTest
public class MainControllerTests {

	private static final String BASE_URI="/";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testBaseURI() throws Exception{
		mockMvc.perform(get(BASE_URI))
		       .andExpect(status().isOk())
		       .andExpect((ResultMatcher) content().contentType("application/json"))
		       .andExpect((ResultMatcher) jsonPath("$.locations_url", is("http://localhost/v1/locations")))
		       .andDo(print());
	}
}
