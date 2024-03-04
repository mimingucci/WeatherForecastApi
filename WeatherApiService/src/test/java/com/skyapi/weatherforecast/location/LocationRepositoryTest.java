package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
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
		location.setCode("MBMH_IN");
		location.setCityName("Mumbai");
		location.setCountryCode("IN");
		location.setCountryName("India");
		location.setEnabled(true);
		location.setRegionName("Maharashtra");
		
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

	@Test
	public void findByCountryCodeAndCityNameNotFound(){
		String countryCode="HN";
		String cityName="city";
		Location location=repo.findByCountryCodeAndCityName(countryCode, cityName);
		assertThat(location).isNull();
	}

	@Test
	public void findByCountryCodeAndCityNameShouldBeFound(){
		String countryCode="IN";
		String cityName="Delhi";
		Location location=repo.findByCountryCodeAndCityName(countryCode, cityName);
		assertThat(location).isNotNull();
	}

	@Test
	public void testAddHourlyWeatherData(){
		Location location=repo.findById("MBMH_IN").get();
		List<HourlyWeather> hourlyWeathers=location.getListHourlyWeather();
		HourlyWeather forecast1=new HourlyWeather().id(location, 9)
				.temperature(40)
				.status("Sunny")
				.precipitation(40);
		hourlyWeathers.add(forecast1);
		Location updatedLocation=repo.save(location);
		assertThat(updatedLocation.getListHourlyWeather()).isNotEmpty();
	}
	
	@Test
	public void testAddDailyWeather() {
		Location location=repo.findById("MBMH_IN").get();
		List<DailyWeather> lisDailyWeathers=location.getListDailyWeather();
		DailyWeather dailyWeather=new DailyWeather().location(location)
				                                    .dayOfMonth(10)
				                                    .month(7)
				                                    .minTemp(20)
				                                    .maxTemp(32)
				                                    .precipitation(20)
				                                    .status("Sunny");
		lisDailyWeathers.add(dailyWeather);
		Location savedLocation=repo.save(location);
		assertThat(savedLocation.getListDailyWeather()).isNotEmpty();
	}

}
