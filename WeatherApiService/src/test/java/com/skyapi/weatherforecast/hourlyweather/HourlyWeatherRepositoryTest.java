package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourly.HourlyWeatherRepository;
import com.skyapi.weatherforecast.location.LocationRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.ClassBasedNavigableIterableAssert.assertThat;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class HourlyWeatherRepositoryTest {
    @Autowired
    private HourlyWeatherRepository repo;
    
    @Autowired
    private LocationRepository repository;

    @Test
    public void testAdd(){
        Location location=repository.findById("MBMH_IN").get();
        List<HourlyWeather> lists=location.getListHourlyWeather();
        HourlyWeather forecast1=new HourlyWeather()
        		.location(location)
                .hourOfDay(11)
                .status("Rainy")
                .temperature(15)
                .precipitation(70);
        HourlyWeather forecast2=new HourlyWeather()
        		.location(location)
                .hourOfDay(12)
                .status("Cloudy")
                .temperature(20)
                .precipitation(70);
        lists.add(forecast1);
        lists.add(forecast2);
        Location savedLocation=repository.save(location);
        assert(!savedLocation.getListHourlyWeather().isEmpty());
//        HourlyWeather updatedHourlyWeather=repo.save(forecast1);
//        assertThat(updatedHourlyWeather.getId().getLocation().getCode()).isEqualTo(locatio);
//        assertThat(updatedHourlyWeather.getId().getHourOfDay()).isEqualTo(new Integer(11));
    }
    
    @Test
    public void testDelete() {
    	Location location=new Location().code("MBMH_IN");
    	HourlyWeatherId hourlyWeather=new HourlyWeatherId(11, location);
    	repo.deleteById(hourlyWeather);
    	Optional<HourlyWeather> res=repo.findById(hourlyWeather);
    	assert(!res.isPresent());
    }
}
