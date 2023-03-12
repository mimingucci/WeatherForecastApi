package com.skyapi.weatherforecast.realtime;

import com.skyapi.weatherforecast.common.RealtimeWeather;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static java.lang.constant.ConstantDescs.NULL;
import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RealtimeWeatherRepositoryTest {
    @Autowired
    private RealtimeWeatherRepository repo;

    @Test
    public void testFindRealtimeNotFound(){
        String countryCode="JP";
        String cityName="Tokyo";
        RealtimeWeather realtime=repo.findByCountryCodeAndCity(countryCode, cityName);
        System.out.println(realtime);
        assert realtime.equals(NULL);
    }
}
