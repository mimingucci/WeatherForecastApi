package com.skyapi.weatherforecast.hourlyweather;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourly.HourlyWeatherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

//import static org.assertj.core.api.ClassBasedNavigableIterableAssert.assertThat;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class HourlyWeatherRepositoryTest {
    @Autowired
    private HourlyWeatherRepository repo;

    @Test
    public void testAdd(){
        String locatio="MBMH_IN";
        Location location=new Location().code(locatio);
        HourlyWeather forecast1=new HourlyWeather().location(location)
                .hourOfDay(11)
                .status("Rainy")
                .temperature(15)
                .precipitation(70);
        HourlyWeather updatedHourlyWeather=repo.save(forecast1);
//        assertThat(updatedHourlyWeather.getId().getLocation().getCode()).isEqualTo(locatio);
//        assertThat(updatedHourlyWeather.getId().getHourOfDay()).isEqualTo(new Integer(11));
    }
}
