package com.skyapi.weatherforecast.daily;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.DailyWeatherId;
import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class DailyWeatherRepositoryTests {
	@Autowired
	private DailyWeatherRepository repo;
	
	@Test
	public void testAdd() {
		String locationCode="MBMH_IN";
		Location location=new Location().code(locationCode);
		DailyWeather dailyWeather=new DailyWeather().location(location)
                .dayOfMonth(10)
                .month(7)
                .minTemp(20)
                .maxTemp(32)
                .precipitation(20)
                .status("Sunny");
		DailyWeather saved=repo.save(dailyWeather);
		assertThat(saved.getId().getLocation().getCode()).isEqualTo(locationCode);
	}
	
	@Test
	public void testDelete() {
		String locationCode="MBMH_IN";
		Location location=new Location().code(locationCode);
		DailyWeatherId id=new DailyWeatherId(10, 7, location);
		repo.deleteById(id);
		Optional<DailyWeather> dw=repo.findById(id);
		assertThat(dw).isNotPresent();
	}
	
	@Test
	public void testGetLocationCode() {
		String locationCode="MBMH_IN";
        List<DailyWeather> ls=repo.findByLocationCode(locationCode);
        assertThat(ls).isNotEmpty();
	}

}
