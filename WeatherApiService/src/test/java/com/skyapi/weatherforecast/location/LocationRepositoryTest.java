package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class LocationRepositoryTest {

	@Autowired
	private LocationRepository repo;
	
	@Test
	public void addLocation() {
		Location location=new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setCountryCode("US");
		location.setCountryName("America");
		location.setEnabled(true);
		location.setRegionName("New York");
		
		Location addedLocation=repo.save(location);
		assertThat(addedLocation).isNotNull();
	}
	
	@Test
	public void findUntrashed() {
		List<Location> unstrashedLocations=repo.findUntrashed();
		assertThat(unstrashedLocations).isNotEmpty();
		unstrashedLocations.forEach(System.out::print);
	}
	
	@Test
	public void testGetNotFound() {
		Location location=repo.findByCode("ABCD");
		assertThat(location).isNull();
	}
	
	@Test
	public void testGetShouldFound() {
		Location location=repo.findByCode("DELHI_IN");
		assertThat(location).isNotNull();
	}
	
	@Test
	public void testTrash() {
		repo.trashByCode("NYC_USA");
		Location location=repo.findByCode("NYC_USA");
		assertThat(location).isNull();
	}
}
